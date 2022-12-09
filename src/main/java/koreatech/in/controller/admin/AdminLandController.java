package koreatech.in.controller.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.BokDuck.Land;
import koreatech.in.dto.SuccessResponse;
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
import java.util.List;
import java.util.Map;

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.LAND)
@Controller
public class AdminLandController {
    @Inject
    private LandService landService;

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lands", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createLand(@ApiParam(value = "(required: name), (optional: size, room_type, floor, latitude, longitude, phone, image_urls, address, description, deposit, monthly_fee, charter_fee, management_fee, opt_refrigerator, opt_closet, opt_tv, opt_microwave, opt_gas_range, opt_induction, opt_water_purifier, opt_air_conditioner, opt_washer, opt_bed, opt_desk, opt_shoe_closet, opt_electronic_door_locks, opt_bidet, opt_veranda, opt_elevator)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Land land, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<Land>(landService.createLandForAdmin(land), HttpStatus.CREATED);
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

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lands/{id}/undelete", method = RequestMethod.PATCH)
    public @ResponseBody
    ResponseEntity undeleteLand(@ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(landService.undeleteLandForAdmin(id), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lands/images", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity uploadImages(@RequestPart("images") List<MultipartFile> images) throws Exception {

        return new ResponseEntity<Map<String, Object>>(landService.uploadImages(images), HttpStatus.CREATED);
    }
}
