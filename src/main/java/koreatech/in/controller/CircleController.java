package koreatech.in.controller;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.BeanSerializer;
import koreatech.in.domain.Circle.Circle;
import koreatech.in.domain.Circle.CircleResponseType;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.service.CircleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

@Controller
public class CircleController {
    @Inject
    private CircleService circleService;

    @RequestMapping(value = "/circles", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getCircleList(@ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<>(BeanSerializer.getSerializedResult(Circle.class, new BeanSerializer(CircleResponseType.getArrayList()), circleService.getCircles(criteria), Map.class), HttpStatus.OK);
    }

    @RequestMapping(value = "/circles/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getCircle(@ApiParam(required = false) @RequestParam(value = "page", required = false, defaultValue = "1") int page, @ApiParam(required = false) @RequestParam(value = "limit", required = false, defaultValue = "10") int limit, @ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<>(circleService.getCircle(page, limit, id), HttpStatus.OK);
    }
}
