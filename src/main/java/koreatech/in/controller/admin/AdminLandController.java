package koreatech.in.controller.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.BokDuck.Land;
import koreatech.in.dto.SuccessCreateResponse;
import koreatech.in.dto.SuccessResponse;
import koreatech.in.dto.UploadImagesResponse;
import koreatech.in.dto.land.admin.request.CreateLandRequest;
import koreatech.in.dto.land.admin.request.LandsCondition;
import koreatech.in.dto.land.admin.response.LandResponse;
import koreatech.in.dto.land.admin.response.LandsResponse;
import koreatech.in.service.LandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.LAND)
@Controller
public class AdminLandController {
    @Inject
    private LandService landService;

    @ParamValid
    @ApiOperation(value = "복덕방 집 생성", authorizations = {@Authorization(value="Authorization")})
    @ApiResponses({
            @ApiResponse(code = 409, message = "이름이 중복될 때 (error code: 301)")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/admin/lands", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<SuccessCreateResponse> createLand(@RequestBody @Valid CreateLandRequest request, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<>(landService.createLandForAdmin(request), HttpStatus.CREATED);
    }

    @ApiOperation(value = "복덕방 집 단건 조회", authorizations = {@Authorization(value="Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "존재하지 않는 집일 때 (error code: 300)")
    })
    @RequestMapping(value = "/admin/lands/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<LandResponse> getLand(@ApiParam(value = "고유 id", required = true) @PathVariable("id") Integer landId) throws Exception {

        return new ResponseEntity<>(landService.getLandForAdmin(landId), HttpStatus.OK);
    }

    @ApiOperation(value = "복덕방 페이지별 리스트 조회", authorizations = {@Authorization(value="Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "유효하지 않은 페이지일 때 (error code: 1)"),
            @ApiResponse(code = 409, message = "검색 문자열이 공백 문자로만 이루어져 있을 때 (error code: 2)")
    })
    @RequestMapping(value = "/admin/lands", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<LandsResponse> getLands(LandsCondition condition) throws Exception {

        return new ResponseEntity<>(landService.getLandsForAdmin(condition), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lands/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateLand(@ApiParam(value = "(optional: name, size, room_type, floor, latitude, longitude, phone, image_urls, address, description, deposit, monthly_fee, charter_fee, management_fee, opt_refrigerator, opt_closet, opt_tv, opt_microwave, opt_gas_range, opt_induction, opt_water_purifier, opt_air_conditioner, opt_washer, opt_bed, opt_desk, opt_shoe_closet, opt_electronic_door_locks, opt_bidet, opt_veranda, opt_elevator)", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Land land, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Land>(landService.updateLandForAdmin(land, id), HttpStatus.CREATED);
    }

    @ApiOperation(value = "복덕방 집 삭제", notes = "복덕방 집을 soft delete 합니다.", authorizations = {@Authorization(value="Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "존재하지 않는 집일 때 (error code: 300)"),
            @ApiResponse(code = 409, message = "이미 soft delete 되어있는 집일 때 (error code: 302)")
    })
    @RequestMapping(value = "/admin/lands/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<SuccessResponse> deleteLand(@ApiParam(value = "고유 id", required = true) @PathVariable("id") Integer landId) throws Exception {

        return new ResponseEntity<>(landService.deleteLandForAdmin(landId), HttpStatus.OK);
    }

    @ApiOperation(value = "복덕방 집 삭제 해제", notes = "복덕방 집의 soft delete 상태를 해제합니다.", authorizations = {@Authorization(value="Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "존재하지 않는 집일 때 (error code: 300)"),
            @ApiResponse(code = 409, message = "soft delete 되어있는 집이 아닐 때 (error code: 303)")
    })
    @RequestMapping(value = "/admin/lands/{id}/undelete", method = RequestMethod.PATCH)
    public @ResponseBody
    ResponseEntity<SuccessResponse> undeleteLand(@ApiParam(value = "고유 id", required = true) @PathVariable(value = "id") Integer landId) throws Exception {

        return new ResponseEntity<>(landService.undeleteLandForAdmin(landId), HttpStatus.OK);
    }

    @ApiOperation(value = "복덕방 이미지 업로드 (다중 업로드 가능)"
                , notes = "Swagger에서는 다중 파일 업로드가 되지 않아 Postman으로 테스트 바랍니다. ([링크](https://docs.google.com/document/d/1ReyohfSr-NuNWc25TN_sd_VDOjZ0sDje-eNGQvkFjWY/edit))"
                , authorizations = {@Authorization(value="Authorization")})
    @ApiResponses({
            @ApiResponse(code = 409, message = "한번에 업로드할 수 있는 최대 개수를 초과하였을 때 (error code: 4)")
    })
    @RequestMapping(value = "/admin/lands/images", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<UploadImagesResponse> uploadImages(@ApiParam(value = "이미지 파일 리스트", required = true) @RequestPart("images") List<MultipartFile> images) throws Exception {

        return new ResponseEntity<>(landService.uploadImages(images), HttpStatus.CREATED);
    }
}
