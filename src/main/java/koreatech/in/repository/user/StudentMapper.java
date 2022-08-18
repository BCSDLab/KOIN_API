package koreatech.in.repository.user;

import koreatech.in.domain.user.student.Student;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentMapper {
    void createStudent();
    Optional<Student> getStudentById(Long id);
    Integer getStudentIdentity(Long id);
}
