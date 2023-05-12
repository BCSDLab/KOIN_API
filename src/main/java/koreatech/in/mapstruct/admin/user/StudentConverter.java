package koreatech.in.mapstruct.admin.user;

import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.user.student.request.StudentUpdateRequest;
import koreatech.in.dto.admin.user.student.response.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentConverter {
    StudentConverter INSTANCE = Mappers.getMapper(StudentConverter.class);

    StudentResponse toStudentResponse(Student student);

    Student toStudent(StudentUpdateRequest student);
}
