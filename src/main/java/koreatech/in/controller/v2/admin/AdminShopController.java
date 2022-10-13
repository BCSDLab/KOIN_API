package koreatech.in.controller.v2.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.shop.request.*;
import koreatech.in.service.ShopService;
import koreatech.in.util.StringXssChecker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.SHOP)
@Controller("AdminShopControllerV2")
@RequestMapping("/admin/v2/shops")
public class AdminShopController {
    @Inject
    private ShopService shopService;

    // --------------------------------------- 상점 카테고리 -------------------------------------------

    // TODO: admin 권한만 허용
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createShopCategory(
            @RequestPart("category") @Valid CreateShopCategoryDTO dto, BindingResult bindingResult,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        CreateShopCategoryDTO clear = new CreateShopCategoryDTO();
        return new ResponseEntity<>(shopService.createShopCategoryForAdmin(
                (CreateShopCategoryDTO) StringXssChecker.xssCheck(dto.init(image), clear)), HttpStatus.CREATED);
    }

    // TODO: admin 권한만 허용
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity updateShopCategory(
            @PathVariable Integer id,
            @RequestPart("category") @Valid UpdateShopCategoryDTO dto, BindingResult bindingResult,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        UpdateShopCategoryDTO clear = new UpdateShopCategoryDTO();
        return new ResponseEntity<>(shopService.updateShopCategoryForAdmin(
                (UpdateShopCategoryDTO) StringXssChecker.xssCheck(dto.init(id, image), clear)), HttpStatus.CREATED);
    }

    // TODO: admin 권한만 허용
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteShopCategory(@PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(shopService.deleteShopCategoryForAdmin(id), HttpStatus.OK);
    }

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    // TODO: 슬라이드 방식으로 결정된다면 리팩토링
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getShopCategories() throws Exception {
        return new ResponseEntity<>(shopService.getShopCategoriesForAdmin(), HttpStatus.OK);
    }

    // ------------------------------------------- 상점 ----------------------------------------------

    // TODO: admin 권한만 허용
    // TODO: 사장님이 존재하는지 확인하는 코드 작성 필요
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/owners/match", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity matchShopWithOwner(
            @PathVariable("id") Integer shopId,
            @RequestBody @Valid MatchShopWithOwnerDTO dto, BindingResult bindingResult) throws Exception {

        MatchShopWithOwnerDTO clear = new MatchShopWithOwnerDTO();
        return new ResponseEntity<>(shopService.matchShopWithOwner(
                (MatchShopWithOwnerDTO) StringXssChecker.xssCheck(dto.init(shopId), clear)), HttpStatus.OK);
    }

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    // TODO: 상점 등록 UI 확인 후 리팩토링
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createShop(
            @RequestPart("shop") @Valid CreateShopDTO dto, BindingResult bindingResult,
            @RequestPart("images") List<MultipartFile> images) throws Exception {

        CreateShopDTO clear = new CreateShopDTO();
        return new ResponseEntity<>(shopService.createShopForAdmin(
                (CreateShopDTO) StringXssChecker.xssCheck(dto, clear)), HttpStatus.CREATED);
    }

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getShop(@PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(shopService.getShopForAdmin(id), HttpStatus.OK);
    }

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity updateShop(
            @PathVariable Integer id,
            @RequestPart("shop") @Valid UpdateShopDTO dto, BindingResult bindingResult,
            @RequestPart("images") List<MultipartFile> images) throws Exception {

        UpdateShopDTO clear = new UpdateShopDTO();
        return new ResponseEntity<>(shopService.updateShopForAdmin(
                (UpdateShopDTO) StringXssChecker.xssCheck(dto.init(id, images), clear)), HttpStatus.OK);
    }

    // TODO: admin 권한만 허용
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteShop(@PathVariable Integer id) throws Exception {
        return new ResponseEntity<>(shopService.deleteShopForAdmin(id), HttpStatus.OK);
    }

    // TODO: admin 권한만 허용
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getShops(@RequestParam("page") Integer page, @RequestParam("limit") Integer limit) throws Exception {
        return new ResponseEntity<>(shopService.getShopsForAdmin(page, limit), HttpStatus.OK);
    }

    // ----------------------------------------- 메뉴 카테고리 --------------------------------------------

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createMenuCategory(
            @PathVariable Integer id,
            @RequestBody CreateShopMenuCategoryDTO dto, BindingResult bindingResult) throws Exception {

        CreateShopMenuCategoryDTO clear = new CreateShopMenuCategoryDTO();
        return new ResponseEntity<>(shopService.createMenuCategoryForAdmin(
                (CreateShopMenuCategoryDTO) StringXssChecker.xssCheck(dto.init(id), clear)), HttpStatus.CREATED);
    }

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMenuCategories(@PathVariable Integer id) throws Exception {

        return new ResponseEntity<>(shopService.getMenuCategoriesForAdmin(id), HttpStatus.OK);
    }

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/categories/{categoryId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteMenuCategory(@PathVariable Integer shopId, @PathVariable Integer categoryId) throws Exception {

        return new ResponseEntity<>(shopService.deleteMenuCategoryForAdmin(shopId, categoryId), HttpStatus.OK);
    }

    // ------------------------------------------- 메뉴 ----------------------------------------------------

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createMenu(
            @PathVariable Integer id,
            @RequestPart("menu") @Valid CreateShopMenuDTO dto, BindingResult bindingResult,
            @RequestPart("images") List<MultipartFile> images) throws Exception {

        CreateShopMenuDTO clear = new CreateShopMenuDTO();
        return new ResponseEntity<>(shopService.createMenuForAdmin(
                (CreateShopMenuDTO) StringXssChecker.xssCheck(dto.init(id, images), clear)), HttpStatus.CREATED);
    }

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMenu(@PathVariable Integer shopId, @PathVariable Integer menuId) throws Exception {

        return new ResponseEntity<>(shopService.getMenuForAdmin(shopId, menuId), HttpStatus.OK);
    }

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.POST)
    ResponseEntity updateMenu(
            @PathVariable Integer shopId, @PathVariable Integer menuId,
            @RequestPart("menu") @Valid UpdateShopMenuDTO dto, BindingResult bindingResult,
            @RequestPart("images") List<MultipartFile> images) throws Exception {

        UpdateShopMenuDTO clear = new UpdateShopMenuDTO();
        return new ResponseEntity<>(shopService.updateMenuForAdmin(
                (UpdateShopMenuDTO) StringXssChecker.xssCheck(dto.init(shopId, menuId, images), clear)), HttpStatus.CREATED);
    }

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteMenu(@PathVariable Integer shopId, @PathVariable Integer menuId) throws Exception {

        return new ResponseEntity<>(shopService.deleteMenuForAdmin(shopId, menuId), HttpStatus.OK);
    }

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}/hide", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity hideMenu(@PathVariable Integer shopId, @PathVariable Integer menuId, @RequestParam("flag") Boolean flag) throws Exception {

        return new ResponseEntity<>(shopService.hideMenuForAdmin(shopId, menuId, flag), HttpStatus.OK);
    }

    // TODO: 사장님 권한으로 요청시 자신의 상점이 아니면 block
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMenus(@PathVariable Integer id) throws Exception {

        return new ResponseEntity<>(shopService.getMenusForAdmin(id), HttpStatus.OK);
    }
}
