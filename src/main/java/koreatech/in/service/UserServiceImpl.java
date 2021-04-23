package koreatech.in.service;

import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.NotiSlack;
import koreatech.in.domain.User.Owner;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserCode;
import koreatech.in.domain.User.UserResponseType;
import koreatech.in.exception.*;
import koreatech.in.repository.AuthorityMapper;
import koreatech.in.repository.UserMapper;
import koreatech.in.util.*;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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

@Service("userService")
public class UserServiceImpl implements UserService, UserDetailsService {
    @Resource(name = "userMapper")
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

    @Inject
    private PasswordEncoder passwordEncoder;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    public Map<String, Object> getUserListForAdmin(Criteria criteria) throws Exception {
        double totalCount = userMapper.totalCount();
        double countByLimit = totalCount / criteria.getLimit();
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / criteria.getLimit());

        if (totalPage<0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        Map<String, Object> map = new HashMap<>();

        map.put("items", userMapper.getUserListForAdmin(criteria.getCursor(), criteria.getLimit()));
        map.put("totalPage", totalPage);

        return map;
    }

    @Override
    public User getUserForAdmin(int id) throws Exception {
        User selectUser = userMapper.getUser(id);

        if(selectUser == null) {
            throw new NotFoundException(new ErrorMessage("User not found.", 0));
        }

        return selectUser;
    }

    @Override
    public User createUserForAdmin(User user) {
        // 가입되어 있는 계정인지 체크
        User selectUser = userMapper.getUserByPortalAccount(user.getPortal_account());

        // 가입되어 있는 계정이거나, 메일 인증을 아직 하지 않은 경우 가입 요청중인 계정이 디비에 존재하는 경우 예외처리
        // TODO: 메일 인증 하지 않은 경우 조건 추가
        if (selectUser != null) {
            throw new NotFoundException(new ErrorMessage("already exists", 0));
        }

        // 닉네임 중복 체크
        if (user.getNickname() != null) {
            User selectUser2 = userMapper.getUserByNickName(user.getNickname());
            if (selectUser2 != null) {
                throw new ConflictException(new ErrorMessage("nickname duplicate", 1));
            }
        }

        // 학번 유효성 체크
        if (user.getStudent_number() != null && !UserCode.isValidatedStudentNumber(user.getIdentity(), user.getStudent_number())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid student number", 2));
        }

        // 학과 유효성 체크
        if (user.getMajor() != null && !UserCode.isValidatedDeptNumber(user.getMajor())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid dept code", 3));
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAnonymous_nickname("익명_" + (System.currentTimeMillis()));

        // TODO: default로 셋팅할 수 있는 방법 알아보기
        if (user.getIdentity() == null) {
            user.setIdentity(UserCode.UserIdentity.STUDENT.getIdentityType());
        }

        if (user.getIs_graduated() == null) {
            user.setIs_graduated(false);
        }

        if (user.getIs_authed() == null) {
            user.setIs_authed(false);
        }

        // 추후 메일 인증에 필요한 가입 정보를 디비에 업데이트
        try {
            userMapper.createUser(user);
        } catch (SQLException sqlException) {
            throw new ConflictException(new ErrorMessage("invalid authenticate", 0));
        }

        return user;
    }


    @Override
    public User updateUserForAdmin(User user, int id) {
        User selectUser = userMapper.getUser(id);
        if (selectUser == null) {
            throw new NotFoundException(new ErrorMessage("No User", 0));
        }

        user.setIdentity(selectUser.getIdentity());

        // 닉네임 중복 체크
        if (user.getNickname() != null) {
            User selectUser2 = userMapper.getUserByNickName(user.getNickname());
            if (selectUser2 != null && !selectUser.getId().equals(selectUser2.getId())) {
                throw new ConflictException(new ErrorMessage("nickname duplicate", 1));
            }
        }

        // 학번 유효성 체크
        if (user.getStudent_number() != null && !UserCode.isValidatedStudentNumber(user.getIdentity(), user.getStudent_number())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid student number", 2));
        }

        // 학과 유효성 체크
        if (user.getMajor() != null && !UserCode.isValidatedDeptNumber(user.getMajor())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid dept code", 3));
        }

        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (selectUser.getIdentity() == UserCode.UserIdentity.OWNER.getIdentityType()) {
            Owner owner = (Owner) selectUser;
            owner.update(user);
            userMapper.updateUser(owner);
            userMapper.updateOwner(owner);
        }
        else {
            selectUser.update(user);
            userMapper.updateUser(selectUser);
        }

        return user;
    }

    @Override
    public Map<String, Object> deleteUserForAdmin(int id) {
        User selectUser = userMapper.getUser(id);
        if (selectUser == null) {
            throw new NotFoundException(new ErrorMessage("No User", 0));
        }

        userMapper.deleteUser(id);

        return new HashMap<String, Object>() {{
            put("success", "delete user");
        }};
    }

    @Transactional
    @Override
    public Authority createPermissionForAdmin(Authority authority, int userId) {
        User selectUser = userMapper.getUser(userId);
        if (selectUser == null) {
            throw new NotFoundException(new ErrorMessage("No User", 0));
        }

        authority.init();
        authority.setUser_id(userId);

        Authority selectAuthority = authorityMapper.getAuthorityByUserId(userId);
        if (selectAuthority != null) {
            throw new PreconditionFailedException(new ErrorMessage("already have authority", 0));
        }

        authorityMapper.createAuthority(authority);

        return authority;
    }

    @Override
    public Authority getPermissionForAdmin(int userId) {
        Authority authority = authorityMapper.getAuthorityByUserId(userId);

        if (authority == null) {
            throw new NotFoundException(new ErrorMessage("No Authority", 0));
        }

        return authority;
    }

    @Override
    public Authority updatePermissionForAdmin(Authority authority, int userId) {
        Authority selectAuthority = authorityMapper.getAuthorityByUserId(userId);

        if (selectAuthority == null) {
            throw new NotFoundException(new ErrorMessage("No Authority", 0));
        }

        selectAuthority.update(authority);

        authorityMapper.updateAuthorityByUserId(selectAuthority, userId);

        return authority;
    }

    @Override
    public Map<String, Object> deletePermissionForAdmin(int userId) {
        Authority selectAuthority = authorityMapper.getAuthorityByUserId(userId);

        if (selectAuthority == null) {
            throw new NotFoundException(new ErrorMessage("No Authority", 0));
        }

        authorityMapper.deleteAuthority(userId);

        return new HashMap<String, Object>() {{
            put("success", "delete authority");
        }};
    }

    @Override
    public Map<String, Object> loginForAdmin(User user) throws Exception {
        final User selectUser = userMapper.getUserByPortalAccount(user.getPortal_account());

        if (selectUser == null || !selectUser.getIs_authed()) {
            throw new UnauthorizeException(new ErrorMessage("There is no such ID", 0));
        }

        if (!passwordEncoder.matches(user.getPassword(), selectUser.getPassword())) {
            throw new UnauthorizeException(new ErrorMessage("password not match", 0));
        }

        if (userMapper.getAuthorityByUserIdForAdmin(selectUser.getId()) == null) {
            throw new UnauthorizeException(new ErrorMessage("There is no authority", 0));
        }

        selectUser.setLast_logged_at(new Date().toString());
        userMapper.updateUser(selectUser);
        Map<String,Object> map = domainToMapWithExcept(selectUser, UserResponseType.getArray(), false);

        String getToken = valueOps.get("user@"+selectUser.getId().toString());
        if (getToken == null || jwtTokenGenerator.isExpired(getToken)) {
            getToken = jwtTokenGenerator.generate(selectUser.getId());
            valueOps.set("user@" + selectUser.getId().toString(), getToken, 72, TimeUnit.HOURS);
        }

        final String token = getToken;

        return new HashMap<String, Object>() {{
            put("user", map);
            put("token", token);
        }};
    }

    @Override
    public Map<String, Object> logoutForAdmin() {
        // TODO: jwt 이력 삭제
        User user = jwtValidator.validate();
        redisTemplate.delete("user@" + user.getId().toString());

        return new HashMap<String, Object>() {{
            put("success", "logout");
        }};
    }

    @Override
    public Map<String, Object> getPermissionListForAdmin(int page, int limit) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        limit = (limit>50) ? 50 : limit;
        page = (page<1) ? 1 : page;

        double totalCount = authorityMapper.totalAuthorityCount();
        double countByLimit = totalCount/limit;
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int)Math.ceil(totalCount/limit);
        if (totalPage<0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        int cursor = (page-1)*limit;

        List<Authority> admins = authorityMapper.getAuthorityList(cursor, limit);
        List<Map<String, Object>> listedAdmin = new ArrayList<>();

        for (Authority admin : admins) {
            Map<String, Object> adminToMap = domainToMap(admin);
            User user = userMapper.getUser(admin.getUser_id());
            if (user == null) {
                adminToMap.put("users", null);
            }
            else {
                Map<String, Object> userToMap = new HashMap<String, Object>() {{
                    put("portal_account", user.getPortal_account());
                }};

                adminToMap.put("users", userToMap);
            }
            listedAdmin.add(adminToMap);
        }
        map.put("admins", listedAdmin);
        map.put("totalPage", totalPage);

        return map;
    }

    @Transactional
    @Override
    public Map<String, Object> register(User user, String host) throws Exception {
        // comment : 추후 로그인 기반 시스템 갖추어지면 변경할 것.
        user.setIdentity(UserCode.UserIdentity.STUDENT.getIdentityType());

        // 가입되어 있는 계정인지 체크
        User selectUser = userMapper.getUserByPortalAccount(user.getPortal_account());

        // 가입되어 있는 계정이거나, 메일 인증을 아직 하지 않은 경우 가입 요청중인 계정이 디비에 존재하는 경우 예외처리
        // TODO: 메일 인증 하지 않은 경우 조건 추가
        if (selectUser != null && (selectUser.getIs_authed() || selectUser.getAuth_expired_at().getTime() - (new Date()).getTime() > 0)) {
            throw new ConflictException(new ErrorMessage("invalid authenticate", 0));
        }

        // 닉네임 중복 체크
        if (user.getNickname() != null) {
            User selectUser2 = userMapper.getUserByNickName(user.getNickname());
            if (selectUser2 != null) {
                throw new ConflictException(new ErrorMessage("nickname duplicate", 1));
            }
        }

        // 학번 유효성 체크
        if (user.getStudent_number() != null && !UserCode.isValidatedStudentNumber(user.getIdentity(), user.getStudent_number())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid student number", 2));
        }

        // 학과 유효성 체크
        if (user.getMajor() != null && !UserCode.isValidatedDeptNumber(user.getMajor())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid dept code", 3));
        }

        // 가입 메일에 있는 토큰의 유효기간 설정
        Date authExpiredAt = DateUtil.addHoursToJavaUtilDate(new Date(), 1);
        final String authToken = SHA256Util.getEncrypt(user.getPortal_account(), authExpiredAt.toString());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuth_token(authToken);
        user.setAuth_expired_at(authExpiredAt);
        user.setAnonymous_nickname("익명_" + (System.currentTimeMillis()));

        // 추후 메일 인증에 필요한 가입 정보를 디비에 업데이트
        if (selectUser == null) {
            try {
                userMapper.createUser(user);
            } catch (SQLException sqlException) {
                throw new ConflictException(new ErrorMessage("invalid authenticate", 0));
            }


        } else {
            user.setId(selectUser.getId());
            userMapper.updateUser(user);
        }

        final String contextPath = host;
        final String toAccount = user.getPortal_account() + "@koreatech.ac.kr";

//        이전 gmail api 사용한 전송
//        MimeMessagePreparator preparator = new MimeMessagePreparator() {
//            @Override
//            public void prepare(MimeMessage mimeMessage) throws Exception {
//                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
//                message.setSubject("코인 이메일 회원가입 인증");
//                message.setTo(toAccount);
//                message.setFrom("bcsdlab@gmail.com");
//
//                Map model = new HashMap();
//                model.put("authToken", authToken);
//                model.put("contextPath", contextPath);
//
//                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mail/register_authenticate.vm", model);
//                message.setText(text, true);
//            }
//        };
//
//        mailSender.send(preparator);

        Map<String, Object> model = new HashMap<>();
        model.put("authToken", authToken);
        model.put("contextPath", contextPath);

        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mail/register_authenticate.vm", "UTF-8", model);

        sesMailSender.sendMail("no-reply@bcsdlab.com", toAccount, "코인 이메일 회원가입 인증", text);

        NotiSlack slack_message = new NotiSlack();

        slack_message.setColor("good");
        slack_message.setText(user.getPortal_account() + "님이 이메일 인증을 요청하였습니다.");

        slackNotiSender.noticeRegister(slack_message);

        return new HashMap<String, Object>() {{
            put("success", "send mail for user authentication to entered email address");
        }};
    }

    @Override
    public Boolean authenticate(String authToken) {
        User user = userMapper.getUserByAuthToken(authToken);

        if (user == null || user.getIs_authed() || user.getAuth_expired_at().getTime() - (new Date()).getTime() < 0) {
            return false;
        }

        user.setIs_authed(true);

        userMapper.updateUser(user);

        NotiSlack slack_message = new NotiSlack();

        slack_message.setColor("good");
        slack_message.setText(user.getPortal_account() + "님이 가입하셨습니다.");

        slackNotiSender.noticeRegister(slack_message);

        return true;
    }

    @Override
    public Map<String, Object> changePasswordConfig(User user, String host) {
        if (user.getPortal_account() == null) {
            throw new ValidationException(new ErrorMessage("portal_account is required", 0));
        }

        User selectUser = userMapper.getUserByPortalAccount(user.getPortal_account());
        if (selectUser == null) {
            throw new NotFoundException(new ErrorMessage("invalid authenticate", 0));
        }

        Date resetExpiredAt = DateUtil.addHoursToJavaUtilDate(new Date(), 1);
        final String resetToken = SHA256Util.getEncrypt(user.getPortal_account(), resetExpiredAt.toString());

        selectUser.setReset_expired_at(resetExpiredAt);
        selectUser.setReset_token(resetToken);

        userMapper.updateUser(selectUser);

        final String contextPath = host;
        final String toAccount;
        if (selectUser.getIdentity() == UserCode.UserIdentity.OWNER.getIdentityType()) {
            toAccount = userMapper.getOwnerEmail(selectUser.getId());
            if (toAccount == null)
                throw new NotFoundException(new ErrorMessage("이메일이 등록되어 있지 않습니다.", 0));
        }
        else {
            toAccount = user.getPortal_account() + "@koreatech.ac.kr";
        }

//        이전 gmail api 사용한 전송
//        MimeMessagePreparator preparator = new MimeMessagePreparator() {
//            @Override
//            public void prepare(MimeMessage mimeMessage) throws Exception {
//                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
//                message.setSubject("코인 패스워드 초기화 인증");
//                message.setTo(toAccount);
//                message.setFrom("bcsdlab@gmail.com");
//
//                Map model = new HashMap();
//                model.put("resetToken", resetToken);
//                model.put("contextPath", contextPath);
//
//                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mail/change_password.vm", model);
//                message.setText(text, true);
//            }
//        };
//
//        mailSender.send(preparator);

        Map<String, Object> model = new HashMap<>();
        model.put("resetToken", resetToken);
        model.put("contextPath", contextPath);

        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "mail/change_password.vm", "UTF-8", model);

        sesMailSender.sendMail("no-reply@bcsdlab.com", toAccount, "코인 패스워드 초기화 인증", text);

        return new HashMap<String, Object>() {{
            put("success", "send authenticate mail to your account email");
        }};
    }

    @Override
    public Boolean changePasswordInput(String resetToken) {
        User user = userMapper.getUserByResetToken(resetToken);

        if ((user == null) || (user.getReset_expired_at().getTime() - (new Date()).getTime() < 0)) {
            return false;
        }

        return true;
    }

    @Override
    public Boolean changePasswordAuthenticate(String password, String resetToken) {
        User selectUser = userMapper.getUserByResetToken(resetToken);

        if ((selectUser == null) || (selectUser.getReset_expired_at().getTime() - (new Date()).getTime() < 0)) {
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
    public Map<String, Object> withdraw() throws Exception {
        User user = jwtValidator.validate();
        userMapper.deleteUser(user.getId());
        redisTemplate.delete("user@" + user.getId().toString());

        NotiSlack slack_message = new NotiSlack();

        slack_message.setColor("good");
        slack_message.setText(user.getPortal_account() + "님이 탈퇴하셨습니다.");

        slackNotiSender.noticeWithdraw(slack_message);

        return new HashMap<String, Object>() {{
            put("success", true);
        }};
    }

    @Override
    public User me() throws Exception {
        User user = jwtValidator.validate();

        return user;
    }

    @Override
    @Transactional
    public Map<String,Object> updateUserInformation(User user) throws Exception {
        User user_old = jwtValidator.validate();

        user.setIdentity(user_old.getIdentity());

        // 인증받은 유저인지 체크
        if (!user_old.getIs_authed()) {
            throw new ForbiddenException(new ErrorMessage("Not Authed User", 0));
        }

        // 닉네임 중복 체크
        if (user.getNickname() != null) {
            User selectUser = userMapper.getUserByNickName(user.getNickname());
            if (selectUser != null && !user_old.getId().equals(selectUser.getId())) {
                throw new ConflictException(new ErrorMessage("nickname duplicate", 1));
            }
        }

        // 학번 유효성 체크
        if (user.getStudent_number() != null && !UserCode.isValidatedStudentNumber(user.getIdentity(), user.getStudent_number())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid student number", 2));
        }

        // 학과 유효성 체크
        if (user.getMajor() != null && !UserCode.isValidatedDeptNumber(user.getMajor())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid dept code", 3));
        }

        // TODO: hashing passowrd
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        user_old.update(user);
        userMapper.updateUser(user_old);

        Map<String,Object> map = domainToMapWithExcept(user_old, UserResponseType.getArray(), false);

        return map;
    }

    @Override
    @Transactional
    public Map<String,Object> updateOwnerInformation(Owner owner) throws Exception {
        Owner user_old;
        try {
            user_old = (Owner) jwtValidator.validate();
        }
        catch (ClassCastException e) {
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

        // 학번 유효성 체크
        if (owner.getStudent_number() != null && !UserCode.isValidatedStudentNumber(owner.getIdentity(), owner.getStudent_number())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid student number", 2));
        }

        // 학과 유효성 체크
        if (owner.getMajor() != null && !UserCode.isValidatedDeptNumber(owner.getMajor())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid dept code", 3));
        }

        // TODO: hashing passowrd
        if (owner.getPassword() != null) {
            owner.setPassword(passwordEncoder.encode(owner.getPassword()));
        }

        user_old.update(owner);
        userMapper.updateUser(user_old);
        userMapper.updateOwner(user_old);

        Map<String,Object> map = domainToMapWithExcept(user_old, UserResponseType.getArray(), false);

        return map;
    }

    @Override
    public Map<String, Object> checkUserNickName(String nickname) {
        if (StringUtils.isEmpty(nickname) || nickname.length() > 10)
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 닉네임 형식입니다.", 0));

        User user = userMapper.getUserByNickName(nickname);
        if (user != null) {
            throw new ConflictException(new ErrorMessage("사용할 수 없는 닉네임입니다.", 0));
        }

        return new HashMap<String, Object>() {{
            put("success", "사용 가능한 닉네임입니다.");
        }};
    }

    @Override
    public Map<String, Object> login(User user) throws Exception {
        final User selectUser = userMapper.getUserByPortalAccount(user.getPortal_account());

        if (selectUser == null || !selectUser.getIs_authed()) {
            throw new UnauthorizeException(new ErrorMessage("There is no such ID", 0));
        }

        if (!passwordEncoder.matches(user.getPassword(), selectUser.getPassword())) {
            throw new UnauthorizeException(new ErrorMessage("password not match", 0));
        }

        selectUser.setLast_logged_at(new Date().toString());
        userMapper.updateUser(selectUser);
        Map<String,Object> map = domainToMapWithExcept(selectUser, UserResponseType.getArray(), false);

        String getToken = valueOps.get("user@" + selectUser.getId().toString());
        if (getToken == null || jwtTokenGenerator.isExpired(getToken)) {
            getToken = jwtTokenGenerator.generate(selectUser.getId());
            valueOps.set("user@" + selectUser.getId().toString(), getToken, 72, TimeUnit.HOURS);
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
    public UserDetails loadUserByUsername(String portal_account) throws UsernameNotFoundException {
        User user = userMapper.getUserByPortalAccount(portal_account);

        if(user == null) {
            throw new NotFoundException(new ErrorMessage("No User", 0));
        }

        return user;
    }
}
