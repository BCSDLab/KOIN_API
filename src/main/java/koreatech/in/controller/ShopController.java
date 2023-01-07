package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import koreatech.in.service.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

@Api(tags = "(Normal) Shop", description = "상점")
@Controller
public class ShopController {
    @Inject
    private ShopService shopService;

    @RequestMapping(value = "/shops", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getShops() throws Exception {
        Map<String, Object> map = shopService.getShops();

        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

    @ApiImplicitParams(
            @ApiImplicitParam(name = "id", required = true, paramType = "path", dataType = "int")
    )
    @RequestMapping(value = "/shops/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getShop(@ApiParam(required = true) @PathVariable(value = "id") String id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(shopService.getShop(id), HttpStatus.OK);
    }
}
