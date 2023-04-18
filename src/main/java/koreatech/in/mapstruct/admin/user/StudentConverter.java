package koreatech.in.mapstruct.admin.user;

import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.user.student.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentConverter {
    StudentConverter INSTANCE = Mappers.getMapper(StudentConverter.class);

    StudentResponse toStudentResponse(Student student);
}
