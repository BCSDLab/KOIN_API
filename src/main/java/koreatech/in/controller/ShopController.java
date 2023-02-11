package koreatech.in.controller;

import io.swagger.annotations.*;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.normal.shop.response.*;
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

    @ApiOperation(value = "특정 상점 조회")
    @ApiResponses({
            @ApiResponse(code = 404, message = "- 상점이 조회되지 않을 때 (code: 104000)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopResponse> getShop(@ApiParam(required = true) @PathVariable("id") Integer shopId) {
        ShopResponse response = shopService.getShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "모든 상점 조회")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllShopsResponse> getAllShops() {
        AllShopsResponse response = shopService.getAllShops();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 상점의 모든 메뉴 조회")
    @ApiResponses({
            @ApiResponse(code = 404, message = "- 상점이 조회되지 않을 때 (code: 104000)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenusOfShopResponse> getAllMenusOfShop(@ApiParam(required = true) @PathVariable("id") Integer shopId) {
        AllMenusOfShopResponse response = shopService.getAllMenusOfShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 상점의 특정 메뉴 조회")
    @ApiResponses({
            @ApiResponse(code = 404, message = "- 상점이 조회되지 않을 때 (code: 104000) \n" +
                                               "- 메뉴가 조회되지 않을 때 (code: 104007) \n" +
                                               "  - 만약 menuId에 대한 메뉴가, shopId에 대한 상점에 소속되어 있지 않은 경우도 포함", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<MenuResponse> getMenu(
            @ApiParam(required = true) @PathVariable("shopId") Integer shopId,
            @ApiParam(required = true) @PathVariable("menuId") Integer menuId) {
        MenuResponse response = shopService.getMenu(shopId, menuId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 상점의 모든 메뉴 카테고리 조회")
    @ApiResponses({
            @ApiResponse(code = 404, message = "- 상점이 조회되지 않을 때 (code: 104000)")
    })
    @RequestMapping(value = "/{id}/menus/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenuCategoriesOfShopResponse> getAllMenuCategoriesOfShop(@ApiParam(required = true) @PathVariable("id") Integer shopId) {
        AllMenuCategoriesOfShopResponse response = shopService.getAllMenuCategoriesOfShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
