package koreatech.in.mapstruct.admin.user;

import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.user.student.request.StudentUpdateRequest;
import koreatech.in.dto.admin.user.student.response.StudentResponse;
import koreatech.in.dto.admin.user.student.response.StudentUpdateResponse;
import koreatech.in.dto.admin.user.student.response.StudentsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentConverter {
    StudentConverter INSTANCE = Mappers.getMapper(StudentConverter.class);

    StudentResponse toStudentResponse(Student student);

    Student toStudent(StudentUpdateRequest student);

    @Mappings({
            @Mapping(source = "phone_number", target = "phoneNumber"),
            @Mapping(source = "student_number", target = "studentNumber"),
            @Mapping(source = "identity", target = "identity"),
            @Mapping(source = "is_graduated", target = "isGraduated")
    })
    StudentUpdateResponse toStudentUpdateResponse(Student student);

    StudentsResponse.Students toStudentsResponse$Students(Student student);
}
