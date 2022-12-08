package koreatech.in.controller.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.SuccessCreateResponse;
import koreatech.in.dto.member.admin.request.CreateMemberRequest;
import koreatech.in.dto.member.admin.request.MembersCondition;
import koreatech.in.dto.member.admin.request.UpdateMemberRequest;
import koreatech.in.dto.member.admin.response.MemberResponse;
import koreatech.in.dto.member.admin.response.MembersResponse;
import koreatech.in.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Map;

// TODO: 응답 타입 전부 커스텀 DTO 클래스로 변경하기
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.BCSDLAB)
@Controller
public class AdminMemberController {
    @Inject
    private MemberService memberService;

    @ApiOperation(value = "BCSDLab 회원 페이지별 리스트 조회", authorizations = {@Authorization(value = "Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "유효하지 않은 페이지일 때"),
            @ApiResponse(code = 409, message = "검색 문자열이 공백 문자로만 이루어져 있을 때")
    })
    @RequestMapping(value = "/admin/members", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<MembersResponse> getMembers(MembersCondition condition) throws Exception {
        return new ResponseEntity<>(memberService.getMembersForAdmin(condition), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "회원이 존재하지 않을 때 (code: 200)")
    })
    @RequestMapping(value = "/admin/members/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<MemberResponse> getMember(@ApiParam(value = "고유 id", required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<>(memberService.getMemberForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "요청한 트랙이 조회되지 않을 때 (code: 201)")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/admin/members", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<SuccessCreateResponse> createMember(@RequestBody @Valid CreateMemberRequest request, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<>(memberService.createMemberForAdmin(request), HttpStatus.CREATED);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/members/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateMember(@ApiParam(required = true) @PathVariable("id") int id, @RequestBody @Valid UpdateMemberRequest request, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<Map<String, Object>>(memberService.updateMemberForAdmin(id, request), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/members/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteMember(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Map<String, Object>>(memberService.deleteMemberForAdmin(id), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/members/{id}/undelete", method = RequestMethod.PATCH)
    public @ResponseBody
    ResponseEntity undeleteMember(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Map<String, Object>>(memberService.undeleteMemberForAdmin(id), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/members/image", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity uploadProfileImage(@RequestPart(value = "image", required = false) MultipartFile image) throws Exception {
        return new ResponseEntity<Map<String, Object>>(memberService.uploadImage(image), HttpStatus.CREATED);
    }
}
