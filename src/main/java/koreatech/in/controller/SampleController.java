package koreatech.in.controller;

import koreatech.in.annotation.ApiOff;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {

    @ApiOff
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public @ResponseBody String test() {
        return null;
    }
}
