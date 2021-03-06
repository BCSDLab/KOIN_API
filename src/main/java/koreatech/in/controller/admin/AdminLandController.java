package koreatech.in.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.BokDuck.Land;
import koreatech.in.service.LandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
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

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lands/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateLand(@ApiParam(value = "(optional: name, size, room_type, floor, latitude, longitude, phone, image_urls, address, description, deposit, monthly_fee, charter_fee, management_fee, opt_refrigerator, opt_closet, opt_tv, opt_microwave, opt_gas_range, opt_induction, opt_water_purifier, opt_air_conditioner, opt_washer, opt_bed, opt_desk, opt_shoe_closet, opt_electronic_door_locks, opt_bidet, opt_veranda, opt_elevator)", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Land land, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Land>(landService.updateLandForAdmin(land, id), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lands/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteLand(@ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(landService.deleteLandForAdmin(id), HttpStatus.OK);
    }
}
