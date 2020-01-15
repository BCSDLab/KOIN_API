package koreatech.in.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Homepage.Member;
import koreatech.in.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.BCSDLAB)
@Controller
public class AdminMemberController {
    @Inject
    private MemberService memberService;

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/members", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMembers() throws Exception {
        return new ResponseEntity<List<Member>>(memberService.getMembersForAdmin(), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/members/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMember(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Member>(memberService.getMemberForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/members", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createMember(@ApiParam(value = "(required:name, track, position), TrackName Example: BackEnd, FrontEnd, Android, UI/UX, ...", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Member member, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<Member>(memberService.createMemberForAdmin(member), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/members/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateMember(@ApiParam(value = "", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Member member, BindingResult bindingResult, @ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Member>(memberService.updateMemberForAdmin(member, id), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/members/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteMember(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Map<String, Object>>(memberService.deleteMemberForAdmin(id), HttpStatus.OK);
    }
}
