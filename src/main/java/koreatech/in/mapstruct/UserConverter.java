package koreatech.in.mapstruct;

import koreatech.in.domain.User.Domain;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.LocalParts;
import koreatech.in.domain.User.student.Student;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.normal.user.request.CheckExistsEmailRequest;
import koreatech.in.dto.normal.user.request.StudentUpdateRequest;
import koreatech.in.dto.normal.user.student.response.StudentResponse;
import koreatech.in.dto.normal.user.request.StudentRegisterRequest;
import koreatech.in.dto.normal.user.request.UserRegisterRequest;
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

//    @Mappings({
//            @Mapping(source = "email", target = "email"),
//            @Mapping(source = "nickname", target = "nickname"),
//            @Mapping(source = "name", target = "name"),
//            @Mapping(source = "gender", target = "gender")
//    })
//    @SubclassMapping(source = Student.class, target = StudentResponse.class)
//    UserResponse toUserResponse(User user);

    @Mappings({
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "name", target = "name"),

            @Mapping(source = "nickname", target = "nickname"),
            @Mapping(source = "gender", target = "gender"),
            @Mapping(source = "anonymous_nickname", target = "anonymousNickname"),
            @Mapping(source = "phone_number", target = "phoneNumber"),
            @Mapping(source = "student_number", target = "studentNumber"),
            @Mapping(source = "major", target = "major")
    })
    StudentResponse toStudentResponse(Student student);

    @Mappings({
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "name", target = "name"),

            @Mapping(source = "nickname", target = "nickname"),
            @Mapping(source = "gender", target = "gender"),
            @Mapping(source = "phoneNumber", target = "phone_number"),
            @Mapping(source = "studentNumber", target = "student_number"),
            @Mapping(source = "major", target = "major")
    })
    Student toStudent(StudentUpdateRequest studentUpdateRequest);



    @Mappings({
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "name", target = "name")
    })
    @SubclassMapping(source = StudentRegisterRequest.class, target = Student.class)
    User toUser(UserRegisterRequest userRegisterRequest);

    @Mappings({
            @Mapping(source = "nickname", target = "nickname"),
            @Mapping(source = "gender", target = "gender"),
            @Mapping(source = "isGraduated", target = "isGraduated"),
            @Mapping(source = "major", target = "major"),
            @Mapping(source = "studentNumber", target = "studentNumber"),
            @Mapping(source = "phoneNumber", target = "phoneNumber"),
    })
    Student toStudent(StudentRegisterRequest studentRegisterRequest);

}
