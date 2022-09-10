package koreatech.in.repository;

import koreatech.in.domain.Dept.DeptInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptMapper {
    @Select("SELECT * FROM koin.departments WHERE is_deleted = 0")
    List<DeptInfo> getAllDeptInfo();

    @Select("SELECT * FROM koin.departments WHERE is_deleted = 0 AND dept_num = #{deptNum}")
    DeptInfo findDeptByDeptNum(@Param("deptNum") String deptNum);

    @Select("SELECT * FROM koin.departments WHERE is_deleted = 0 AND name = #{name}")
    DeptInfo findDeptByName(@Param("name") String name);

    @Select("SELECT * FROM koin.departments WHERE is_deleted = 0 AND dept_num = #{deptNum} AND name = #{name}")
    DeptInfo findDeptByDeptNumAndName(@Param("deptNum") String deptNum, @Param("name") String name);
}
