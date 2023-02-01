package koreatech.in.repository.user;

import koreatech.in.domain.User.student.Student;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentMapper {
    void insertStudent(Student student);
    Student getStudentById(Integer id);
    Integer getStudentIdentity(Integer id);
    void deleteStudent(Integer id);
    // TODO 쿼리 추가 필요
    void updateStudent(Student student);
}
