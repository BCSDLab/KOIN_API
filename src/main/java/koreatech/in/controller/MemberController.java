package koreatech.in.controller;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.Homepage.Member;
import koreatech.in.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class MemberController {

    @Resource(name = "memberService")
    MemberService memberService;

    @RequestMapping(value = "/members", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMembers() throws Exception {
        return new ResponseEntity<List<Member>>(memberService.getMembers(), HttpStatus.OK);
    }

    @RequestMapping(value = "/members/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMemberById(@ApiParam(value = "멤버 id", required = true) @PathVariable(value = "id") Integer memberId) throws Exception {
        return new ResponseEntity<Member>(memberService.getMemberById(memberId), HttpStatus.OK);
    }
}
