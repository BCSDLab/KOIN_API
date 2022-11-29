package koreatech.in.service;

import koreatech.in.domain.Dept.DeptInfoVO;
import koreatech.in.domain.Dept.DeptNum;

import java.util.ArrayList;

public interface DeptService {

    ArrayList<DeptInfoVO> getAllDeptInfo();

    DeptNum findDept(String deptNum);
}
