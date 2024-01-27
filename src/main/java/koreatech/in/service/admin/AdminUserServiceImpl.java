package koreatech.in.service.admin;

import static koreatech.in.domain.DomainToMap.domainToMap;
import static koreatech.in.exception.ExceptionInformation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import koreatech.in.domain.Auth.LoginResult;
import koreatech.in.domain.Auth.RefreshResult;
import koreatech.in.domain.Auth.RefreshToken;
import koreatech.in.domain.Authority;
import koreatech.in.domain.Criteria.StudentCriteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.PageInfo;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.UserCode;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerIncludingShop;
import koreatech.in.domain.User.owner.OwnerShop;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.auth.TokenRefreshRequest;
import koreatech.in.dto.admin.auth.TokenRefreshResponse;
import koreatech.in.dto.admin.user.owner.request.OwnerUpdateRequest;
import koreatech.in.dto.admin.user.owner.response.OwnerUpdateResponse;
import koreatech.in.dto.admin.user.owner.response.OwnersResponse;
import koreatech.in.dto.admin.user.request.LoginRequest;
import koreatech.in.dto.admin.user.request.NewOwnersCondition;
import koreatech.in.dto.admin.user.request.OwnersCondition;
import koreatech.in.dto.admin.user.response.LoginResponse;
import koreatech.in.dto.admin.user.response.NewOwnersResponse;
import koreatech.in.dto.admin.user.response.OwnerResponse;
import koreatech.in.dto.admin.user.student.request.StudentUpdateRequest;
import koreatech.in.dto.admin.user.student.response.StudentResponse;
import koreatech.in.dto.admin.user.student.response.StudentUpdateResponse;
import koreatech.in.dto.admin.user.student.response.StudentsResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ConflictException;
import koreatech.in.exception.NotFoundException;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.mapstruct.admin.auto.AuthConverter;
import koreatech.in.mapstruct.admin.user.OwnerConverter;
import koreatech.in.mapstruct.admin.user.StudentConverter;
import koreatech.in.mapstruct.admin.user.UserConverter;
import koreatech.in.repository.AuthenticationMapper;
import koreatech.in.repository.AuthorityMapper;
import koreatech.in.repository.RedisOwnerMapper;
import koreatech.in.repository.admin.AdminShopMapper;
import koreatech.in.repository.admin.AdminUserMapper;
import koreatech.in.repository.user.StudentMapper;
import koreatech.in.repository.user.UserMapper;
import koreatech.in.service.JwtValidator;
import koreatech.in.service.RefreshJwtValidator;
import koreatech.in.service.UserAccessJwtGenerator;
import koreatech.in.service.UserRefreshJwtGenerator;
import koreatech.in.util.StringRedisUtilObj;

@Service
@Transactional
public class AdminUserServiceImpl implements AdminUserService {
    public static final String OWNER_SHOP_REDIS_PREFIX = "owner_shop@";
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private AdminShopMapper adminShopMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    private JwtValidator jwtValidator;

    @Autowired
    private RefreshJwtValidator refreshJwtValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAccessJwtGenerator userAccessJwtGenerator;

    @Autowired
    private UserRefreshJwtGenerator userRefreshJwtGenerator;

    @Autowired
    private AuthenticationMapper authenticationMapper;

    @Autowired
    private StringRedisUtilObj stringRedisUtilObj;

    @Autowired
    private RedisOwnerMapper redisOwnerMapper;

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
                .refreshToken(getRefreshToken(user.getId()))
                .build();

        return AuthConverter.INSTANCE.toLoginResponse(loginResult);
    }

    private String generateAccessToken(Integer adminId) {
        return userAccessJwtGenerator.generate(adminId);
    }

    private String getRefreshToken(Integer userId) throws IOException {
        String refreshToken = authenticationMapper.getRefreshToken(userId);
        if (isExpired(refreshToken)) {
            return generateRefreshToken(userId);
        }

        return refreshToken;
    }

    private String generateRefreshToken(Integer userId) {
        String newRefreshToken = userRefreshJwtGenerator.generate(userId);
        authenticationMapper.setRefreshToken(newRefreshToken, userId);

        return newRefreshToken;
    }

    private boolean isExpired(String refreshToken) {
        return (refreshToken == null || refreshJwtValidator.isExpiredToken(refreshToken));
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
    public StudentsResponse getStudents(StudentCriteria criteria) throws Exception {
        Integer totalCount = adminUserMapper.getTotalCountOfStudentsByCondition(criteria);
        Integer totalPage = criteria.extractTotalPage(totalCount);
        Integer currentPage = criteria.getPage();

        if (currentPage > totalPage) {
            throw new BaseException(PAGE_NOT_FOUND);
        }

        List<Student> students = adminUserMapper.getStudentsByCondition(criteria.getCursor(), criteria);
        return StudentsResponse.of(totalCount, totalPage, currentPage, students);
    }

    @Override
    public User getUserForAdmin(int id) {
        User user = userMapper.getUserById(id);

        if (user == null) {
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
    @Transactional
    public void allowOwnerPermission(Integer ownerId) {
        User user = Optional.ofNullable(adminUserMapper.getUserById(ownerId)).orElseThrow(() -> new BaseException(INQUIRED_USER_NOT_FOUND));

        if (!user.isOwner()) {
            throw new BaseException(NOT_OWNER);
        }
        if (user.isAuthenticated()) {
            throw new BaseException(AUTHENTICATED_USER);
        }
        Integer shopId=getShopIdFromRedis(ownerId);
        if (shopId != null) {
            updateShopOwnerId(ownerId, shopId);
        }
        adminUserMapper.updateOwnerAuthorById(ownerId);
        adminUserMapper.updateOwnerGrantShopByOwnerId(ownerId);
        redisOwnerMapper.removeRedisFrom(getKeyForRedis(ownerId));
    }

    private Integer getShopIdFromRedis(Integer ownerId) {
        OwnerShop ownerShop;
        try {
            ownerShop = (OwnerShop) stringRedisUtilObj.getDataAsString(getKeyForRedis(ownerId), OwnerShop.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (ownerShop != null) {
            return ownerShop.getShop_id();
        }
        return null;
    }

    private void updateShopOwnerId(Integer ownerId, Integer shopId) {
        Optional.ofNullable(adminShopMapper.getShopById(shopId)).orElseThrow(() -> new BaseException(SHOP_NOT_FOUND));
        adminShopMapper.updateShopOwnerId(ownerId, shopId);
    }

    @Override
    public Student createStudentForAdmin(Student student) {
        // 가입되어 있는 계정이거나, 메일 인증을 아직 하지 않은 경우 가입 요청중인 계정이 디비에 존재하는 경우 예외처리
        // TODO: 메일 인증 하지 않은 경우 조건 추가
        EmailAddress studentEmail = EmailAddress.from(student.getEmail());
        studentEmail.validatePortalEmail();

        if (userMapper.isEmailAlreadyExist(studentEmail).equals(true)) {
            throw new NotFoundException(new ErrorMessage("already exists", 0));
        }

        // 닉네임 중복 체크
        if (student.getNickname() != null) {
            if (userMapper.isNicknameAlreadyUsed(student.getNickname()) > 0) {
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
    public StudentUpdateResponse updateStudent(StudentUpdateRequest studentUpdateRequest, int id) {
        User user = Optional.ofNullable(adminUserMapper.getUserById(id)).orElseThrow(() -> new BaseException(USER_NOT_FOUND));

        if (!user.isStudent()) {
            throw new BaseException(NOT_STUDENT);
        }

        Student selectUser = (Student) user;
        Student student = StudentConverter.INSTANCE.toStudent(studentUpdateRequest);
        student.setId(id);

        student.setIdentity(selectUser.getIdentity());//identity 수정 막음.
        student.setIs_graduated(selectUser.getIs_graduated());//is_graduated 수정 막음.
        student.setIs_authed(selectUser.getIs_authed());

        validateRequest(student, selectUser);

        if (student.getPassword() != null) {
            student.setPassword(passwordEncoder.encode(student.getPassword()));
        }

        selectUser.update(student);
        userMapper.updateUser(selectUser);
        studentMapper.updateStudent(selectUser);

        return StudentConverter.INSTANCE.toStudentUpdateResponse(selectUser);
    }

    private void validateRequest(Student student, Student selectUser) {
        if (student.getGender() != null && !student.getGender().equals(selectUser.getGender())) {
            validateGender(student);
        }
        if (student.getNickname() != null && !student.getNickname().equals(selectUser.getNickname())) {
            validateNicknameDuplication(student);
        }
        if (student.getStudent_number() != null && !student.getStudent_number().equals(selectUser.getStudent_number())) {
            validateStudentNumber(student);
        }
        if (student.getMajor() != null && !student.getMajor().equals(selectUser.getMajor())) {
            validateMajor(student);
        }
    }

    private void validateGender(Student student) {
        if (student.getGender() != null && !(student.getGender() == 0 || student.getGender() == 1)) {
            throw new BaseException(GENDER_INVALID);
        }
    }

    private void validateNicknameDuplication(Student student) {
        Optional.ofNullable(student.getNickname())
                .filter(nickname -> adminUserMapper.getNicknameUsedCount(nickname, student.getId()) > 0)
                .ifPresent(nickname -> {
                    throw new BaseException(NICKNAME_DUPLICATE);
                });
    }

    private void validateStudentNumber(Student student) {
        if (student.getStudent_number() != null && !UserCode.isValidatedStudentNumber(0, student.getStudent_number())) {//현재 identity를 사용하지 않기 때문에 기존 코드를 위해 재학생 코드인 0으로 할당.
            throw new BaseException(STUDENT_NUMBER_INVALID);
        }
    }

    private void validateMajor(Student student) {
        if (student.getMajor() != null && !UserCode.isValidatedDeptNumber(student.getMajor())) {
            throw new BaseException(STUDENT_MAJOR_INVALID);
        }
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
        int totalCount = adminUserMapper.getTotalCountOfUnauthenticatedOwnersByCondition(condition);

        List<OwnerIncludingShop> unauthenticatedOwners = adminUserMapper.getUnauthenticatedOwnersByCondition(condition);

        enrichOwnerFromRedis(unauthenticatedOwners);

        PageInfo pageInfo = UserConverter.INSTANCE.toPageInfo(condition, totalCount, unauthenticatedOwners.size());

        List<NewOwnersResponse.NewOwner> newOwner = OwnerConverter.INSTANCE.toNewOwnerResponse$NewOwners(unauthenticatedOwners);

        NewOwnersResponse response = OwnerConverter.INSTANCE.toNewOwnersResponse(pageInfo, newOwner);

        return response;
    }

    private void enrichOwnerFromRedis(List<OwnerIncludingShop> owners) {
        for (OwnerIncludingShop owner : owners) {
            OwnerShop ownerShop;

            try {
               ownerShop = (OwnerShop) stringRedisUtilObj.getDataAsString(getKeyForRedis(owner.getId()), OwnerShop.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (ownerShop != null) {
                owner.setShop_id(ownerShop.getShop_id());
                owner.setShop_name(ownerShop.getShop_name());
            }
        }
    }

    private String getKeyForRedis(Integer id) {
        return OWNER_SHOP_REDIS_PREFIX + id;
    }

    @Override
    @Transactional
    public OwnersResponse getOwners(OwnersCondition condition) {
        int totalCount = adminUserMapper.getTotalCountOfOwnersByCondition(condition);

        List<Owner> ownersByCondition = adminUserMapper.getOwnersByCondition(condition);

        PageInfo pageInfo = UserConverter.INSTANCE.toPageInfo(condition, totalCount, ownersByCondition.size());

        List<OwnersResponse.Owner> owners = OwnerConverter.INSTANCE.toOwnersResponse$Owners(ownersByCondition);

        OwnersResponse response = OwnerConverter.INSTANCE.toOwnersResponse(pageInfo, owners);

        return response;
    }

    private void enrichAuthedOwnersFromDB(List<Owner> owners) {
        for (Owner owner : owners) {
            List<Shop> shops = adminShopMapper.getShopsByOwnerId(owner.getId());

            if (shops != null) {
                owner.setShops(shops);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OwnerResponse getOwner(int ownerId) {
        User user = Optional.ofNullable(adminUserMapper.getUserById(ownerId))
                .orElseThrow(() -> new BaseException(INQUIRED_USER_NOT_FOUND));

        if (!user.isOwner()) {
            throw new BaseException(NOT_OWNER);
        }
        Owner owner = (Owner) user;

        List<Integer> shopsId = getShopIdsFromRedisOrDatabase(owner.getId());
        List<String> attachmentsUrl = adminUserMapper.getAttachmentsUrlByOwnerId(owner.getId());

        return OwnerConverter.INSTANCE.toOwnerResponse((Owner) user, shopsId, attachmentsUrl);
    }

    private List<Integer> getShopIdsFromRedisOrDatabase(Integer id) {
        OwnerShop ownerShop;
        List<Integer> shopsId;

        shopsId = adminUserMapper.getShopsIdByOwnerId(id);

        if (shopsId == null || shopsId.isEmpty()){
            try {
                ownerShop = (OwnerShop) stringRedisUtilObj.getDataAsString(getKeyForRedis(id), OwnerShop.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            shopsId= Collections.singletonList(ownerShop.getShop_id());
        }

        return shopsId;
    }

    @Override
    public OwnerUpdateResponse updateOwner(Integer userId, OwnerUpdateRequest request) {
        User user = Optional.ofNullable(adminUserMapper.getUserById(userId))
                .orElseThrow(() -> new BaseException(INQUIRED_USER_NOT_FOUND));

        if (!user.isOwner()) {
            throw new BaseException(NOT_OWNER);
        }

        Owner existingOwner = (Owner) user;
        Owner owner = OwnerConverter.INSTANCE.toOwner(request);

        existingOwner.setId(userId);
        existingOwner.setUser_id(userId);//id의 값이 null이므로 user_id로 값을 변경해줌.
        owner.setUser_id(userId);

        validateRequest(owner, existingOwner);
        existingOwner.update(owner);
        updateInDBFor(existingOwner);

        return OwnerConverter.INSTANCE.toOwnerUpdateResponse(existingOwner);
    }

    private void updateInDBFor(Owner owner) {
        adminUserMapper.updateOwner(owner);
        adminUserMapper.updateUser(owner);
    }

    private void validateRequest(Owner owner, Owner existingOwner) {
        if (owner.getGender() != null && !owner.getGender().equals(existingOwner.getGender())) {
            validateGender(owner);
        }
        if (owner.getEmail() != null && !owner.getEmail().equals(existingOwner.getEmail())) {
            validateEmailUniqueness(owner);
        }
        if (owner.getNickname() != null && !owner.getNickname().equals(existingOwner.getNickname())) {
            validateNicknameUniqueness(owner);
        }
        if (owner.getCompany_registration_number() != null && !owner.getCompany_registration_number().equals(existingOwner.getCompany_registration_number())) {
            validateCompanyRegistrationNumberUniqueness(owner);
        }
    }

    private void validateEmailUniqueness(Owner owner) {
        Optional.ofNullable(owner.getEmail())
                .filter(email -> adminUserMapper.getEmailUsedCount(email, owner.getUser_id()) > 0)
                .ifPresent(email -> {
                    throw new BaseException(EMAIL_DUPLICATED);
                });
    }

    private void validateNicknameUniqueness(Owner owner) {
        Optional.ofNullable(owner.getNickname())
                .filter(nickname -> adminUserMapper.getNicknameUsedCount(nickname, owner.getUser_id()) > 0)
                .ifPresent(nickname -> {
                    throw new BaseException(NICKNAME_DUPLICATE);
                });
    }

    private void validateCompanyRegistrationNumberUniqueness(Owner owner) {
        Optional.ofNullable(owner.getCompany_registration_number())
                .filter(registrationNumber -> adminUserMapper.getCompanyRegistrationNumberUsedCount(registrationNumber, owner.getUser_id()) > 0)
                .ifPresent(registrationNumber -> {
                    throw new BaseException(COMPANY_REGISTRATION_NUMBER_DUPLICATE);
                });
    }

    private void validateGender(Owner owner) {
        Optional.ofNullable(owner.getGender())
                .filter(gender -> !(gender == 0 || gender == 1))
                .ifPresent(gender -> {
                    throw new BaseException(GENDER_INVALID);
                });
    }

    @Override
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        RefreshToken refreshToken = AuthConverter.INSTANCE.toToken(request);

        Integer tokenUserId = refreshJwtValidator.getUserIdInToken(refreshToken.getToken());
        validateAdmin(tokenUserId);

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

    private void validateAdmin(Integer tokenUserId) {
        Optional.ofNullable(authorityMapper.getAuthorityByUserId(tokenUserId))
                .orElseThrow(() -> new BaseException(FORBIDDEN));
    }

    private User getUserById(int id) {
        return Optional.ofNullable(userMapper.getUserById(id))
                .orElseThrow(() -> new BaseException(USER_NOT_FOUND));
    }

    private void deleteRefreshTokenInDB(Integer userId) {
        authenticationMapper.deleteRefreshToken(userId);
    }


}
