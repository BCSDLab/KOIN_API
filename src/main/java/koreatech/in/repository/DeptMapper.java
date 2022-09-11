package koreatech.in.repository;

import koreatech.in.domain.Dept.DeptInfoWithNum;
import koreatech.in.domain.Dept.DeptNum;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface DeptMapper {

    @Select("SELECT a.*, b.dept_num FROM koin.dept_infos AS a " +
            "INNER JOIN koin.dept_nums AS b " +
            "ON a.name = b.dept_name AND a.is_deleted = 0 " +
            "ORDER BY b.dept_num")
    ArrayList<DeptInfoWithNum> getAllDeptInfo();

    @Select("SELECT a.* FROM koin.dept_nums AS a " +
            "JOIN koin.dept_infos AS b " +
            "ON a.dept_name = b.name AND b.is_deleted = 0 " +
            "WHERE a.dept_num = #{deptNum}")
    DeptNum findDeptByDeptNum(@Param("deptNum") String deptNum);
}
