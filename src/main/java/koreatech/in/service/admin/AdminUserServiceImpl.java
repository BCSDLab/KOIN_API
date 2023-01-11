package koreatech.in.service.admin;

import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserCode;
import koreatech.in.domain.User.UserResponseType;
import koreatech.in.domain.User.UserType;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.user.request.LoginRequest;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.exception.UnauthorizeException;
import koreatech.in.repository.AuthorityMapper;
import koreatech.in.repository.user.OwnerMapper;
import koreatech.in.repository.user.StudentMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.service.JwtValidator;
import koreatech.in.util.JwtTokenGenerator;
import koreatech.in.util.StringRedisUtilStr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static koreatech.in.domain.DomainToMap.domainToMap;
import static koreatech.in.domain.DomainToMap.domainToMapWithExcept;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisUtilStr stringRedisUtilStr;

    @Value("${redis.key.login_prefix}")
    private String redisLoginTokenKeyPrefix;


    public Map<String, Object> getUserListForAdmin(Criteria criteria) {
        Integer totalCount = userMapper.getTotalCount();
        int totalPage = criteria.calcTotalPage(totalCount);
        Map<String, Object> map = new HashMap<>();

        map.put("items", userMapper.getUserListForAdmin(criteria.getCursor(), criteria.getLimit()));
        map.put("totalPage", totalPage);

        return map;
    }

    @Override
    public User getUserForAdmin(int id) {
        User user = userMapper.getUserById(id);
        if(user == null){
            throw new NotFoundException(new ErrorMessage("User not found.", 0));
        }
        return user;
    }

    @Override
    public Student createStudentForAdmin(Student student) {
        // 가입되어 있는 계정이거나, 메일 인증을 아직 하지 않은 경우 가입 요청중인 계정이 디비에 존재하는 경우 예외처리
        // TODO: 메일 인증 하지 않은 경우 조건 추가
        if(userMapper.isAccountAlreadyUsed(student.getAccount()) > 0){
            throw new NotFoundException(new ErrorMessage("already exists", 0));
        }

        // 닉네임 중복 체크
        if (student.getNickname() != null) {
            if (userMapper.isNicknameAlreadyUsed(student.getNickname()) > 0){
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

        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setAnonymousNickname("익명_" + (System.currentTimeMillis()));

        // TODO: default로 셋팅할 수 있는 방법 알아보기
        if (student.getIdentity() == null) {
            student.setIdentity(UserCode.UserIdentity.STUDENT.getIdentityType());
        }

        if (student.getIsGraduated() == null) {
            student.setIsGraduated(false);
        }

        if (student.getIsAuthed() == null) {
            student.setIsAuthed(false);
        }

        // 추후 메일 인증에 필요한 가입 정보를 디비에 업데이트
        try {
            userMapper.insertUser(student);
            studentMapper.insertStudent(student);
        } catch (SQLException sqlException) {
            throw new ConflictException(new ErrorMessage("invalid authenticate", 0));
        }

        return student;
    }


    @Override
    public Student updateStudentForAdmin(Student student, int id) {
        Student selectUser = studentMapper.getStudentById(id);

        if(selectUser == null){
            throw new NotFoundException(new ErrorMessage("No User", 0));
        }

        student.setIdentity(selectUser.getIdentity());

        // 닉네임 중복 체크
        if (student.getNickname() != null) {
            User selectUser2 = userMapper.getUserByNickName(student.getNickname());
            if (selectUser2 != null && !selectUser.getId().equals(selectUser2.getId())) {
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

        if (student.getPassword() != null) {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        }

        selectUser.update(student);
        userMapper.updateUser(selectUser);
        studentMapper.updateStudent(selectUser);

        return student;
    }

    @Override
    public Map<String, Object> deleteUserForAdmin(int id) {
        User selectUser = userMapper.getUserById(id);

        if(selectUser == null){
            throw new NotFoundException(new ErrorMessage("No User", 0));
        }

        if (selectUser.getUserType().equals(UserType.STUDENT)){
            studentMapper.deleteStudent(id);

        } else {
            ownerMapper.deleteOwner(id);
        }
        userMapper.deleteUser(id);

        return new HashMap<String, Object>() {{
            put("success", "delete student");
        }};
    }

    @Transactional
    @Override
    public Authority createPermissionForAdmin(Authority authority, int userId) {
        User selectUser = userMapper.getUserById(userId);
        if(selectUser == null){
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
    public Map<String, Object> loginForAdmin(LoginRequest request) throws Exception {
        final User selectUser = userMapper.getAuthedUserByAccount(request.getAccount());

        if(selectUser == null){
            throw new UnauthorizeException(new ErrorMessage("There is no such ID", 0));
        }

        if (!passwordEncoder.matches(request.getPassword(), selectUser.getPassword())) {
            throw new UnauthorizeException(new ErrorMessage("password not match", 0));
        }

        if (userMapper.getAuthorityByUserIdForAdmin(selectUser.getId()) == null) {
            throw new UnauthorizeException(new ErrorMessage("There is no authority", 0));
        }

        selectUser.setLastLoggedAt(new Date());
        userMapper.updateUser(selectUser);
        Map<String, Object> map = domainToMapWithExcept(selectUser, UserResponseType.getArray(), false);

        String getToken = stringRedisUtilStr.getDataAsString(redisLoginTokenKeyPrefix + selectUser.getId().toString());
        if (getToken == null || jwtTokenGenerator.isExpired(getToken)) {
            getToken = jwtTokenGenerator.generate(selectUser.getId());
            stringRedisUtilStr.valOps.set(redisLoginTokenKeyPrefix + selectUser.getId().toString(), getToken, 72, TimeUnit.HOURS);
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
        stringRedisUtilStr.deleteData(redisLoginTokenKeyPrefix + user.getId().toString());

        return new HashMap<String, Object>() {{
            put("success", "logout");
        }};
    }

    @Override
    public Map<String, Object> getPermissionListForAdmin(int page, int limit) throws Exception {
        Map<String, Object> map = new HashMap<>();

        limit = Math.min(limit, 50);
        page = Math.max(page, 1);

        double totalCount = authorityMapper.totalAuthorityCount();
        double countByLimit = totalCount / limit;
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / limit);
        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        int cursor = (page - 1) * limit;

        List<Authority> admins = authorityMapper.getAuthorityList(cursor, limit);
        List<Map<String, Object>> listedAdmin = new ArrayList<>();

        for (Authority admin : admins) {
            Map<String, Object> adminToMap = domainToMap(admin);
            User user = userMapper.getUserById(admin.getUser_id());
            if (user == null) {
                adminToMap.put("users", null);
            } else {
                Map<String, Object> userToMap = new HashMap<String, Object>() {{
                    put("portal_account", user.getAccount());
                }};

                adminToMap.put("users", userToMap);
            }
            listedAdmin.add(adminToMap);
        }
        map.put("admins", listedAdmin);
        map.put("totalPage", totalPage);

        return map;
    }
}
