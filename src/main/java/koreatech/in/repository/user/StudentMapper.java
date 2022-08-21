package koreatech.in.repository.user;

import koreatech.in.domain.user.student.Student;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentMapper {
    void insertStudent(Student student);
    Optional<Student> getStudentById(Integer id);
    Integer getStudentIdentity(Integer id);
    void deleteStudent(Integer id);
}
