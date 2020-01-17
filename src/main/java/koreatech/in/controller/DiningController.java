package koreatech.in.controller;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.DiningMenu;
import koreatech.in.service.DiningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class DiningController {
    @Inject
    private DiningService diningService;
    
    @RequestMapping(value = "/dinings", method = RequestMethod.GET)
    public ResponseEntity getDinings(@ApiParam(required = false) @RequestParam(value = "date", required = false) String date) throws Exception {

        return new ResponseEntity<>(diningService.getDinings(date), HttpStatus.OK);
    }
}
