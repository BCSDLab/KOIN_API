package koreatech.in.controller.v2.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.controller.v2.dto.shop.request.*;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.Shop.Shop;
import koreatech.in.service.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.SHOP)
@Controller("AdminShopControllerV2")
@RequestMapping(value="/admin/v2/shops")
public class AdminShopController {
    @Inject
    private ShopService shopService;

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getShops(@ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<Map<String, Object>>(shopService.getShopsForAdmin(criteria), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createShop(@ApiParam(value = "(required: name, category), (optional: phone, open_time, close_time, weekend_open_time, weekend_close_time, image_urls, address, description, delivery, delivery_price, pay_card, pay_bank, is_event, remarks, is_deleted)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) CreateShopDTO shop, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<Shop>(shopService.createShopForAdmin(shop.toEntity()), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getShop(@ApiParam(required = true) @PathVariable(value = "id") String id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(shopService.getShopForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateShop(@ApiParam(value = "(optional: name, category, phone, open_time, close_time, image_urls, address, description, delivery, delivery_price, pay_card, pay_bank, is_event, remarks, is_deleted)", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) CreateShopDTO request, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Shop>(shopService.updateShopForAdmin(request.toEntity(), id), HttpStatus.CREATED);
    }

    // ------------------------------- 여기부터 코인 리뉴얼 API ----------------------------------

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/category", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createMenuCategory(@PathVariable Integer shopId, @RequestBody CreateShopMenuCategoryDTO dto) throws Exception {
        return new ResponseEntity<>(shopService.createMenuCategoryForOwner(dto.init(shopId)), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMenuCategories(@PathVariable Integer shopId) throws Exception {
        return new ResponseEntity<>(shopService.getMenuCategoriesForOwner(shopId), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/categories/{categoryId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteMenuCategory(@PathVariable Integer shopId, @PathVariable Integer categoryId) throws Exception {
        return new ResponseEntity<>(shopService.deleteMenuCategoryForOwner(shopId, categoryId), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menu", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createMenu(
            @PathVariable Integer shopId,
            @RequestPart("menu") @Valid CreateShopMenuDTO dto, BindingResult bindingResult,
            @RequestPart("images") List<MultipartFile> images) throws Exception {
        return new ResponseEntity<>(shopService.createMenuForOwner(dto.init(shopId, images)), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMenu(@PathVariable Integer shopId, @PathVariable Integer menuId) throws Exception {
        return new ResponseEntity<>(shopService.getMenuForOwner(shopId, menuId), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.POST)
    ResponseEntity updateMenu(
            @PathVariable Integer shopId, @PathVariable Integer menuId,
            @RequestPart("menu") @Valid UpdateShopMenuDTO dto, BindingResult bindingResult,
            @RequestPart("images") List<MultipartFile> images) throws Exception {

        return new ResponseEntity<>(shopService.updateMenuForOwner(dto.init(shopId, menuId, images)), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteMenu(@PathVariable Integer shopId, @PathVariable Integer menuId) throws Exception {
        return new ResponseEntity<>(shopService.deleteMenuForOwner(shopId, menuId), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}/hide", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity hideMenu(
            @PathVariable Integer shopId, @PathVariable Integer menuId,
            @RequestParam("flag") Boolean flag) throws Exception {
        return new ResponseEntity<>(shopService.hideMenuForOwner(shopId, menuId, flag), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMenus(@PathVariable Integer shopId) throws Exception {
        return new ResponseEntity<>(shopService.getMenusForOwner(shopId), HttpStatus.OK);
    }
}
