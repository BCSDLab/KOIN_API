package koreatech.in.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Homepage.TechStack;
import koreatech.in.domain.Homepage.Track;
import koreatech.in.service.TrackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Api(tags = "(Admin) Track", description = "BCSDLab 트랙")
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.BCSDLAB)
@Controller
public class AdminTrackController {
    @Inject
    private TrackService trackService;

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/tracks", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getTracks() throws Exception {
        return new ResponseEntity<List<Track>>(trackService.getTracksForAdmin(), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/tracks/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getTrack(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Map<String, Object>>(trackService.getTrackInfoForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/tracks", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createTrack(@ApiParam(value = "(required:name, headcount)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Track track, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<Track>(trackService.createTrackForAdmin(track), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/tracks/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateTrack(@ApiParam(value = "", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Track track, BindingResult bindingResult, @ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Track>(trackService.updateTrackForAdmin(track, id), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/tracks/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteTrack(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Map<String, Object>>(trackService.deleteTrackForAdmin(id), HttpStatus.OK);
    }

    // TECH STACK APIs
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/techStacks", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createTechStack(@ApiParam(value = "(required:name)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) TechStack techStack, BindingResult bindingResult,
                                   @ApiParam(value = "TrackName (ex: Android, FrontEnd, Game, UI/UX, ...", required = true) @RequestParam(value = "trackName") String trackName) throws Exception {
        return new ResponseEntity<TechStack>(trackService.createTechStackForAdmin(techStack, trackName), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/techStacks/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateTechStack(@ApiParam(value = "", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) TechStack techStack, BindingResult bindingResult,
                                   @ApiParam(required = false) @RequestParam(value = "trackName", required = false) String trackName, @ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<TechStack>(trackService.updateTechStackForAdmin(techStack, trackName, id), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/techStacks/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteTechStack(@ApiParam(required = true) @PathVariable("id") int id) throws Exception {
        return new ResponseEntity<Map<String, Object>>(trackService.deleteTechStackForAdmin(id), HttpStatus.OK);
    }
}