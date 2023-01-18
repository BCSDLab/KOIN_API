package koreatech.in.service;

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
import org.springframework.util.StringUtils;

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

    @Override
    public void logout() {
        jwtValidator.validate(); // access token 인증이 잘 되는지 확인
    }



    @Transactional
    @Override
    public Map<String, Object> StudentRegister(StudentRegisterRequest request, String host) throws Exception {
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

    private void checkInputDataDuplicationAndValidation(Student student){
        checkAccountDuplication(student);
        checkNicknameDuplicationWithoutSameUser(student);
        checkStudentNumberValidation(student);
        checkMajorValidation(student);
    }

    private void checkNicknameDuplicationWithoutSameUser(Student student) {
        if (student.getNickname() != null) {
            User selectUser = userMapper.getUserByNickName(student.getNickname());
            if (selectUser != null && !student.equals(selectUser)) {
                throw new ConflictException(new ErrorMessage("nickname duplicate", 1));
            }
        }
    }

    private void checkAccountDuplication(Student student) {
        User selectUser = userMapper.getUserByAccount(student.getAccount());
        if (selectUser != null) {
            if (selectUser.isUserAuthed() || selectUser.isAwaitingEmailAuthenticate()) {
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

    @Override
    public Boolean authenticate(String authToken) {
        User user = userMapper.getUserByAuthToken(authToken);

        if (user == null || user.isUserAuthed() || user.isAuthTokenExpired()) {
            return false;
        }

        userMapper.updateUserIsAuthed(user.getId(), true);

        slackNotiSender.noticeRegister(NotiSlack.builder()
                .color("good")
                .text(user.getAccount() + "님이 가입하셨습니다.")
                .build());

        return true;
    }

    @Override
    public Map<String, Object> changePasswordConfig(String account, String host) {
        if (account == null) {
            throw new ValidationException(new ErrorMessage("account is required", 0));
        }

        User selectUser = userMapper.getUserByAccount(account);
        if(selectUser == null){
            throw new NotFoundException(new ErrorMessage("invalid authenticate", 0));
        }

        Date resetExpiredAt = DateUtil.addHoursToJavaUtilDate(new Date(), 1);
        final String resetToken = SHA256Util.getEncrypt(account, resetExpiredAt.toString());
        userMapper.updateResetTokenAndResetTokenExpiredTime(selectUser.getId(), resetToken, resetExpiredAt);

        sendResetTokenByEmailForAuthenticate(resetToken, host, selectUser.getEmail());

        return new HashMap<String, Object>() {{
            put("success", "send authenticate mail to your account email");
        }};
    }

    private void sendResetTokenByEmailForAuthenticate(String resetToken, String contextPath, String email) {
        Map<String, Object> model = new HashMap<>();
        model.put("resetToken", resetToken);
        model.put("contextPath", contextPath);

        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mail/change_password.vm", "UTF-8", model);

        sesMailSender.sendMail("no-reply@bcsdlab.com", email, "코인 패스워드 초기화 인증", text);
    }

    @Override
    public Boolean changePasswordInput(String resetToken) {
        User user = userMapper.getUserByResetToken(resetToken);

        if ((user == null) || isTokenExpired(user.getReset_expired_at())) {
            return false;

        } else {
            return true;
        }
    }

    private boolean isTokenExpired(Date expiredAt) {
        return expiredAt.getTime() - (new Date()).getTime() < 0;
    }

    @Override
    public Boolean changePasswordAuthenticate(String password, String resetToken) {
        User selectUser = userMapper.getUserByResetToken(resetToken);

        if ((selectUser == null) || isTokenExpired(selectUser.getReset_expired_at())) {
            return false;
        }

        // TODO: password hashing
        selectUser.setPassword(passwordEncoder.encode(password));
        selectUser.setReset_expired_at(new Date());

        userMapper.updateUser(selectUser);

        return true;
    }

    @Override
    @Transactional
    public void withdraw() throws Exception {
        User user = jwtValidator.validate();

        userMapper.deleteUser(user.getId(), false);
        deleteAccessTokenFromRedis(user.getId());

        slackNotiSender.noticeWithdraw(NotiSlack.builder()
                .color("good")
                .text(user.getAccount() + "님이 탈퇴하셨습니다.")
                .build());
    }

    private void deleteAccessTokenFromRedis(Integer userId) {
        stringRedisUtilStr.deleteData(redisLoginTokenKeyPrefix + userId.toString());
    }


    @Override
    @Transactional
    public StudentResponse updateStudentInformation(UpdateUserRequest request) throws Exception {
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
            User selectUser = userMapper.getUserByNickName(owner.getNickname());
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
    public Map<String, Object> checkUserNickName(String nickname) {
        checkNicknameValidAndNotUsed(nickname);

        return new HashMap<String, Object>() {{
            put("success", "사용 가능한 닉네임입니다.");
        }};
    }

    private void checkNicknameValidAndNotUsed(String nickname){
        if (StringUtils.isEmpty(nickname) || nickname.length() > 10)
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 닉네임 형식입니다.", 0));

        if (isNicknameAlreadyUsed(nickname)) {
            throw new ConflictException(new ErrorMessage("사용할 수 없는 닉네임입니다.", 0));
        }
    }

    private boolean isNicknameAlreadyUsed(String nickname){
        return userMapper.isNicknameAlreadyUsed(nickname) > 0;
    }

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        return null;
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
}
