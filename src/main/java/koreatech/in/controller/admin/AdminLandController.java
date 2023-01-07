package koreatech.in.controller.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.*;
import koreatech.in.dto.admin.land.request.CreateLandRequest;
import koreatech.in.dto.admin.land.request.LandsCondition;
import koreatech.in.dto.admin.land.request.UpdateLandRequest;
import koreatech.in.dto.admin.land.response.LandResponse;
import koreatech.in.dto.admin.land.response.LandsResponse;
import koreatech.in.service.LandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

@Api(tags = "(Admin) Land", description = "복덕방")
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.LAND)
@Controller
public class AdminLandController {
    @Inject
    private LandService landService;

    @ApiOperation(value = "집 생성", code = 201, authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 409, message = "이름이 중복될 때 (code: 107001)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "요청 데이터 제약조건이 지켜지지 않았을 때 (code: 100000)", response = ExceptionResponse.class)
    })
    @ParamValid
    @RequestMapping(value = "/admin/lands", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createLand(@RequestBody @Valid CreateLandRequest request, BindingResult bindingResult) throws Exception {
        landService.createLandForAdmin(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "집 조회", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "존재하지 않는 집일 때 (code: 107000)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/lands/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<LandResponse> getLand(@ApiParam(value = "고유 id", required = true) @PathVariable("id") Integer landId) throws Exception {
        LandResponse response = landService.getLandForAdmin(landId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "페이지별 집 리스트 조회", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "유효하지 않은 페이지일 때 (code: 100002)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "요청 데이터 제약조건이 지켜지지 않았을 때 (code: 100000)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/lands", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<LandsResponse> getLands(LandsCondition condition) throws Exception {
        LandsResponse response = landService.getLandsForAdmin(condition);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "집 수정", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "존재하지 않는 집일 때 (code: 107000)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "이름이 중복될 때 (code: 107001)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "요청 데이터 제약조건이 지켜지지 않았을 때 (code: 100000)", response = ExceptionResponse.class)
    })
    @ParamValid
    @RequestMapping(value = "/admin/lands/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<EmptyResponse> updateLand(
            @ApiParam(value = "고유 id", required = true) @PathVariable("id") Integer landId,
            @RequestBody @Valid UpdateLandRequest request, BindingResult bindingResult) throws Exception {
        landService.updateLandForAdmin(request, landId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "집 삭제", notes = "soft delete 합니다.", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "존재하지 않는 집일 때 (error code: 107000)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "이미 soft delete 되어있는 집일 때 (error code: 107002)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/lands/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteLand(@ApiParam(value = "고유 id", required = true) @PathVariable("id") Integer landId) throws Exception {
        landService.deleteLandForAdmin(landId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "집 삭제 해제", notes = "soft delete 상태를 해제합니다.", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "존재하지 않는 집일 때 (error code: 107000)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "soft delete 되어있는 집이 아닐 때 (error code: 107003)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/lands/{id}/undelete", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> undeleteLand(@ApiParam(value = "고유 id", required = true) @PathVariable(value = "id") Integer landId) throws Exception {
        landService.undeleteLandForAdmin(landId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
