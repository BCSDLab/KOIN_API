package koreatech.in.mapstruct;

import koreatech.in.domain.User.Domain;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.LocalParts;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.normal.user.request.CheckExistsEmailRequest;
import koreatech.in.dto.normal.user.response.StudentResponse;
import koreatech.in.dto.normal.user.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);


    @Mappings({
            @Mapping(source = "address", target = "localParts", qualifiedByName = "convertLocalParts"),
            @Mapping(source = "address", target = "domain", qualifiedByName = "convertDomain")
    })
    EmailAddress toEmailAddress(CheckExistsEmailRequest checkExistsEmailRequest);

    @Named("convertLocalParts")
    default LocalParts convertLocalParts(String address) {
        return LocalParts.from(address);
    }

    @Named("convertDomain")
    default Domain convertDomain(String address) {
        return Domain.from(address);
    }



    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "nickname", target = "nickname"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "phone_number", target = "phoneNumber"),
            @Mapping(source = "gender", target = "gender"),
            @Mapping(source = "is_authed", target = "isAuth"),
            @Mapping(source = "user_type", target = "userType"),
            @Mapping(source = "is_deleted", target = "isDeleted")
    })
    @SubclassMapping(source = Student.class, target = StudentResponse.class)
    UserResponse toUserResponse(User user);

    @Mappings({
            @Mapping(source = "anonymous_nickname", target = "anonymousNickname"),
            @Mapping(source = "student_number", target = "studentNumber"),
            @Mapping(source = "major", target = "major"),
            @Mapping(source = "identity", target = "identity"),
            @Mapping(source = "is_graduated", target = "isGraduated"),
            @Mapping(source = "phone_number", target = "phoneNumber")
    })
    StudentResponse toStudentResponse(Student student);

}
