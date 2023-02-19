package koreatech.in.service;

import static koreatech.in.domain.DomainToMap.domainToMapWithExcept;
import static koreatech.in.exception.ExceptionInformation.NICKNAME_DUPLICATE;
import static koreatech.in.exception.ExceptionInformation.PASSWORD_DIFFERENT;
import static koreatech.in.exception.ExceptionInformation.USER_NOT_FOUND;

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
import koreatech.in.dto.normal.user.response.UserResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.exception.ForbiddenException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.exception.ValidationException;
import koreatech.in.mapstruct.UserConverter;
import koreatech.in.repository.AuthorityMapper;
import koreatech.in.repository.user.OwnerMapper;
import koreatech.in.repository.user.StudentMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.DateUtil;
import koreatech.in.util.JwtTokenGenerator;
import koreatech.in.util.SHA256Util;
import koreatech.in.util.SesMailSender;
import koreatech.in.util.SlackNotiSender;
import koreatech.in.util.StringRedisUtilStr;
import org.apache.velocity.app.VelocityEngine;
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
    @Transactional(readOnly = true)
    public void logout() {
        User user = jwtValidator.validate();
        deleteAccessTokenFromRedis(user.getId());
    }

    @Transactional
    @Override
    public Map<String, Object> StudentRegister(StudentRegisterRequest request, String host) {
        Student student = request.toEntity(UserCode.UserIdentity.STUDENT.getIdentityType());

        EmailAddress studentEmail = EmailAddress.from(student.getEmail());
        studentEmail.validatePortalEmail();

        checkInputDataDuplicationAndValidation(student);
        String anonymousNickname = "익명_" + (System.currentTimeMillis());
        student.setEmail(studentEmail.getEmailAddress());
        student.setAnonymous_nickname(anonymousNickname);
        Date authExpiredAt = DateUtil.addHoursToJavaUtilDate(new Date(), 1);
        String authToken = SHA256Util.getEncrypt(student.getEmail(), authExpiredAt.toString());
        student.changeAuthTokenAndExpiredAt(authToken, authExpiredAt);
        String encodedPassword = passwordEncoder.encode(student.getPassword());
        student.changePassword(encodedPassword);

        try {
            insertStudentToDB(student);
        } catch (SQLException sqlException) {
            throw new ConflictException(new ErrorMessage("invalid authenticate", 0));
        }

        sendAuthTokenByEmailForAuthenticate(authToken, host, student.getEmail());
        slackNotiSender.noticeEmailVerification(student);

        return new HashMap<String, Object>() {{
            put("success", "send mail for student authentication to entered email address");
        }};
    }

    @Override
    public StudentResponse getStudent() {
        Integer userId = jwtValidator.validate().getId();
        Student student = studentMapper.getStudentById(userId);

        if(student == null){
            throw new BaseException(USER_NOT_FOUND);
        }

        return downcastFor(UserConverter.INSTANCE.toUserResponse(student));
    }

    private static StudentResponse downcastFor(UserResponse userResponse) {
        if (!(userResponse instanceof StudentResponse)) {
            throw new ClassCastException("UserConverter에서 UserResponse -> StudentResponse 로 변환 과정 중 잘못된 다운캐스팅이 발생했습니다.");
        }
        return (StudentResponse) userResponse;
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
        checkNicknameDuplicationWithoutSameUser(student);
        if (student.getStudent_number() != null) {
            checkStudentNumberValidation(student);
        }
        if (student.getMajor() != null) {
            checkMajorValidation(student);
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

        userMapper.deleteUser(user);
        deleteAccessTokenFromRedis(user.getId());

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

    private void checkInputDataDuplicationAndValidation(Student student){
        validateEmailUniqueness(EmailAddress.from(student.getEmail()));
        checkNicknameDuplicationWithoutSameUser(student);
        checkStudentNumberValidation(student);
        checkMajorValidation(student);
    }

    private void checkNicknameDuplicationWithoutSameUser(Student student) {
        if (student.getNickname() != null) {
            User selectUser = userMapper.getUserByNickname(student.getNickname());
            if (selectUser != null && !student.equals(selectUser)) {
                throw new ConflictException(new ErrorMessage("nickname duplicate", 1));
            }
        }
    }

    private void checkMajorValidation(Student student) {
        if (student.getMajor() != null) {
            if (!student.isMajorValidated()) {
                throw new PreconditionFailedException(new ErrorMessage("invalid dept code", 3));
            }
        }
    }

    private void checkStudentNumberValidation(Student student) {
        if (student.getStudent_number() != null) {
            if (!student.isStudentNumberValidated()) {
                throw new PreconditionFailedException(new ErrorMessage("invalid student number", 2));
            }
        }
    }

    private void sendAuthTokenByEmailForAuthenticate(String authToken, String contextPath, String email){
        Map<String, Object> model = new HashMap<>();
        model.put("authToken", authToken);
        model.put("contextPath", contextPath);

        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mail/register_authenticate.vm", "UTF-8", model);

        sesMailSender.sendMail("no-reply@bcsdlab.com", email, "코인 이메일 회원가입 인증", text);
    }

    private void insertStudentToDB(Student student) throws SQLException{
        userMapper.insertUser(student);
        studentMapper.insertStudent(student);
    }

    private void sendResetTokenByEmailForFindPassword(String resetToken, String contextPath, String email) {
        Map<String, Object> model = new HashMap<>();
        model.put("resetToken", resetToken);
        model.put("contextPath", contextPath);

        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mail/change_password.vm", "UTF-8", model);

        sesMailSender.sendMail("no-reply@bcsdlab.com", email, "코인 패스워드 초기화 인증", text);
    }

    private boolean isTokenExpired(Date expiredAt) {
        return expiredAt.getTime() - (new Date()).getTime() < 0;
    }

    private void deleteAccessTokenFromRedis(Integer userId) {
        stringRedisUtilStr.deleteData(redisLoginTokenKeyPrefix + userId.toString());
    }

    private void validateEmailUniqueness(EmailAddress emailAddress) {
        if(userMapper.isEmailAlreadyExist(emailAddress).equals(true)) {
            throw new BaseException(ExceptionInformation.EMAIL_DUPLICATED);
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
