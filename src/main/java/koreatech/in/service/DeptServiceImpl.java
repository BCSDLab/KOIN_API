package koreatech.in.service;

import koreatech.in.domain.Dept.DeptInfo;
import koreatech.in.repository.DeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public List<DeptInfo> getAllDeptInfo() {
        return deptMapper.getAllDeptInfo();
    }

    @Override
    public DeptInfo findDept(String deptNum, String name) {
        if (deptNum != null && name != null) {
            return deptMapper.findDeptByDeptNumAndName(deptNum, name);
        } else if (deptNum == null) {
            return deptMapper.findDeptByName(name);
        }
        return deptMapper.findDeptByDeptNum(deptNum);
    }
}
