package koreatech.in.controller;

import koreatech.in.domain.PageResource.PageResource;
import koreatech.in.service.PageResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

@Controller
public class PageResourceController {

    @Inject
    private PageResourceService pageResourceService;

    @RequestMapping(value = "/cardNews", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getCardNews() throws Exception {
        return new ResponseEntity<PageResource>(pageResourceService.getCardNews(), HttpStatus.OK);
    }
}
