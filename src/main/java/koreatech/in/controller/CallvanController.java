package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Callvan.Company;
import koreatech.in.domain.Callvan.Participant;
import koreatech.in.domain.Callvan.Room;
import koreatech.in.service.CallvanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@ApiIgnore
@Api(tags = "(Normal) Callvan", description = "콜밴")
@Auth(role = Auth.Role.USER)
@Controller
public class CallvanController {
    @Inject
    private CallvanService callvanService;

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/callvan/companies", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getCompanies() {
        return new ResponseEntity<List<Company>>(callvanService.getCompanies(), HttpStatus.OK);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/callvan/companies/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getCompany(@ApiParam(required = true) @PathVariable("id") int id) {
        return new ResponseEntity<Company>(callvanService.getCompany(id), HttpStatus.OK);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/callvan/companies/{id}/call", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity increaseCallCount(@ApiParam(required = true) @PathVariable("id") int id) {
        return new ResponseEntity<Company>(callvanService.increaseCallCount(id), HttpStatus.CREATED);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/callvan/rooms", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getRooms() {
        return new ResponseEntity<List<Room>>(callvanService.getRooms(), HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/callvan/rooms", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createRoom(@ApiParam(value = "(required: departure_place, departure_datetime, arrival_place, maximum_people)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) Room room, BindingResult bindingResult) {
        return new ResponseEntity<Room>(callvanService.createRoom(room), HttpStatus.CREATED);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/callvan/rooms/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getRoom(@ApiParam(required = true) @PathVariable("id") int id) {
        return new ResponseEntity<Room>(callvanService.getRoom(id), HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/callvan/rooms/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateRoom(@ApiParam(value = "(optional: departure_place, departure_datetime, arrival_place, maximum_people)", required = false) @RequestBody @Validated(ValidationGroups.Update.class) Room room, BindingResult bindingResult, @ApiParam(required = true) @PathVariable("id") int id) {
        return new ResponseEntity<Room>(callvanService.updateRoom(room, id), HttpStatus.CREATED);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/callvan/rooms/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteRoom(@ApiParam(required = true) @PathVariable("id") int id) {
        return new ResponseEntity<Map<String, Object>>(callvanService.deleteRoom(id), HttpStatus.OK);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/callvan/rooms/participant", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity ParticipateInRoom(@ApiParam(value = "(required: room_id)", required = true) @RequestBody Participant participant) {
        return new ResponseEntity<Room>(callvanService.participateRoom(participant), HttpStatus.CREATED);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/callvan/rooms/participant", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity unParticipateInRoom(@ApiParam(value = "(required: room_id)", required = true) @RequestBody Participant participant) {
        return new ResponseEntity<Room>(callvanService.unParticipateRoom(participant), HttpStatus.OK);
    }
}
