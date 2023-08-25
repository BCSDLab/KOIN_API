package koreatech.in.service;

import static koreatech.in.domain.DomainToMap.domainToMapWithExcept;
import static koreatech.in.exception.ExceptionInformation.FORBIDDEN;
import static koreatech.in.exception.ExceptionInformation.INQUIRED_USER_NOT_FOUND;
import static koreatech.in.exception.ExceptionInformation.NICKNAME_DUPLICATE;
import static koreatech.in.exception.ExceptionInformation.PASSWORD_DIFFERENT;
import static koreatech.in.exception.ExceptionInformation.USER_NOT_FOUND;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import koreatech.in.domain.Auth.LoginResult;
import koreatech.in.domain.Auth.RefreshResult;
import koreatech.in.domain.Auth.RefreshToken;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.User.AuthResult;
import koreatech.in.domain.User.AuthToken;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserResponseType;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.normal.auth.TokenRefreshRequest;
import koreatech.in.dto.normal.auth.TokenRefreshResponse;
import koreatech.in.dto.normal.user.request.AuthTokenRequest;
import koreatech.in.dto.normal.user.request.CheckExistsEmailRequest;
import koreatech.in.dto.normal.user.request.FindPasswordRequest;
import koreatech.in.dto.normal.user.request.LoginRequest;
import koreatech.in.dto.normal.user.response.AuthResponse;
import koreatech.in.dto.normal.user.response.LoginResponse;
import koreatech.in.dto.normal.user.student.request.StudentRegisterRequest;
import koreatech.in.dto.normal.user.student.request.StudentUpdateRequest;
import koreatech.in.dto.normal.user.student.response.StudentResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.exception.ForbiddenException;
import koreatech.in.mapstruct.UserConverter;
import koreatech.in.mapstruct.normal.auto.AuthConverter;
import koreatech.in.repository.AuthenticationMapper;
import koreatech.in.repository.user.OwnerMapper;
import koreatech.in.repository.user.StudentMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.SesMailSender;
import koreatech.in.util.SlackNotiSender;
import koreatech.in.util.jwt.UserAccessJwtGenerator;
import koreatech.in.util.jwt.UserRefreshJwtGenerator;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    private static final String CHANGE_PASSWORD_FORM_LOCATION = "mail/change_password_certificate_button.vm";

    public static final String MAIL_REGISTER_AUTHENTICATE_FORM_LOCATION = "mail/register_authenticate.vm";
    public static final String AUTH_TOKEN = "authToken";
    public static final String CONTEXT_PATH = "contextPath";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SesMailSender sesMailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private UserAccessJwtGenerator userAccessJwtGenerator;

    @Autowired
    private UserRefreshJwtGenerator userRefreshJwtGenerator;

    @Autowired
    private SlackNotiSender slackNotiSender;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private AuthenticationMapper authenticationMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) throws Exception {
        User user = getUserByEmail(request.getEmail());

        checkPasswordEquals(request.getPassword(), user.getPassword());
        checkAuthenticationStatus(user);

        user.updateLastLoginTimeToCurrent();
        userMapper.updateUser(user); // TODO: (김주원) 유저 신원에 따라 owners 또는 student 테이블 데이터도 update 할것인지 결정

        LoginResult loginResult = LoginResult
                .builder()
                .accessToken(generateAccessToken(user.getId()))
                .refreshToken(getRefreshToken(user.getId()))
                .userType(user.getUser_type().name())
                .build();

        return AuthConverter.INSTANCE.toLoginResponse(loginResult);
    }

    private String generateAccessToken(Integer userId) {
        return userAccessJwtGenerator.generateToken(userId);
    }

    private String getRefreshToken(Integer userId) throws IOException {
        String refreshToken = authenticationMapper.getRefreshToken(userId);
        if (isExpired(refreshToken)) {
            return generateRefreshToken(userId);
        }

        return refreshToken;
    }

    private String generateRefreshToken(Integer userId) {
        String newRefreshToken = userRefreshJwtGenerator.generateToken(userId);
        authenticationMapper.setRefreshToken(newRefreshToken, userId);

        return newRefreshToken;
    }

    private void checkAuthenticationStatus(User user) {
        if (!user.isAuthenticated()) {
            throw new BaseException(FORBIDDEN);
        }
    }

    private void checkPasswordEquals(String requestedPassword, String encodedPassword) {
        if (!passwordEncoder.matches(requestedPassword, encodedPassword)) {
            throw new BaseException(PASSWORD_DIFFERENT);
        }
    }

    private boolean isExpired(String refreshToken) {
        return (refreshToken == null || userRefreshJwtGenerator.isExpired(refreshToken));
    }

    @Override
    @Transactional(readOnly = true)
    public void logout() {
        User user = jwtValidator.validate();

        deleteRefreshTokenInDB(user.getId());
    }

    private void deleteRefreshTokenInDB(Integer userId) {
        authenticationMapper.deleteRefreshToken(userId);
    }

    @Override
    public void StudentRegister(StudentRegisterRequest request, String host) {
        Student student = UserConverter.INSTANCE.toStudent(request);

        validateInRegister(student);

        enrichInRegisterFor(student);

        createInDBFor(student);

        sendAuthTokenByEmailForAuthenticate(student.getAuth_token(), host, EmailAddress.from(student.getEmail()));

        slackNotiSender.noticeEmailVerification(student);
    }

    private void enrichInRegisterFor(Student student) {
        student.fillAnonymousNickname();
        student.fillAuthTokenAndTokenExpiredAt();
        encodePasswordFor(student);
    }

    private void encodePasswordFor(Student student) {
        if (student.getPassword() == null) {
            return;
        }

        student.changePassword(passwordEncoder.encode(student.getPassword()));
    }

    @Override
    public StudentResponse getStudent() {
        Integer userId = jwtValidator.validate().getId();
        Student student = studentMapper.getStudentById(userId);

        if (student == null) {
            throw new BaseException(USER_NOT_FOUND);
        }

        return UserConverter.INSTANCE.toStudentResponse(student);
    }

    @Override
    public StudentResponse updateStudent(StudentUpdateRequest studentUpdateRequest) {
        Student student = UserConverter.INSTANCE.toStudent(studentUpdateRequest);
        Student studentInToken = getStudentInToken();

        validateInUpdate(student, studentInToken);
        enrichInUpdateFor(student);

        studentInToken.update(student);
        updateInDBFor(studentInToken);

        return UserConverter.INSTANCE.toStudentResponse(studentInToken);
    }

    private void enrichInUpdateFor(Student student) {
        encodePasswordFor(student);
        student.setIs_authed(true);
    }

    private void updateInDBFor(Student studentInToken) {
        userMapper.updateUser(studentInToken);
        studentMapper.updateStudent(studentInToken);
    }

    private void validateInUpdate(Student student, Student studentInToken) {
        if (student.getNickname() != null && !student.getNickname().equals(studentInToken.getNickname())) {
            validateNicknameUniqueness(student,studentInToken.getId());
        }
        if (student.getStudent_number() != null && !student.getStudent_number().equals(studentInToken.getStudent_number())) {
            validateStudentNumber(student);
        }
        if (student.getMajor() != null && !student.getMajor().equals(studentInToken.getMajor())) {
            validateMajor(student);
        }
    }

    private Student getStudentInToken() {
        User validatedUser = jwtValidator.validate();

        if (!(validatedUser instanceof Student)) {
            throw new BaseException(ExceptionInformation.BAD_ACCESS);
        }

        return (Student) validatedUser;
    }

    // 기획되지 않은 기능이라 사용하지 않음.
    @Override
    @Deprecated
    public Map<String, Object> updateOwnerInformation(Owner owner) throws Exception {
        Owner user_old;
        try {
            user_old = (Owner) jwtValidator.validate();
        } catch (ClassCastException e) {
            throw new ForbiddenException(new ErrorMessage("점주가 아닙니다.", 0));
        }

        // 인증받은 유저인지 체크
        if (!user_old.getIs_authed()) {
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));
        }

        // 닉네임 중복 체크
        if (owner.getNickname() != null) {
            User selectUser = userMapper.getUserByNickname(owner.getNickname());

            if (selectUser != null && !user_old.getId().equals(selectUser.getId())) {
                throw new ConflictException(new ErrorMessage("nickname duplicate", 1));
            }
        }

        // TODO: hashing passowrd
        if (owner.getPassword() != null) {
            owner.setPassword(passwordEncoder.encode(owner.getPassword()));
        }

        user_old.update(owner);
        userMapper.updateUser(user_old);
        ownerMapper.updateOwner(user_old);

        return domainToMapWithExcept(user_old, UserResponseType.getArray(), false);
    }

    @Override
    public void withdraw() {
        User user = jwtValidator.validate();

        userMapper.deleteUser(user); // 회원 관련 테이블에서 해당 회원에 대한 모든 레코드 삭제

        if (user.isOwner()) {
            userMapper.deleteRelationBetweenOwnerAndShop(user.getId());
        }

        deleteRefreshTokenInDB(user.getId());

        slackNotiSender.noticeDelete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkUserNickname(String nickname) {
        if (userMapper.getUserByNickname(nickname) != null) {
            throw new BaseException(NICKNAME_DUPLICATE);
        }
    }

    @Override
    public void changePasswordConfig(FindPasswordRequest request, String host) {
        User user = userMapper.getAuthedUserByEmail(request.getEmail());
        if (user == null) {
            throw new BaseException(INQUIRED_USER_NOT_FOUND);
        }

        user.generateResetTokenForFindPassword();
        userMapper.updateUser(user);

        sendResetTokenByEmailForFindPassword(user.getReset_token(), host, user.getEmail());
    }

    @Override
    public AuthResponse authenticate(AuthTokenRequest authTokenRequest) {

        AuthToken authToken = UserConverter.INSTANCE.toAuthToken(authTokenRequest);
        User user = userMapper.getUserByAuthToken(authToken.getToken());

        AuthResult authResult = AuthResult.from(user);

        if (authResult.isSuccess()) {
            user.enrichForAuthed();
            userMapper.updateUser(user);

            slackNotiSender.noticeRegisterComplete(user);
        }

        return UserConverter.INSTANCE.toAuthResponse(authResult);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean changePasswordInput(String resetToken) {
        User user = userMapper.getAuthedUserByResetToken(resetToken);

        return user != null && user.isAwaitingToFindPassword();
    }

    @Override
    public Boolean changePasswordAuthenticate(String password, String resetToken) {
        User user = userMapper.getAuthedUserByResetToken(resetToken);

        if (user == null || !user.isAwaitingToFindPassword()) {
            return false;
        }

        String encodedPassword = passwordEncoder.encode(password);
        user.changeToNewPassword(encodedPassword);

        userMapper.updateUser(user);

        return true;
    }

    @Override
    public void checkExists(CheckExistsEmailRequest checkExistsEmailRequest) {
        EmailAddress emailAddress = UserConverter.INSTANCE.toEmailAddress(checkExistsEmailRequest);

        if (userMapper.isEmailAlreadyExist(emailAddress).equals(true)) {
            throw new BaseException(ExceptionInformation.EMAIL_DUPLICATED);
        }
    }

    @Override
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        RefreshToken refreshToken = AuthConverter.INSTANCE.toToken(request);

        Integer tokenUserId = userRefreshJwtGenerator.getFromToken(refreshToken.getToken());

        User user = getUserById(tokenUserId);
        user.updateLastLoginTimeToCurrent();
        userMapper.updateUser(user);

        RefreshResult refreshResult = makeRefreshResult(tokenUserId);

        return AuthConverter.INSTANCE.toTokenRefreshResponse(refreshResult);
    }

    private RefreshResult makeRefreshResult(Integer userId) {
        return RefreshResult.builder()
                .accessToken(generateAccessToken(userId))
                .refreshToken(generateRefreshToken(userId))
                .build();
    }

    private User getUserByEmail(String email) {
        return Optional.ofNullable(userMapper.getUserByEmail(email))
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    }

    private User getUserById(int id) {
        return Optional.ofNullable(userMapper.getUserById(id))
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    }

    private void validateInRegister(Student student) {
        EmailAddress studentEmail = EmailAddress.from(student.getEmail());
        studentEmail.validatePortalEmail();

        validateUniqueness(student);
        validateStudentNumber(student);
        validateMajor(student);
    }
    private void validateUniqueness(Student student) {
        validateEmailUniqueness(EmailAddress.from(student.getEmail()));
        validateNicknameUniqueness(student);
    }

    private void validateNicknameUniqueness(Student student) {
        Optional.ofNullable(student.getNickname())
                .filter(nickname -> userMapper.getNicknameUsedCount(nickname, student.getId()) > 0)
                .ifPresent(nickname -> {
                    throw new BaseException(NICKNAME_DUPLICATE);
                });
    }

    private void validateNicknameUniqueness(Student student, Integer userId) {
        Optional.ofNullable(student.getNickname())
                .filter(nickname -> userMapper.getNicknameUsedCount(nickname, userId) > 0)
                .ifPresent(nickname -> {
                    throw new BaseException(NICKNAME_DUPLICATE);
                });
    }

    private void validateMajor(Student student) {
        if (student.getMajor() == null) {
            return;
        }
        if (!student.isMajorValidated()) {
            throw new BaseException(ExceptionInformation.STUDENT_MAJOR_INVALID);
        }
    }

    private void validateStudentNumber(Student student) {
        if (student.getStudent_number() == null) {
            return;
        }
        if (!student.isStudentNumberValidated()) {
            throw new BaseException(ExceptionInformation.STUDENT_NUMBER_INVALID);
        }
    }

    private void sendAuthTokenByEmailForAuthenticate(String authToken, String contextPath, EmailAddress emailAddress) {
        emailAddress.validateSendable();

        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                MAIL_REGISTER_AUTHENTICATE_FORM_LOCATION,
                StandardCharsets.UTF_8.name(),
                makeModelFor(authToken, contextPath));

        sesMailSender.sendMail(
                SesMailSender.COMPANY_NO_REPLY_EMAIL_ADDRESS,
                emailAddress.getEmailAddress(),
                SesMailSender.STUDENT_EMAIL_AUTHENTICATION_SUBJECT,
                text);
    }

    private static Map<String, Object> makeModelFor(String authToken, String contextPath) {
        Map<String, Object> model = new HashMap<>();
        model.put(AUTH_TOKEN, authToken);
        model.put(CONTEXT_PATH, contextPath);
        return model;
    }

    private void createInDBFor(Student student) {
        try {
            userMapper.insertUser(student);
            studentMapper.insertStudent(student);
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    private void sendResetTokenByEmailForFindPassword(String resetToken, String contextPath, String email) {
        Map<String, Object> model = new HashMap<>();
        model.put("resetToken", resetToken);
        model.put(CONTEXT_PATH, contextPath);

        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, CHANGE_PASSWORD_FORM_LOCATION, StandardCharsets.UTF_8.name(), model);

        sesMailSender.sendMail(SesMailSender.COMPANY_NO_REPLY_EMAIL_ADDRESS, email, SesMailSender.FIND_PASSWORD_SUBJECT, text);
    }

    private void validateEmailUniqueness(EmailAddress emailAddress) {
        if (userMapper.isEmailAlreadyExist(emailAddress).equals(true)) {
            throw new BaseException(ExceptionInformation.EMAIL_DUPLICATED);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return null;
    }
}
