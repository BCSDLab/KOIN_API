package koreatech.in.controller.v2.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.controller.v2.dto.shop.request.CreateShopDTO;
import koreatech.in.controller.v2.dto.shop.request.CreateShopMenuDTO;
import koreatech.in.controller.v2.dto.shop.request.UpdateShopMenuCategoryDTO;
import koreatech.in.controller.v2.dto.shop.request.UpdateShopMenuDTO;
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

    // ------------------------------- 여기부터 코인 리뉴얼 API -----------------------------------
    /*@ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/menus/category", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createMenuCategory(@ApiParam(required = true) @RequestBody @Valid CreateShopMenuCategoryDTO dto, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<>(shopService.createMenuCategoryForAdmin(dto.getName()), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/menus/categorys", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getAllMenuCategory() throws Exception {
        return new ResponseEntity<>(shopService.getAllMenuCategoryForAdmin(), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/menus/category/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMenuCategory(@ApiParam(required = true) @PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(shopService.getMenuCategoryForAdmin(id), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/menus/category/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateMenuCategory(@ApiParam(required = true) @PathVariable Integer id, @ApiParam(required = true) @RequestBody @Valid CreateShopMenuCategoryDTO dto, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<>(shopService.updateMenuCategoryForAdmin(id, dto.getName()), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/menus/category/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteMenuCategory(@ApiParam(required = true) @PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(shopService.deleteMenuCategoryForAdmin(id), HttpStatus.OK);
    }*/

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMenuCategoriesOfShop(@PathVariable Integer shopId) throws Exception {
        return new ResponseEntity<>(shopService.getMenuCategoriesOfShopForOwner(shopId), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/categories", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateMenuCategoriesOfShop(@PathVariable Integer shopId, @RequestBody UpdateShopMenuCategoryDTO dto) throws Exception {
        return new ResponseEntity<>(shopService.updateMenuCategoriesOfShopForOwner(shopId, dto), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/menu", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createMenu(
            @RequestPart("menu") @Valid CreateShopMenuDTO dto, BindingResult bindingResult,
            @RequestPart("images") List<MultipartFile> images) throws Exception {
        return new ResponseEntity<>(shopService.createMenuForOwner(dto.init(images)), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/menu/{menuId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMenu(@PathVariable Integer menuId) throws Exception {
        return new ResponseEntity<>(shopService.getShopMenu(menuId), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/menu/{menuId}", method = RequestMethod.PUT)
    ResponseEntity updateMenu(
            @PathVariable Integer menuId,
            @RequestPart("menu") @Valid UpdateShopMenuDTO dto, BindingResult bindingResult,
            @RequestPart("images") List<MultipartFile> images) throws Exception {
        dto.init(menuId, images);
        return new ResponseEntity<>(shopService.updateMenuForOwner(dto), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/menu/{menuId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteMenu(@PathVariable Integer menuId) throws Exception {
        return new ResponseEntity<>(shopService.deleteMenuForOwner(menuId), HttpStatus.OK);
    }


    /**
     *  shop_menus의 price_type(json) 컬럼 데이터들을 parsing해서 shop_menu_details에 마이그레이션하는 API입니다.
     *  코인 리뉴얼시 프로덕션에서 호출이 성공적으로 이루어진 후에는, 더이상 호출하면 안됩니다.
     *  admin 계정으로 호출 예정입니다.
     */
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/menus/migrate", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity migratePriceType() throws Exception {

        return new ResponseEntity<Map<String, Object>>(shopService.migratePriceType(), HttpStatus.OK);
    }
}
