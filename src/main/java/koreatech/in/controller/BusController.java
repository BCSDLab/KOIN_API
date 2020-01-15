package koreatech.in.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import koreatech.in.service.BusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.Map;

@Controller
public class BusController {
    private static final Logger logger = LoggerFactory.getLogger(BusController.class);

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Inject
    BusService busService;

    @RequestMapping(value = "/buses", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getBus(@ApiParam(value = "koreatech, station, terminal", required = true) @RequestParam(value = "depart") String depart, @ApiParam(value = "koreatech, station, terminal", required = true) @RequestParam(value = "arrival") String arrival) throws Exception{
        return new ResponseEntity<Map<String,Object>>(busService.getBus(depart, arrival), HttpStatus.OK);
    }
}
