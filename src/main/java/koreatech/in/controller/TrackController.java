package koreatech.in.controller;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.Homepage.Member;
import koreatech.in.domain.Homepage.Track;
import koreatech.in.service.TrackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class TrackController {
    @Resource(name = "trackService")
    private TrackService trackService;

    @RequestMapping(value = "/tracks", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getTracks() throws Exception {
        return new ResponseEntity<List<Track>>(trackService.getTracks(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tracks/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getTrackById(@ApiParam(value = "트랙 uid", required = true) @PathVariable(value = "id") Integer trackId) throws Exception {
        return new ResponseEntity<Map<String, Object>>(trackService.getTrackInfo(trackId), HttpStatus.OK);
    }
}
