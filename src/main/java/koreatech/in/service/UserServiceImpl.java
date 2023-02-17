package koreatech.in.service;

import static koreatech.in.domain.DomainToMap.domainToMapWithExcept;
import static koreatech.in.exception.ExceptionInformation.NICKNAME_DUPLICATE;
import static koreatech.in.exception.ExceptionInformation.NICKNAME_LENGTH_AT_LEAST_1;
import static koreatech.in.exception.ExceptionInformation.NICKNAME_MAXIMUM_LENGTH_IS_10;
import static koreatech.in.exception.ExceptionInformation.NICKNAME_MUST_NOT_BE_BLANK;
import static koreatech.in.exception.ExceptionInformation.NICKNAME_SHOULD_NOT_BE_NULL;
import static koreatech.in.exception.ExceptionInformation.PASSWORD_DIFFERENT;
import static koreatech.in.exception.ExceptionInformation.USER_NOT_FOUND;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserCode;
import koreatech.in.domain.User.UserResponseType;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.normal.user.request.CheckExistsEmailRequest;
import koreatech.in.dto.normal.user.request.FindPasswordRequest;
import koreatech.in.dto.normal.user.request.LoginRequest;
import koreatech.in.dto.normal.user.request.StudentRegisterRequest;
import koreatech.in.dto.normal.user.request.UpdateUserRequest;
import koreatech.in.dto.normal.user.response.LoginResponse;
import koreatech.in.dto.normal.user.response.StudentResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.exception.ForbiddenException;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.ValidationException;
import koreatech.in.mapstruct.UserConverter;
import koreatech.in.repository.AuthorityMapper;
import koreatech.in.repository.user.OwnerMapper;
import koreatech.in.repository.user.StudentMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.JwtTokenGenerator;
import koreatech.in.util.SesMailSender;
import koreatech.in.util.SlackNotiSender;
import koreatech.in.util.StringRedisUtilStr;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public static final String MAIL_REGISTER_AUTHENTICATE_FORM_LOCATION = "mail/register_authenticate.vm";
    public static final String AUTH_TOKEN = "authToken";
    public static final String CONTEXT_PATH = "contextPath";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private SesMailSender sesMailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private SlackNotiSender slackNotiSender;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${redis.key.login_prefix}")
    private String redisLoginTokenKeyPrefix;

    @Override
    public LoginResponse login(LoginRequest request) throws Exception {
        User user = userMapper.getAuthedUserByEmail(request.getEmail());

        // TODO 23.02.15. 박한수 user가 존재하지 않은 경우와 존재하되 is_authed만 false인 경우가 같이 처리됨 -> getUserByEmail (아직 sql문 작성 안됨)으로 유저를 가져와서, 1. null 체크 2. is_authed 체크로 다른 예외로 반환하기.
        if (user == null) {
            throw new BaseException(USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(PASSWORD_DIFFERENT);
        }

        userMapper.updateLastLoggedAt(user.getId(), new Date());

        String accessToken = getAccessTokenFromRedis(user);
        if (isTokenNotExistOrExpired(accessToken)) {
            accessToken = regenerateAccessTokenAndSetRedis(user.getId());
        }

        return LoginResponse.from(accessToken);
    }

    @Override
    public void logout() {
        jwtValidator.validate(); // access token 인증이 잘 되는지 확인
    }

    @Transactional
    @Override
    public Map<String, Object> StudentRegister(StudentRegisterRequest request, String host) {
        Student student = request.toEntity(UserCode.UserIdentity.STUDENT.getIdentityType());

        validateInRegister(student);

        enrichInRegisterFor(student);

        createInDBFor(student);

        sendAuthTokenByEmailForAuthenticate(student.getAuth_token(), host, student.getEmail());

        slackNotiSender.noticeEmailVerification(student);

        return new HashMap<String, Object>() {{
            put("success", "send mail for student authentication to entered email address");
        }};
    }

    private void enrichInRegisterFor(Student student) {
        student.fillAnonymousNickname();
        student.fillAuthTokenAndTokenExpiredAt();
        encodePasswordFor(student);
    }

    private void encodePasswordFor(Student student) {
        student.changePassword(passwordEncoder.encode(student.getPassword()));
    }

    @Override
    public Student getStudent() {
        User user = jwtValidator.validate();

        Student student = studentMapper.getStudentById(user.getId());
        if(student == null){
            throw new NotFoundException(new ErrorMessage("No User", 0));
        }
        return student;
    }

    @Override
    @Transactional
    public StudentResponse updateStudentInformation(UpdateUserRequest request) {
        Student student = request.toEntity();

        Student student_old = studentMapper.getStudentById(jwtValidator.validate().getId());
        if (student_old == null) {
            throw new ValidationException(new ErrorMessage("token not validate", 402));
        }
        student.changeIdentity(student_old.getIdentity());

        if (!student_old.isUserAuthed()) {
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));
        }
        validateNicknameUniqueness(student);
        if (student.getStudent_number() != null) {
            validateStudentNumber(student);
        }
        if (student.getMajor() != null) {
            validateMajor(student);
        }
        if (student.getPassword() != null) {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        }

        student_old.update(student);
        userMapper.updateUser(student_old);
        studentMapper.updateStudent(student_old);

        return new StudentResponse(student_old);
    }

    // TODO owner 정보 업데이트
    // TODO 23.02.12. 박한수 개편 필요.. (사장님 관련 UPDATE는 아직 건드리지 않았음.)
    @Override
    @Transactional
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
    @Transactional
    public void withdraw() {
        User user = jwtValidator.validate();

        userMapper.deleteUserLogicallyById(user.getId());
        deleteAccessTokenFromRedis(user.getId());

        slackNotiSender.noticeDelete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkUserNickname(String nickname) {
        checkNicknameValid(nickname);
        checkNicknameDuplicated(nickname);
    }

    @Override
    public void changePasswordConfig(FindPasswordRequest request, String host) {
        User user = userMapper.getAuthedUserByEmail(request.getEmail());

        if (user == null) {
            throw new BaseException(USER_NOT_FOUND);
        }

        user.generateDataForFindPassword();
        userMapper.updateUser(user);

        sendResetTokenByEmailForFindPassword(user.getReset_token(), host, user.getEmail());
    }

    @Override
    public Boolean authenticate(String authToken) {
        User user = userMapper.getUserByAuthToken(authToken);

        if (user == null || !user.isAwaitingEmailAuthentication()) {
            return false;
        }

        user.changeEmailAuthenticationStatusToComplete();
        userMapper.updateUser(user);

        slackNotiSender.noticeRegisterComplete(user);
        return true;
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

        if(userMapper.isEmailAlreadyExist(emailAddress).equals(true)) {
            throw new BaseException(ExceptionInformation.EMAIL_DUPLICATED);
        }
    }

    private void validateInRegister(Student student){
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
        if(student.getNickname() == null) {
            return;
        }

        if (isExistOtherUser(student, userMapper.getUserByNickname(student.getNickname()))) {
            throw new BaseException(NICKNAME_DUPLICATE);
        }
    }

    private static boolean isExistOtherUser(User registerUser, User selectUser) {
        return selectUser != null && !registerUser.equals(selectUser);
    }

    private void validateMajor(Student student) {
        if(student.getMajor() == null) {
            return;
        }
        if (!student.isMajorValidated()) {
            throw new BaseException(ExceptionInformation.STUDENT_MAJOR_INVALID);
        }
    }

    private void validateStudentNumber(Student student) {
        if(student.getStudent_number() == null) {
            return;
        }
        if (!student.isStudentNumberValidated()) {
            throw new BaseException(ExceptionInformation.STUDENT_NUMBER_INVALID);
        }
    }

    private void sendAuthTokenByEmailForAuthenticate(String authToken, String contextPath, String email){
        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                MAIL_REGISTER_AUTHENTICATE_FORM_LOCATION,
                StandardCharsets.UTF_8.name(),
                makeModelFor(authToken, contextPath));

        sesMailSender.sendMail(
                SesMailSender.COMPANY_NO_REPLY_EMAIL_ADDRESS,
                email,
                SesMailSender.STUDENT_EMAIL_AUTHENTICATION_SUBJECT,
                text);
    }

    @NotNull
    private static Map<String, Object> makeModelFor(String authToken, String contextPath) {
        Map<String, Object> model = new HashMap<>();
        model.put(AUTH_TOKEN, authToken);
        model.put(CONTEXT_PATH, contextPath);
        return model;
    }

    private void createInDBFor(Student student){
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

        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mail/change_password.vm", "UTF-8", model);

        sesMailSender.sendMail("no-reply@bcsdlab.com", email, "코인 패스워드 초기화 인증", text);
    }

    private boolean isTokenExpired(Date expiredAt) {
        return expiredAt.getTime() - (new Date()).getTime() < 0;
    }

    private void deleteAccessTokenFromRedis(Integer userId) {
        stringRedisUtilStr.deleteData(redisLoginTokenKeyPrefix + userId.toString());
    }


    private void checkNicknameValid(String nickname) {
        if (nickname == null) {
            throw new BaseException(NICKNAME_SHOULD_NOT_BE_NULL);
        }
        if (nickname.length() == 0) {
            throw new BaseException(NICKNAME_LENGTH_AT_LEAST_1);
        }
        if (StringUtils.isBlank(nickname)) {
            throw new BaseException(NICKNAME_MUST_NOT_BE_BLANK);
        }
        if (nickname.length() > 10) {
            throw new BaseException(NICKNAME_MAXIMUM_LENGTH_IS_10);
        }
    }

    private void validateEmailUniqueness(EmailAddress emailAddress) {
        if(userMapper.isEmailAlreadyExist(emailAddress).equals(true)) {
            throw new BaseException(ExceptionInformation.EMAIL_DUPLICATED);
        }
    }

    private void checkNicknameDuplicated(String nickname) {
        User user = userMapper.getUserByNickname(nickname);

        if (user != null && (user.isEmailAuthenticationCompleted() || user.isAwaitingEmailAuthentication())) {
            throw new BaseException(NICKNAME_DUPLICATE);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return null;
    }

    private String getAccessTokenFromRedis(User user) throws Exception {
        return stringRedisUtilStr.getDataAsString(redisLoginTokenKeyPrefix + user.getId());
    }

    private boolean isTokenNotExistOrExpired(String getToken) {
        return getToken == null || jwtTokenGenerator.isExpired(getToken);
    }

    private String regenerateAccessTokenAndSetRedis(Integer userId) {
        String accessToken = jwtTokenGenerator.generate(userId);
        stringRedisUtilStr.valOps.set(redisLoginTokenKeyPrefix + userId, accessToken, 72, TimeUnit.HOURS);
        return accessToken;
    }
}
