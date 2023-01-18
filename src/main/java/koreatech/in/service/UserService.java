package koreatech.in.service;

import koreatech.in.dto.normal.user.request.LoginRequest;
import koreatech.in.dto.normal.user.request.StudentRegisterRequest;
import koreatech.in.dto.normal.user.request.UpdateUserRequest;
import koreatech.in.dto.normal.user.response.LoginResponse;
import koreatech.in.dto.normal.user.response.StudentResponse;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.student.Student;

import java.util.Map;

public interface UserService {
    LoginResponse login(LoginRequest request) throws Exception;

    void logout();

    Map<String, Object> StudentRegister(StudentRegisterRequest request, String host) throws Exception;

    Boolean authenticate(String authToken);

    Map<String, Object> changePasswordConfig(String account, String host);

    Boolean changePasswordInput(String resetToken);

    Boolean changePasswordAuthenticate(String password, String resetToken);

    void withdraw();

    Student getStudent() throws Exception;

    StudentResponse updateStudentInformation(UpdateUserRequest request) throws Exception;

    Map<String,Object> updateOwnerInformation(Owner owner) throws Exception;

    Map<String, Object> checkUserNickName(String nickname) throws Exception;
}
