package koreatech.in.controller;

import koreatech.in.dto.shop.normal.response.AllShopCategoriesResponse;
import koreatech.in.dto.shop.normal.response.AllShopsResponse;
import koreatech.in.dto.shop.normal.response.ShopResponse;
import koreatech.in.service.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@Controller
public class ShopController {
    @Inject
    private ShopService shopService;

    @RequestMapping(value = "/shops/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopResponse> getShop(@PathVariable("id") Integer shopId) throws Exception {
        ShopResponse response = shopService.getShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/shops", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllShopsResponse> getAllShops() throws Exception {
        AllShopsResponse response = shopService.getAllShops();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
         TODO: 카테고리 순서 확정 후 마이그레이션 SQL 변경,
               페이지 방식으로 결정된다면 리팩토링
     */
    @RequestMapping(value = "/shops/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllShopCategoriesResponse> getAllShopCategories() throws Exception {
        AllShopCategoriesResponse response = shopService.getAllShopCategories();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
