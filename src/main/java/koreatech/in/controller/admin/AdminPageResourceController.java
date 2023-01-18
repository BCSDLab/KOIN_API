package koreatech.in.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.domain.PageResource.PageResource;
import koreatech.in.service.PageResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;

@ApiOff @ApiIgnore @Deprecated
@Api(tags = "(Admin) PageResource", description = "페이지 리소스")
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.COMMUNITY)
@Controller
public class AdminPageResourceController {

    @Inject
    private PageResourceService pageResourceService;

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/cardNews", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateCardNews(@ApiParam(value = "(optional: card_news_image(이미지 주소), card_link_url(링크 주소))", required = true) @RequestBody PageResource pageResource) throws Exception {
        return new ResponseEntity<PageResource>(pageResourceService.updateCardNewsForAdmin(pageResource), HttpStatus.CREATED);
    }
}
