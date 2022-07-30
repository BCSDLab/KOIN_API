package koreatech.in.repository;

import koreatech.in.domain.Bus.Course;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository("busMapper")
public interface BusMapper {
    @Select("SELECT id, region, bus_type FROM koin.courses WHERE is_deleted = 0")
    ArrayList<Course> getCourses();
}
