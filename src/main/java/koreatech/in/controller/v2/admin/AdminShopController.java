package koreatech.in.controller.v2.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.shop.request.*;
import koreatech.in.dto.shop.response.*;
import koreatech.in.service.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;

import static koreatech.in.util.StringXssChecker.xssPrevent;

/**
 *   기본적으로 grant_shop 권한이 있는 admin 계정만 권한을 부여하지만,
 *   일부 메소드는 사장님 권한도 허용한다.
 *   왜냐하면 사장님은 자신의 상점 및 메뉴 정보에 대해 CRUD할 권한이 있어야 하기 때문이다.
 *   (단, 사장님은 본인의 상점에 해당하는 shopId에 대한 요청만 권한이 부여된다.)
 *
 *   이 controller에서 사장님 권한까지 부여되는 메소드는 다음과 같다.
 *
 *   - getShopCategories (GET /admin/v2/shops/categories)
 *   - createShop (POST /admin/v2/shops)
 *   - getShop (GET /admin/v2/shops/{id})
 *   - updateShop (POST /admin/v2/shops/{id})
 *   - createMenuCategory (POST /admin/v2/shops/{id}/menus/categories)
 *   - getMenuCategories (GET /admin/v2/shops/{id}/menus/categories)
 *   - deleteMenuCategory (DELETE /admin/v2/shops/{shopId}/menus/categories/{categoryId})
 *   - createMenu (POST /admin/v2/shops/{id}/menus)
 *   - getMenu (GET /admin/v2/shops/{shopId}/menus/{menuId})
 *   - updateMenu (POST /admin/v2/shops/{shopId}/menus/{menuId})
 *   - deleteMenu (DELETE /admin/v2/shops/{shopId}/menus/{menuId})
 *   - hideMenu (PUT /admin/v2/shops/{shopId}/menus/{menuId}/hide)
 *   - getMenus (GET /admin/v2/shops/{id}/menus)
 */
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.SHOP)
@Controller("AdminShopControllerV2")
@RequestMapping("/admin/v2/shops")
public class AdminShopController {
    @Inject
    private ShopService shopService;

    // --------------------------------------- 상점 카테고리 -------------------------------------------

    @ParamValid
    @ApiOperation(value = "상점 카테고리 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<ResponseSuccessCreateDTO> createShopCategory(
            @RequestPart("category") @Valid CreateShopCategoryDTO dto, BindingResult bindingResult,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        return new ResponseEntity<>(shopService.createShopCategoryForAdmin(
                (CreateShopCategoryDTO) xssPrevent(dto), image), HttpStatus.CREATED);
    }

    @ParamValid
    @ApiOperation(value = "상점 카테고리 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<ResponseSuccessfulDTO> updateShopCategory(
            @PathVariable("id") Integer shopCategoryId,
            @RequestPart("category") @Valid UpdateShopCategoryDTO dto, BindingResult bindingResult,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        return new ResponseEntity<>(shopService.updateShopCategoryForAdmin(
                shopCategoryId, (UpdateShopCategoryDTO) xssPrevent(dto), image), HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점 카테고리 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<ResponseSuccessfulDTO> deleteShopCategory(@PathVariable("id") Integer shopCategoryId) throws Exception {
        return new ResponseEntity<>(shopService.deleteShopCategoryForAdmin(shopCategoryId), HttpStatus.OK);
    }

    // TODO: 카테고리 순서 확정 후에 마이그레이션 SQL 변경
    @ApiOperation(value = "모든 상점 카테고리 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ResponseAllShopCategoriesDTO> getAllShopCategories() throws Exception {
        return new ResponseEntity<>(shopService.getAllShopCategoriesForAdmin(), HttpStatus.OK);
    }

    // ------------------------------------------- 상점 ----------------------------------------------

    // TODO: 로그인 코드와 merge되면 개발 재개
    @ParamValid
    @ApiOperation(value = "상점과 특정 사장님 매칭", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/owners/match", method = RequestMethod.PATCH)
    public @ResponseBody
    ResponseEntity<ResponseSuccessfulDTO> matchShopWithOwner(
            @PathVariable("id") Integer shopId,
            @RequestBody @Valid MatchShopWithOwnerDTO dto, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<>(shopService.matchShopWithOwner(
                shopId, (MatchShopWithOwnerDTO) xssPrevent(dto)), HttpStatus.OK);
    }

    /*
         TODO: 사장님 권한은 자신의 상점 아니면 403,
               로그인 코드와 merge되면 개발 재개
     */
    @ParamValid
    @ApiOperation(value = "상점 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<ResponseSuccessCreateDTO> createShop(
            @RequestPart("shop") @Valid CreateShopDTO dto, BindingResult bindingResult,
            @RequestPart("images") List<MultipartFile> images) throws Exception {

        return new ResponseEntity<>(shopService.createShopForAdmin(
                (CreateShopDTO) xssPrevent(dto), images), HttpStatus.CREATED);
    }

    /*
        TODO: 사장님 권한은 자신의 상점 아니면 403,
              로그인 코드와 merge되면 개발 재개
     */
    @ApiOperation(value = "상점 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ResponseShopDTO> getShop(@PathVariable("id") Integer shopId) throws Exception {
        return new ResponseEntity<>(shopService.getShopForAdmin(shopId), HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ParamValid
    @ApiOperation(value = "상점 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<ResponseSuccessfulDTO> updateShop(
            @PathVariable("id") Integer shopId,
            @RequestPart("shop") @Valid UpdateShopDTO dto, BindingResult bindingResult,
            @RequestPart("images") List<MultipartFile> images) throws Exception {

        return new ResponseEntity<>(shopService.updateShopForAdmin(
                shopId, (UpdateShopDTO) xssPrevent(dto), images), HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<ResponseSuccessfulDTO> deleteShop(@PathVariable("id") Integer shopId) throws Exception {
        return new ResponseEntity<>(shopService.deleteShopForAdmin(shopId), HttpStatus.OK);
    }

    @ApiOperation(value = "상점 삭제 해제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/undelete", method = RequestMethod.PATCH)
    public @ResponseBody
    ResponseEntity<ResponseSuccessfulDTO> undeleteShop(@PathVariable("id") Integer shopId) throws Exception {
        return new ResponseEntity<>(shopService.undeleteOfShopForAdmin(shopId), HttpStatus.OK);
    }

    // TODO: 로그인 코드와 merge되면 개발 재개
    @ParamValid
    @ApiOperation(value = "상점 리스트 조회 (페이지네이션)", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ResponseShopsDTO> getShops(@Valid ShopsConditionDTO dto, BindingResult bindingResult) throws Exception {
        return new ResponseEntity<>(shopService.getShopsForAdmin(dto), HttpStatus.OK);
    }

    // ----------------------------------------- 메뉴 카테고리 --------------------------------------------

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ParamValid
    @ApiOperation(value = "특정 상점의 메뉴 카테고리 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<ResponseSuccessCreateDTO> createMenuCategory(
            @PathVariable("id") Integer shopId,
            @RequestBody CreateShopMenuCategoryDTO dto, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<>(shopService.createMenuCategoryForAdmin(
                shopId, (CreateShopMenuCategoryDTO) xssPrevent(dto)), HttpStatus.CREATED);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 모든 메뉴 카테고리 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ResponseShopMenuCategoriesDTO> getAllMenuCategoriesOfShop(@PathVariable("id") Integer shopId) throws Exception {

        return new ResponseEntity<>(shopService.getAllMenuCategoriesOfShopForAdmin(shopId), HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 메뉴 카테고리 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/categories/{categoryId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<ResponseSuccessfulDTO> deleteMenuCategory(
            @PathVariable("shopId") Integer shopId, @PathVariable("categoryId") Integer menuCategoryId) throws Exception {

        return new ResponseEntity<>(shopService.deleteMenuCategoryForAdmin(shopId, menuCategoryId), HttpStatus.OK);
    }

    // ------------------------------------------- 메뉴 ----------------------------------------------------

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ParamValid
    @ApiOperation(value = "특정 상점의 메뉴 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<ResponseSuccessCreateDTO> createMenu(
            @PathVariable("id") Integer shopId,
            @RequestPart("menu") @Valid CreateShopMenuDTO dto, BindingResult bindingResult,
            @RequestPart("images") List<MultipartFile> images) throws Exception {

        return new ResponseEntity<>(shopService.createMenuForAdmin(
                shopId, (CreateShopMenuDTO) xssPrevent(dto), images), HttpStatus.CREATED);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 메뉴 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ResponseShopMenuDTO> getMenu(
            @PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId) throws Exception {

        return new ResponseEntity<>(shopService.getMenuForAdmin(shopId, menuId), HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ParamValid
    @ApiOperation(value = "특정 상점의 메뉴 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.POST)
    ResponseEntity<ResponseSuccessfulDTO> updateMenu(
            @PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId,
            @RequestPart("menu") @Valid UpdateShopMenuDTO dto, BindingResult bindingResult,
            @RequestPart("images") List<MultipartFile> images) throws Exception {

        return new ResponseEntity<>(shopService.updateMenuForAdmin(
                shopId, menuId, (UpdateShopMenuDTO) xssPrevent(dto), images), HttpStatus.CREATED);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 메뉴 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<ResponseSuccessfulDTO> deleteMenu(
            @PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId) throws Exception {

        return new ResponseEntity<>(shopService.deleteMenuForAdmin(shopId, menuId), HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 메뉴 숨김 또는 숨김 해제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}/hide", method = RequestMethod.PATCH)
    public @ResponseBody
    ResponseEntity<ResponseSuccessfulDTO> hideMenu(
            @PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId,
            @RequestParam("hide") Boolean hide) throws Exception {

        return new ResponseEntity<>(shopService.hideMenuForAdmin(shopId, menuId, hide), HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 모든 메뉴 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ResponseShopMenusDTO> getAllMenusOfShop(@PathVariable("id") Integer shopId) throws Exception {

        return new ResponseEntity<>(shopService.getAllMenusOfShopForAdmin(shopId), HttpStatus.OK);
    }
}
