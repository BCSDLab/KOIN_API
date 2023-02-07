package koreatech.in.controller;

import io.swagger.annotations.ApiOperation;
import koreatech.in.dto.normal.shop.response.AllMenusOfShopResponse;
import koreatech.in.dto.normal.shop.response.AllShopCategoriesResponse;
import koreatech.in.dto.normal.shop.response.AllShopsResponse;
import koreatech.in.dto.normal.shop.response.ShopResponse;
import io.swagger.annotations.Api;
import koreatech.in.service.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@Api(tags = "(Normal) Shop", description = "상점")
@Controller
@RequestMapping("/shops")
public class ShopController {
    @Inject
    private ShopService shopService;

    @ApiOperation(value = "모든 상점 카테고리 조회")
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllShopCategoriesResponse> getAllShopCategories() {
        AllShopCategoriesResponse response = shopService.getAllShopCategories();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopResponse> getShop(@PathVariable("id") Integer shopId) {
        ShopResponse response = shopService.getShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllShopsResponse> getAllShops() {
        AllShopsResponse response = shopService.getAllShops();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/menus", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenusOfShopResponse> getAllMenusOfShop(@PathVariable("id") Integer shopId) {
        AllMenusOfShopResponse response = shopService.getAllMenusOfShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
