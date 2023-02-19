package koreatech.in.service;

import java.util.Map;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.dto.normal.user.request.CheckExistsEmailRequest;
import koreatech.in.dto.normal.user.request.FindPasswordRequest;
import koreatech.in.dto.normal.user.request.LoginRequest;
import koreatech.in.dto.normal.user.request.StudentUpdateRequest;
import koreatech.in.dto.normal.user.response.LoginResponse;
import koreatech.in.dto.normal.user.student.request.StudentRegisterRequest;
import koreatech.in.dto.normal.user.student.response.StudentResponse;

public interface UserService {
    LoginResponse login(LoginRequest request) throws Exception;

    void logout();

    void StudentRegister(StudentRegisterRequest request, String host);

    StudentResponse getStudent();

    StudentResponse updateStudent(StudentUpdateRequest studentUpdateRequest);

    Map<String,Object> updateOwnerInformation(Owner owner) throws Exception;

    void withdraw();

    void checkUserNickname(String nickname);

    void changePasswordConfig(FindPasswordRequest request, String host);

    Boolean authenticate(String authToken);

    Boolean changePasswordInput(String resetToken);

    Boolean changePasswordAuthenticate(String password, String resetToken);

    void checkExists(CheckExistsEmailRequest checkExistsEmailRequest);
}
