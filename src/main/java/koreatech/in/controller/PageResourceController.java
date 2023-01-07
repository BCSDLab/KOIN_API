package koreatech.in.controller;

import io.swagger.annotations.Api;
import koreatech.in.annotation.ApiOff;
import koreatech.in.domain.PageResource.PageResource;
import koreatech.in.service.PageResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;

@ApiIgnore
@Api(tags = "(Normal) PageResource", description = "페이지 리소스")
@Controller
public class PageResourceController {

    @Inject
    private PageResourceService pageResourceService;

    @ApiOff
    @RequestMapping(value = "/cardNews", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getCardNews() throws Exception {
        return new ResponseEntity<PageResource>(pageResourceService.getCardNews(), HttpStatus.OK);
    }
}
