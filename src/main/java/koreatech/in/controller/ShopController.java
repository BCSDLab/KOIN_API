package koreatech.in.controller;

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
    ResponseEntity getShop(@PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(shopService.getShop(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/shops", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getShops() throws Exception {
        return new ResponseEntity<>(shopService.getShops(), HttpStatus.OK);
    }

    // TODO: 슬라이드 방식으로 결정된다면 리팩토링
    @RequestMapping(value = "/shops/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getShopCategories() throws Exception {
        return new ResponseEntity<>(shopService.getShopCategories(), HttpStatus.OK);
    }
}
