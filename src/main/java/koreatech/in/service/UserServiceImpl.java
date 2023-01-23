package koreatech.in.service;

import koreatech.in.dto.normal.user.request.FindPasswordRequest;
import koreatech.in.dto.normal.user.request.LoginRequest;
import koreatech.in.dto.normal.user.request.StudentRegisterRequest;
import koreatech.in.dto.normal.user.request.UpdateUserRequest;
import koreatech.in.dto.normal.user.response.LoginResponse;
import koreatech.in.dto.normal.user.response.StudentResponse;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.NotiSlack;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserCode;
import koreatech.in.domain.User.UserResponseType;
import koreatech.in.domain.User.student.Student;
import koreatech.in.exception.*;
import koreatech.in.repository.AuthorityMapper;
import koreatech.in.repository.user.OwnerMapper;
import koreatech.in.repository.user.StudentMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.*;
import org.apache.commons.lang.StringUtils;
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

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static koreatech.in.domain.DomainToMap.domainToMapWithExcept;
import static koreatech.in.exception.ExceptionInformation.*;

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
        User user = userMapper.getAuthedUserByAccount(request.getAccount());

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
        checkInputDataDuplicationAndValidation(student);

        String email = student.getAccount()+"@koreatech.ac.kr";
        String anonymousNickname = "익명_" + (System.currentTimeMillis());
        student.setEmail(email);
        student.setAnonymous_nickname(anonymousNickname);
        Date authExpiredAt = DateUtil.addHoursToJavaUtilDate(new Date(), 1);
        String authToken = SHA256Util.getEncrypt(student.getAccount(), authExpiredAt.toString());
        student.changeAuthTokenAndExpiredAt(authToken, authExpiredAt);
        String encodedPassword = passwordEncoder.encode(student.getPassword());
        student.changePassword(encodedPassword);

        try {
            insertStudentToDB(student);
        } catch (SQLException sqlException) {
            throw new ConflictException(new ErrorMessage("invalid authenticate", 0));
        }

        sendAuthTokenByEmailForAuthenticate(authToken, host, student.getEmail());

        slackNotiSender.noticeRegister(NotiSlack.builder()
                .color("good")
                .text(student.getAccount() + "님이 이메일 인증을 요청하였습니다.")
                .build());

        return new HashMap<String, Object>() {{
            put("success", "send mail for student authentication to entered email address");
        }};
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

        slackNotiSender.noticeWithdraw(NotiSlack.builder()
                .color("good")
                .text(user.getAccount() + "님이 탈퇴하셨습니다.")
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public void checkUserNickname(String nickname) {
        checkNicknameValid(nickname);
        checkNicknameDuplicated(nickname);
    }

    @Override
    public void changePasswordConfig(FindPasswordRequest request, String host) {
        User user = userMapper.getAuthedUserByAccount(request.getAccount());

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

        slackNotiSender.noticeRegister(
                NotiSlack.builder()
                        .color("good")
                        .text(user.getAccount() + "님이 가입하셨습니다.")
                        .build()
        );

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
        User selectUser = userMapper.getAuthedUserByResetToken(resetToken);

        if ((selectUser == null) || isTokenExpired(selectUser.getReset_expired_at())) {
            return false;
        }

        // TODO: password hashing
        selectUser.setPassword(passwordEncoder.encode(password));
        selectUser.setReset_expired_at(new Date());

        userMapper.updateUser(selectUser);

        return true;
    }

    private void checkInputDataDuplicationAndValidation(Student student){
        checkAccountDuplication(student);
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

    private void checkAccountDuplication(Student student) {
        User selectUser = userMapper.getUserByAccount(student.getAccount());
        if (selectUser != null) {
            if (selectUser.isUserAuthed() || selectUser.isAwaitingEmailAuthentication()) {
                throw new ConflictException(new ErrorMessage("invalid authenticate", 0));
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


    private void checkNicknameValid(String nickname) {
        if (StringUtils.isBlank(nickname)) {
            throw new BaseException("nickname은 null이거나 길이가 0이거나 공백 문자로만 이루어져 있으면 안됩니다.", REQUEST_DATA_INVALID);
        }
        if (nickname.length() > 10) {
            throw new BaseException("nickname은 최대 10자입니다.", REQUEST_DATA_INVALID);
        }
    }

    private void checkNicknameDuplicated(String nickname) {
        User user = userMapper.getUserByNickname(nickname);

        if (user != null && (user.isEmailAuthenticationCompleted() || user.isAwaitingEmailAuthentication())) {
            throw new BaseException(NICKNAME_DUPLICATE);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
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
