package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import koreatech.in.annotation.ApiOff;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.Faq.Faq;
import koreatech.in.service.FaqService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApiOff @ApiIgnore @Deprecated
@Api(tags = "(Normal) Faq", description = "질문")
@Controller
public class FaqController {
    @Inject
    private FaqService faqService;

    @ApiOff
    @RequestMapping(value = "/faqs", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getFaqList(@ModelAttribute("criteria") Criteria criteria) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        return new ResponseEntity<Map<String, Object>>(faqService.getFaqList(criteria), HttpStatus.OK);
    }

    @ApiOff
    @RequestMapping(value = "/faqs/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getFaq(@ApiParam(required = true) @PathVariable int id) throws Exception {
        return new ResponseEntity<Faq>(faqService.getFaq(id), HttpStatus.OK);
    }
}
