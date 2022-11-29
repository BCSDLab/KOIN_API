package koreatech.in.controller;

import koreatech.in.domain.Dept.DeptInfoVO;
import koreatech.in.domain.Dept.DeptNum;
import koreatech.in.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DeptController {

    @Autowired
    private DeptService deptService;

    @RequestMapping(value = "/depts", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<DeptInfoVO>> getAllDeptInfo() {

        return new ResponseEntity<>(deptService.getAllDeptInfo(), HttpStatus.OK);
    }

    @RequestMapping(value = "/dept", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<DeptNum> findDept(@RequestParam(value = "dept_num") String deptNum) {

        return new ResponseEntity<>(deptService.findDept(deptNum), HttpStatus.OK);
    }
}
