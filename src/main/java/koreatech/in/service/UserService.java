package koreatech.in.service;

import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.normal.user.request.CheckExistsEmailRequest;
import koreatech.in.dto.normal.user.request.FindPasswordRequest;
import koreatech.in.dto.normal.user.request.LoginRequest;
import koreatech.in.dto.normal.user.request.StudentRegisterRequest;
import koreatech.in.dto.normal.user.request.UpdateUserRequest;
import koreatech.in.dto.normal.user.response.LoginResponse;
import koreatech.in.dto.normal.user.response.StudentResponse;
import koreatech.in.domain.User.owner.Owner;

import java.util.Map;

public interface UserService {
    LoginResponse login(LoginRequest request) throws Exception;

    void logout();

    Map<String, Object> StudentRegister(StudentRegisterRequest request, String host);

    Student getStudent();

    StudentResponse updateStudentInformation(UpdateUserRequest request);

    Map<String,Object> updateOwnerInformation(Owner owner) throws Exception;

    void withdraw();

    void checkUserNickname(String nickname);

    void changePasswordConfig(FindPasswordRequest request, String host);

    Boolean authenticate(String authToken);

    Boolean changePasswordInput(String resetToken);

    Boolean changePasswordAuthenticate(String password, String resetToken);

    void checkExists(CheckExistsEmailRequest checkExistsEmailRequest);
}
