package koreatech.in.controller.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.admin.member.request.CreateMemberRequest;
import koreatech.in.dto.admin.member.request.MembersCondition;
import koreatech.in.dto.admin.member.request.UpdateMemberRequest;
import koreatech.in.dto.admin.member.response.MemberResponse;
import koreatech.in.dto.admin.member.response.MembersResponse;
import koreatech.in.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

@Api(tags = "(Admin) Member", description = "BCSDLab 회원")
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.BCSDLAB)
@Controller
public class AdminMemberController {
    @Inject
    private MemberService memberService;

    @ApiOperation(value = "BCSDLab 회원 생성", code = 201, authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "요청한 트랙이 조회되지 않을 때 (error code: 201000)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)", response = ExceptionResponse.class)
    })
    @ParamValid
    @RequestMapping(value = "/admin/members", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createMember(@RequestBody @Valid CreateMemberRequest request, BindingResult bindingResult) throws Exception {
        memberService.createMemberForAdmin(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "BCSDLab 회원 조회", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "회원이 존재하지 않을 때 (error code: 202000)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/members/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<MemberResponse> getMember(@ApiParam(value = "고유 id", required = true) @PathVariable("id") Integer memberId) throws Exception {
        MemberResponse response = memberService.getMemberForAdmin(memberId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "페이지별 BCSDLab 회원 리스트 조회", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "유효하지 않은 페이지일 때 (error code: 100002)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/members", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<MembersResponse> getMembers(MembersCondition condition) throws Exception {
        MembersResponse response = memberService.getMembersForAdmin(condition);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "BCSDLab 회원 수정", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "회원이 존재하지 않을 때 (error code: 202000) \n\n" +
                                               "요청한 트랙이 조회되지 않을 때 (error code: 201000)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "요청 데이터 제약조건이 지켜지지 않았을 때 (error code: 100000)", response = ExceptionResponse.class)
    })
    @ParamValid
    @RequestMapping(value = "/admin/members/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<EmptyResponse> updateMember(
            @ApiParam(value = "고유 id", required = true) @PathVariable("id") Integer memberId,
            @RequestBody @Valid UpdateMemberRequest request, BindingResult bindingResult) throws Exception {
        memberService.updateMemberForAdmin(memberId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "BCSDLab 회원 삭제", notes = "soft delete 합니다.", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "회원이 존재하지 않을 때 (error code: 202000)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "이미 soft delete 되어있을 때 (error code: 202001)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/members/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteMember(@ApiParam(value = "고유 id", required = true) @PathVariable("id") Integer memberId) throws Exception {
        memberService.deleteMemberForAdmin(memberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "BCSDLab 회원 삭제 해제", notes = "soft delete 상태를 해제합니다.", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "회원이 존재하지 않을 때 (error code: 200000)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "회원이 삭제되어 있는 상태가 아닐 때 (error code: 202002)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/members/{id}/undelete", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> undeleteMember(@ApiParam(value = "고유 id", required = true) @PathVariable("id") Integer memberId) throws Exception {
        memberService.undeleteMemberForAdmin(memberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
