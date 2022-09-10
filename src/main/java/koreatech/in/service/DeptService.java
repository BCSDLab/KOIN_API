package koreatech.in.service;

import koreatech.in.domain.Dept.DeptInfo;

import java.util.List;

public interface DeptService {

    List<DeptInfo> getAllDeptInfo();

    DeptInfo findDept(String deptNum, String name);
}
