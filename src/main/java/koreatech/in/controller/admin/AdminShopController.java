package koreatech.in.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.Shop.Menu;
import koreatech.in.domain.Shop.Shop;
import koreatech.in.service.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.SHOP)
@Controller
public class AdminShopController {
    @Inject
    private ShopService shopService;

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/shops", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getShops(@ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<Map<String, Object>>(shopService.getShopsForAdmin(criteria), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/shops", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createShop(@ApiParam(value = "(required: name, category), (optional: phone, open_time, close_time, image_urls, address, description, delivery, delivery_price, pay_card, pay_bank, is_event, remarks, is_deleted)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Shop shop, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<Shop>(shopService.createShopForAdmin(shop), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/shops/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getShop(@ApiParam(required = true) @PathVariable(value = "id") String id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(shopService.getShopForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/shops/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateShop(@ApiParam(value = "(optional: name, category, phone, open_time, close_time, image_urls, address, description, delivery, delivery_price, pay_card, pay_bank, is_event, remarks, is_deleted)", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Shop shop, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Shop>(shopService.updateShopForAdmin(shop, id), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/shops/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteShop(@ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(shopService.deleteShopForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/shops/{shopId}/menus", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createMenu(@ApiParam(value = "(required: name, price_type), (optional: is_deleted)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Menu menu, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "shopId") int shop_id) throws Exception {

        return new ResponseEntity<Menu>(shopService.createMenuForAdmin(menu, shop_id), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/shops/{shopId}/menus/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMenu(@ApiParam(required = true) @PathVariable(value = "shopId") int shop_id, @ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(shopService.getMenuForAdmin(shop_id, id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/shops/{shopId}/menus/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateMenu(@ApiParam(value = "(optional: name, price_type, is_deleted)", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Menu menu, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "shopId") int shopId, @ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Menu>(shopService.updateMenuForAdmin(menu, shopId, id), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/shops/{shopId}/menus/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteMenu(@ApiParam(required = true) @PathVariable(value = "shopId") int shopId, @ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(shopService.deleteMenuForAdmin(shopId, id), HttpStatus.OK);
    }
}
