package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import koreatech.in.domain.Dining.DiningMenuDTO;
import koreatech.in.service.DiningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Api(tags = "(Normal) Dining", description = "식단")
@Controller
public class DiningController {
    @Autowired
    private DiningService diningService;

    @RequestMapping(value = "/dinings", method = RequestMethod.GET)
    public ResponseEntity<List<DiningMenuDTO>> getDinings(@ApiParam(required = false)
                                                          @RequestParam(value = "date", required = false)
                                                          String date) throws Exception {

        return new ResponseEntity<>(diningService.getDinings(date), HttpStatus.OK);
    }
}
