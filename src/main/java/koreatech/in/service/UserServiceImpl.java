package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.NotiSlack;
import koreatech.in.domain.user.owner.Owner;
import koreatech.in.domain.user.User;
import koreatech.in.domain.user.UserCode;
import koreatech.in.domain.user.UserResponseType;
import koreatech.in.domain.user.student.Student;
import koreatech.in.exception.*;
import koreatech.in.repository.AuthorityMapper;
import koreatech.in.repository.user.OwnerMapper;
import koreatech.in.repository.user.StudentMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.util.*;
import lombok.RequiredArgsConstructor;
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

import javax.annotation.Resource;
import javax.inject.Inject;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static koreatech.in.domain.DomainToMap.domainToMap;
import static koreatech.in.domain.DomainToMap.domainToMapWithExcept;

// TODO 리터럴 문자열 전부 제거
@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserMapper userMapper;
    private final AuthorityMapper authorityMapper;
    private final SesMailSender sesMailSender;
    private final VelocityEngine velocityEngine;
    private final JwtValidator jwtValidator;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final SlackNotiSender slackNotiSender;
    private final OwnerMapper ownerMapper;
    private final StringRedisUtilStr stringRedisUtilStr;
    private final StudentMapper studentMapper;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Value("${redis.key.login_prefix}")
    private String redisLoginTokenKeyPrefix;

    @Transactional
    @Override
    public Map<String, Object> StudentRegister(Student student, String host) throws Exception {
        // comment : 추후 로그인 기반 시스템 갖추어지면 변경할 것.
        student.setIdentity(UserCode.UserIdentity.STUDENT.getIdentityType());

        Student selectUser = userMapper.<Student>getUserByAccount(student.getAccount()).get();

        // 가입되어 있는 계정이거나, 메일 인증을 아직 하지 않은 경우 가입 요청중인 계정이 디비에 존재하는 경우 예외처리
        // 가입 요청 후, 인증 토큰의 유효기간이 초과된 경우는 회원가입 재시도 가능
        // TODO: 메일 인증 하지 않은 경우 조건 추가
        if (selectUser != null) {
            if (selectUser.getIsAuthed() || !isTokenExpired(selectUser.getAuthExpiredAt())) {
                throw new ConflictException(new ErrorMessage("invalid authenticate", 0));
            }
        }

       checkInputDataValidationForRegister(student);

        // 가입 메일에 있는 토큰의 유효기간 설정
        // TODO 추상화 + 정적 팩토리 메소드 혹은 빌더 패턴을 이용하여 생성하도록 수정
        Date authExpiredAt = DateUtil.addHoursToJavaUtilDate(new Date(), 1);
        final String authToken = SHA256Util.getEncrypt(student.getAccount(), authExpiredAt.toString());
        student.setAuthToken(authToken);
        student.setAuthExpiredAt(authExpiredAt);

        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setAnonymousNickname("익명_" + (System.currentTimeMillis()));
        student.setEmail(student.getAccount() + "@koreatech.ac.kr");

        // 추후 메일 인증에 필요한 가입 정보를 디비에 업데이트
        try {
            userMapper.createUser(student);
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

    private void sendAuthTokenByEmailForAuthenticate(String authToken, String contextPath, String email){
        Map<String, Object> model = new HashMap<>();
        model.put("authToken", authToken);
        model.put("contextPath", contextPath);

        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mail/register_authenticate.vm", "UTF-8", model);

        sesMailSender.sendMail("no-reply@bcsdlab.com", email, "코인 이메일 회원가입 인증", text);
    }

    private void checkInputDataValidationForRegister(Student student){
        // 닉네임 중복 체크
        if (student.getNickname() != null) {
            if(isUserNickNameAlreadyUsed(student.getNickname())){
                throw new ConflictException(new ErrorMessage("nickname duplicate", 1));
            }
        }

        // 학번 유효성 체크
        if (student.getStudentNumber() != null && !UserCode.isValidatedStudentNumber(student.getIdentity(), student.getStudentNumber())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid student number", 2));
        }

        // 학과 유효성 체크
        if (student.getMajor() != null && !UserCode.isValidatedDeptNumber(student.getMajor())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid dept code", 3));
        }
    }

    @Override
    public Boolean authenticate(String authToken) {
        User user = userMapper.getUserByAuthToken(authToken).get();

        if (user == null || user.getIsAuthed() || isTokenExpired(user.getAuthExpiredAt())) {
            return false;
        }

        userMapper.updateUserIsAuth(true);

        slackNotiSender.noticeRegister(NotiSlack.builder()
                .color("good")
                .text(user.getAccount() + "님이 가입하셨습니다.")
                .build());

        return true;
    }

    @Override
    public Map<String, Object> changePasswordConfig(String account, String host) {
        // TODO client 로부터 받을 때 validation 확인
        if (account == null) {
            throw new ValidationException(new ErrorMessage("account is required", 0));
        }

        // TODO 유저 전체가 아닌 필요한 것만 조회?
        User selectUser = userMapper.getUserByAccount(account)
                .orElseThrow(()->new NotFoundException(new ErrorMessage("invalid authenticate", 0)));

        Date resetExpiredAt = DateUtil.addHoursToJavaUtilDate(new Date(), 1);
        final String resetToken = SHA256Util.getEncrypt(account, resetExpiredAt.toString());
        userMapper.updateResetTokenAndResetTokenExpiredTime(resetToken, resetExpiredAt);

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
        User user = userMapper.getUserByResetToken(resetToken).get();

        if ((user == null) || isTokenExpired(user.getResetExpiredAt())) {
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
        User selectUser = userMapper.getUserByResetToken(resetToken).get();

        if ((selectUser == null) || isTokenExpired(selectUser.getResetExpiredAt())) {
            return false;
        }

        // TODO: password hashing
        selectUser.setPassword(passwordEncoder.encode(password));
        selectUser.setResetExpiredAt(new Date());

        userMapper.updateUser(selectUser);

        return true;
    }

    @Override
    @Transactional
    public Map<String, Object> withdraw() throws Exception {
        User user = jwtValidator.validate();
        userMapper.deleteUser(user.getId());
        stringRedisUtilStr.deleteData("student@" + user.getId().toString());

        slackNotiSender.noticeWithdraw(NotiSlack.builder()
                .color("good")
                .text(user.getAccount() + "님이 탈퇴하셨습니다.")
                .build());

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }


    @Override
    @Transactional
    public Map<String, Object> updateStudentInformation(Student student) throws Exception {
        Student student_old = studentMapper.getStudentById(jwtValidator.validate().getId())
                .orElseThrow(()->new ValidationException(new ErrorMessage("token not validate", 402)));

        student.setIdentity(student_old.getIdentity());

        // 인증받은 유저인지 체크
        if (!student_old.getIsAuthed()) {
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));
        }

        // 닉네임 중복 체크
        if (student.getNickname() != null) {
            User selectUser = userMapper.getUserByNickName(student.getNickname()).get();
            if (selectUser != null && !student_old.getId().equals(selectUser.getId())) {
                throw new ConflictException(new ErrorMessage("nickname duplicate", 1));
            }
        }

        // 학번 유효성 체크
        if (student.getStudentNumber() != null && !UserCode.isValidatedStudentNumber(student.getIdentity(), student.getStudentNumber())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid student number", 2));
        }

        // 학과 유효성 체크
        if (student.getMajor() != null && !UserCode.isValidatedDeptNumber(student.getMajor())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid dept code", 3));
        }

        // TODO: hashing passowrd
        if (student.getPassword() != null) {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        }

        student_old.update(student);
        userMapper.updateUser(student_old);

        Map<String, Object> map = domainToMapWithExcept(student_old, UserResponseType.getArray(), false);

        return map;
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
        if (!user_old.getIsAuthed()) {
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));
        }

        // 닉네임 중복 체크
        if (owner.getNickname() != null) {
            User selectUser = userMapper.getUserByNickName(owner.getNickname()).get();
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
        // TODO 클라이언트에서 넘어올때 확인하도록 수정
        if (StringUtils.isEmpty(nickname) || nickname.length() > 10)
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 닉네임 형식입니다.", 0));

        if (isUserNickNameAlreadyUsed(nickname)) {
            throw new ConflictException(new ErrorMessage("사용할 수 없는 닉네임입니다.", 0));
        }
    }

    private boolean isUserNickNameAlreadyUsed(String nickname){
        return userMapper.isNicknameAlreadyUsed(nickname) > 0;
    }

    @Override
    public Map<String, Object> login(User user) throws Exception {
        final User selectUser = userMapper.getAuthedUserPasswordByAccount(user.getAccount())
                .orElseThrow(()->new UnauthorizeException(new ErrorMessage("There is no such ID", 0)));

        if (!passwordEncoder.matches(user.getPassword(), selectUser.getPassword())) {
            throw new UnauthorizeException(new ErrorMessage("password not match", 0));
        }

        selectUser.setLastLoggedAt(new Date().toString());
        userMapper.updateUser(selectUser);
        Map<String, Object> map = domainToMapWithExcept(selectUser, UserResponseType.getArray(), false);

        // ?? 레디스에서 이전 로그인 토큰이 있는지 확인 후, 없거나 expired 됐다면 재발급 후 반환
        // regenerateTokenAndSetRedisIfTokenNotExistOrExpired();
        String getToken = stringRedisUtilStr.getDataAsString(redisLoginTokenKeyPrefix + selectUser.getId());
        if (getToken == null || jwtTokenGenerator.isExpired(getToken)) {
            getToken = jwtTokenGenerator.generate(selectUser.getId());
            stringRedisUtilStr.valOps.set(redisLoginTokenKeyPrefix + selectUser.getId().toString(), getToken, 72, TimeUnit.HOURS);
        }

        final String token = getToken;

        return new HashMap<String, Object>() {{
            put("user", map);
            put("token", token);
            put("ttl", 4320);
        }};
    }

    @Override
    public Map<String, Object> logout() {
        jwtValidator.validate();

        return new HashMap<String, Object>() {{
            put("success", "logout");
        }};
    }

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        return userMapper.getUserByAccount(account)
                .orElseThrow(()->new NotFoundException(new ErrorMessage("No User", 0)));
    }
}
