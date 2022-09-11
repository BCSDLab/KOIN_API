package koreatech.in.service;

import koreatech.in.domain.Dept.DeptInfoVO;
import koreatech.in.domain.Dept.DeptInfoWithNum;
import koreatech.in.domain.Dept.DeptNum;
import koreatech.in.repository.DeptMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public ArrayList<DeptInfoVO> getAllDeptInfo() {

        ArrayList<DeptInfoWithNum> deptInfoWithNumList = deptMapper.getAllDeptInfo();
        HashMap<String, DeptInfoVO> hashedName = new HashMap<>();
        deptInfoWithNumList.forEach(item -> {
            if (hashedName.containsKey(item.getName())) {
                hashedName.get(item.getName()).getDept_nums().add(item.getDept_num());
                return;
            }

            hashedName.put(item.getName(), new DeptInfoVO(item.getName(), item.getCurriculum_link(), new ArrayList<String>() {{
                add(item.getDept_num());
            }}));
        });

        return new ArrayList<>(hashedName.values());
    }

    @Override
    public DeptNum findDept(String deptNum) {

        return deptMapper.findDeptByDeptNum(deptNum);
    }
}
