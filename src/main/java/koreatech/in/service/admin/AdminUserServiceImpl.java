package koreatech.in.service.admin;

import static koreatech.in.domain.DomainToMap.domainToMap;
import static koreatech.in.exception.ExceptionInformation.INQUIRED_USER_NOT_FOUND;
import static koreatech.in.exception.ExceptionInformation.NOT_OWNER;
import static koreatech.in.exception.ExceptionInformation.NOT_STUDENT;
import static koreatech.in.exception.ExceptionInformation.PAGE_NOT_FOUND;
import static koreatech.in.exception.ExceptionInformation.PASSWORD_DIFFERENT;
import static koreatech.in.exception.ExceptionInformation.USER_NOT_FOUND;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import koreatech.in.domain.Auth.LoginResult;
import koreatech.in.domain.Auth.RefreshToken;
import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.UserCriteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserCode;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.auth.TokenRefreshRequest;
import koreatech.in.dto.admin.auth.TokenRefreshResponse;
import koreatech.in.dto.admin.user.request.LoginRequest;
import koreatech.in.dto.admin.user.request.NewOwnersCondition;
import koreatech.in.dto.admin.user.response.LoginResponse;
import koreatech.in.dto.admin.user.response.NewOwnersResponse;
import koreatech.in.dto.admin.user.response.OwnerResponse;
import koreatech.in.dto.admin.user.student.StudentResponse;
import koreatech.in.dto.normal.user.request.UpdateUserRequest;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.mapstruct.admin.auto.AuthConverter;
import koreatech.in.mapstruct.admin.user.OwnerConverter;
import koreatech.in.mapstruct.admin.user.StudentConverter;
import koreatech.in.repository.AuthenticationMapper;
import koreatech.in.repository.AuthorityMapper;
import koreatech.in.repository.admin.AdminUserMapper;
import koreatech.in.repository.user.StudentMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.service.JwtValidator;
import koreatech.in.util.jwt.UserAccessJwtGenerator;
import koreatech.in.util.jwt.UserRefreshJwtGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AdminUserServiceImpl implements AdminUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAccessJwtGenerator userAccessJwtGenerator;

    @Autowired
    private UserRefreshJwtGenerator userRefreshJwtGenerator;

    @Autowired
    private AuthenticationMapper redisAuthenticationMapper;

    @Override
    public LoginResponse login(LoginRequest request) throws Exception {
        final User user = userMapper.getAuthedUserByEmail(request.getEmail());

        if (user == null || !user.hasAuthority() /* 어드민 권한이 없으면 없는 회원으로 간주 */) {
            throw new BaseException(USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BaseException(PASSWORD_DIFFERENT);
        }

        user.updateLastLoginTimeToCurrent();
        userMapper.updateUser(user);

        LoginResult loginResult = LoginResult
                .builder()
                .accessToken(generateAccessToken(user.getId()))
                .refreshToken(getOrCreateRefreshToken(user.getId()))
                .build();

        return AuthConverter.INSTANCE.toLoginResponse(loginResult);
    }

    private String generateAccessToken(Integer adminId) {
        return userAccessJwtGenerator.generateToken(adminId);
    }

    private String getOrCreateRefreshToken(Integer userId) throws IOException {
        String refreshToken = getRefreshToken(userId);
        if (!isDBTokenExpired(refreshToken)) {
            return refreshToken;
        }

        return createAndSetRefreshToken(userId);
    }

    private String getRefreshToken(Integer userId) throws IOException {
        String refreshToken = redisAuthenticationMapper.getRefreshToken(userId);
        return refreshToken;
    }

    private String createAndSetRefreshToken(Integer userId) {
        String newRefreshToken = generateRefreshToken(userId);
        setRefreshTokenToRedis(newRefreshToken, userId);
        return newRefreshToken;
    }

    private String generateRefreshToken(Integer userId) {
        return userRefreshJwtGenerator.generateToken(userId);
    }

    private boolean isDBTokenExpired(String refreshToken) {
        return (refreshToken == null || userRefreshJwtGenerator.isExpired(refreshToken));
    }

    private void setRefreshTokenToRedis(String accessToken, Integer userId) {
        redisAuthenticationMapper.setRefreshToken(accessToken, userId);
    }

    @Override
    public void logout() {
        User user = jwtValidator.validate();
        deleteRefreshTokenInDB(user.getId());
    }


//    @Override
//    public Map<String, Object> getUserListForAdmin(Criteria criteria) {
//        Integer totalCount = userMapper.getTotalCount();
//        //int totalPage = criteria.calcTotalPage(totalCount);
//        Map<String, Object> map = new HashMap<>();
//
//        Users userListForAdmin = userMapper.getUserListForAdmin(criteria.getCursor(), criteria.getLimit());
//        map.put("items", userListForAdmin);
//        //map.put("totalPage", totalPage);
//
//        return map;
//    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUserListForAdmin(UserCriteria userCriteria) throws Exception {
        return userMapper.getUserListForAdmin(userCriteria.getCursor(), userCriteria.getLimit(), userCriteria.getUserType().name());
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
    public StudentResponse getStudent(Integer userId) {
        User user = Optional.ofNullable(adminUserMapper.getUserById(userId)).orElseThrow(() -> new BaseException(INQUIRED_USER_NOT_FOUND));

        if (!user.isStudent()) {
            throw new BaseException(NOT_STUDENT);
        }
        return StudentConverter.INSTANCE.toStudentResponse((Student) user);
    }

    @Override
    public Student createStudentForAdmin(Student student) {
        // 가입되어 있는 계정이거나, 메일 인증을 아직 하지 않은 경우 가입 요청중인 계정이 디비에 존재하는 경우 예외처리
        // TODO: 메일 인증 하지 않은 경우 조건 추가
        EmailAddress studentEmail = EmailAddress.from(student.getEmail());
        studentEmail.validatePortalEmail();

        if(userMapper.isEmailAlreadyExist(studentEmail).equals(true)){
            throw new NotFoundException(new ErrorMessage("already exists", 0));
        }

        // 닉네임 중복 체크
        if (student.getNickname() != null) {
            if (userMapper.isNicknameAlreadyUsed(student.getNickname()) > 0){
                throw new ConflictException(new ErrorMessage("nickname duplicate", 1));
            }
        }

        // 학번 유효성 체크
        if (student.getStudent_number() != null && !UserCode.isValidatedStudentNumber(student.getIdentity(), student.getStudent_number())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid student number", 2));
        }

        // 학과 유효성 체크
        if (student.getMajor() != null && !UserCode.isValidatedDeptNumber(student.getMajor())) {
            throw new PreconditionFailedException(new ErrorMessage("invalid dept code", 3));
        }

        student.setPassword(passwordEncoder.encode(student.getPassword()));
        student.setAnonymous_nickname("익명_" + (System.currentTimeMillis()));

        // TODO: default로 셋팅할 수 있는 방법 알아보기
        if (student.getIdentity() == null) {
            student.setIdentity(UserCode.UserIdentity.STUDENT.getIdentityType());
        }

        if (student.getIs_graduated() == null) {
            student.setIs_graduated(false);
        }

        if (student.getIs_authed() == null) {
            student.setIs_authed(false);
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
    public koreatech.in.dto.normal.user.student.response.StudentResponse updateStudentForAdmin(UpdateUserRequest updateUserRequest, int id) {
        User user =  userMapper.getUserById(id);
        if (!user.isStudent()) {
            throw new NotFoundException(new ErrorMessage("User is not Student", 0));
        }
        Student selectUser = (Student) user;
        Student student = updateUserRequest.toEntity();
        if(selectUser == null){
            throw new NotFoundException(new ErrorMessage("No User", 0));
        }

        student.setIdentity(selectUser.getIdentity());

        // 닉네임 중복 체크
        if (student.getNickname() != null) {
            User selectUser2 = userMapper.getUserByNickname(student.getNickname());
            if (selectUser2 != null && !selectUser.getId().equals(selectUser2.getId())) {
                throw new ConflictException(new ErrorMessage("nickname duplicate", 1));
            }
        }

        // 학번 유효성 체크
        if (student.getStudent_number() != null && !UserCode.isValidatedStudentNumber(student.getIdentity(), student.getStudent_number())) {
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

        return new koreatech.in.dto.normal.user.student.response.StudentResponse(student);
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = adminUserMapper.getUserById(userId);
        if (user == null) {
            throw new BaseException(INQUIRED_USER_NOT_FOUND);
        }

        user.checkPossibilityOfDeletion();

        deleteRefreshTokenInDB(userId);
        adminUserMapper.deleteUserLogicallyById(userId);
    }

    @Override
    public void undeleteUser(Integer userId) {
        User user = adminUserMapper.getUserById(userId);
        if (user == null) {
            throw new BaseException(INQUIRED_USER_NOT_FOUND);
        }

        user.checkPossibilityOfUndeletion(adminUserMapper.getUndeletedUserByEmail(user.getEmail()));

        adminUserMapper.undeleteUserLogicallyById(userId);
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
                    put("email", user.getEmail());
                }};

                adminToMap.put("users", userToMap);
            }
            listedAdmin.add(adminToMap);
        }
        map.put("admins", listedAdmin);
        map.put("totalPage", totalPage);

        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public NewOwnersResponse getNewOwners(NewOwnersCondition condition) {
        Integer totalCount = adminUserMapper.getTotalCountOfUnauthenticatedOwnersByCondition(condition);
        Integer totalPage = condition.extractTotalPage(totalCount);
        Integer currentPage = condition.getPage();

        if (currentPage > totalPage) {
            throw new BaseException(PAGE_NOT_FOUND);
        }

        List<Owner> owners = adminUserMapper.getUnauthenticatedOwnersByCondition(condition.getCursor(), condition);
        return NewOwnersResponse.of(totalCount, totalPage, currentPage, owners);
    }

    @Override
    @Transactional(readOnly = true)
    public OwnerResponse getOwner(int ownerId) {
        User user = Optional.ofNullable(adminUserMapper.getUserById(ownerId))
            .orElseThrow(() -> new BaseException(INQUIRED_USER_NOT_FOUND));

        if (!user.isOwner()) {
            throw new BaseException(NOT_OWNER);
        }
        Owner owner = (Owner)user;

        List<Integer> shopsId = adminUserMapper.getShopsIdByOwnerId(owner.getId());
        List<Integer> attachmentsId = adminUserMapper.getAttachmentsIdByOwnerId(owner.getId());

        return OwnerConverter.INSTANCE.toOwnerResponse((Owner) user, shopsId, attachmentsId);
    }

    @Override
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        RefreshToken refreshToken = AuthConverter.INSTANCE.toToken(request);

        User userInHeader = jwtValidator.validate();

        //어드민만 접근가능하므로, 어드민임을 다시 검증할 필요는 없다.

        Integer tokenUserId = userRefreshJwtGenerator.getFromToken(refreshToken.getToken());
        validateEqualUser(userInHeader, tokenUserId);

        String newToken = userAccessJwtGenerator.generateToken(tokenUserId);
        return AuthConverter.INSTANCE.toTokenRefreshResponse(newToken);
    }

    private void validateEqualUser(User userInHeader, Integer tokenUserId) {
        if(!userInHeader.hasSameId(tokenUserId)) {
            throw new BaseException(ExceptionInformation.BAD_ACCESS);
        }
    }

    private void deleteRefreshTokenInDB(Integer userId) {
        redisAuthenticationMapper.deleteRefreshToken(userId);
    }
}
