package koreatech.in.controller;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.Version.Version;
import koreatech.in.service.VersionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class VersionController {
    @Inject
    private VersionService versionService;

    @RequestMapping(value = "/versions/{type}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getVersion(@ApiParam(required = true) @PathVariable(value = "type") String type) throws Exception {

        return new ResponseEntity<Version>(versionService.getVersion(type), HttpStatus.OK);
    }
}
