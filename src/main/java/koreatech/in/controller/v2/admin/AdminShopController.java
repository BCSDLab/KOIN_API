package koreatech.in.controller.v2.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.SuccessCreateResponse;
import koreatech.in.dto.SuccessResponse;
import koreatech.in.dto.UploadImageResponse;
import koreatech.in.dto.UploadImagesResponse;
import koreatech.in.dto.shop.admin.request.*;
import koreatech.in.dto.shop.admin.response.*;
import koreatech.in.repository.ShopMapper;
import koreatech.in.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ShopMapper shopMapper;

    // ======================================= 상점 카테고리 ============================================

    @ParamValid
    @ApiOperation(value = "상점 카테고리 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<SuccessCreateResponse> createShopCategory(@RequestBody @Valid CreateShopCategoryRequest request, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<>(shopService.createShopCategoryForAdmin((CreateShopCategoryRequest) xssPrevent(request)), HttpStatus.CREATED);
    }

    @ParamValid
    @ApiOperation(value = "상점 카테고리 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<SuccessResponse> updateShopCategory(@PathVariable("id") Integer shopCategoryId, @RequestBody @Valid UpdateShopCategoryRequest request, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<>(shopService.updateShopCategoryForAdmin(shopCategoryId, (UpdateShopCategoryRequest) xssPrevent(request)), HttpStatus.OK);
    }

    @ApiOperation(value = "상점 카테고리 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<SuccessResponse> deleteShopCategory(@PathVariable("id") Integer shopCategoryId) throws Exception {

        return new ResponseEntity<>(shopService.deleteShopCategoryForAdmin(shopCategoryId), HttpStatus.OK);
    }

    // TODO: 카테고리 순서 확정 후에 마이그레이션 SQL 변경
    @ApiOperation(value = "모든 상점 카테고리 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllShopCategoriesResponse> getAllShopCategories() throws Exception {

        return new ResponseEntity<>(shopService.getAllShopCategoriesForAdmin(), HttpStatus.OK);
    }

    // ============================================ 상점 ================================================

    // TODO: 로그인 코드와 merge되면 개발 재개
    @ParamValid
    @ApiOperation(value = "상점과 특정 사장님 매칭", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/owners/match", method = RequestMethod.PATCH)
    public @ResponseBody
    ResponseEntity<SuccessResponse> matchShopWithOwner(@PathVariable("id") Integer shopId, @RequestBody @Valid MatchShopWithOwnerRequest request, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<>(shopService.matchShopWithOwner(shopId, (MatchShopWithOwnerRequest) xssPrevent(request)), HttpStatus.OK);
    }

    /*
         TODO: 사장님 권한은 자신의 상점 아니면 403,
               로그인 코드와 merge되면 개발 재개
     */
    @ParamValid
    @ApiOperation(value = "상점 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<SuccessCreateResponse> createShop(@RequestBody @Valid CreateShopRequest request, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<>(shopService.createShopForAdmin((CreateShopRequest) xssPrevent(request)), HttpStatus.CREATED);
    }

    /*
        TODO: 사장님 권한은 자신의 상점 아니면 403,
              로그인 코드와 merge되면 개발 재개
     */
    @ApiOperation(value = "상점 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopResponse> getShop(@PathVariable("id") Integer shopId) throws Exception {

        return new ResponseEntity<>(shopService.getShopForAdmin(shopId), HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ParamValid
    @ApiOperation(value = "상점 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<SuccessResponse> updateShop(@PathVariable("id") Integer shopId, @RequestBody @Valid UpdateShopRequest request, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<>(shopService.updateShopForAdmin(shopId, (UpdateShopRequest) xssPrevent(request)), HttpStatus.OK);
    }

    @ApiOperation(value = "상점 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<SuccessResponse> deleteShop(@PathVariable("id") Integer shopId) throws Exception {

        return new ResponseEntity<>(shopService.deleteShopForAdmin(shopId), HttpStatus.OK);
    }

    @ApiOperation(value = "상점 삭제 해제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/undelete", method = RequestMethod.PATCH)
    public @ResponseBody
    ResponseEntity<SuccessResponse> undeleteShop(@PathVariable("id") Integer shopId) throws Exception {

        return new ResponseEntity<>(shopService.undeleteOfShopForAdmin(shopId), HttpStatus.OK);
    }

    // TODO: 로그인 코드와 merge되면 개발 재개
    @ParamValid
    @ApiOperation(value = "상점 리스트 조회 (페이지네이션)", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopsResponse> getShops(ShopsCondition condition, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<>(shopService.getShopsForAdmin(condition), HttpStatus.OK);
    }

    // ============================================= 메뉴 카테고리 =============================================

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ParamValid
    @ApiOperation(value = "특정 상점의 메뉴 카테고리 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<SuccessCreateResponse> createMenuCategory(@PathVariable("id") Integer shopId, @RequestBody @Valid CreateShopMenuCategoryRequest request, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<>(shopService.createMenuCategoryForAdmin(shopId, (CreateShopMenuCategoryRequest) xssPrevent(request)), HttpStatus.CREATED);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 모든 메뉴 카테고리 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenuCategoriesOfShopResponse> getAllMenuCategoriesOfShop(@PathVariable("id") Integer shopId) throws Exception {

        return new ResponseEntity<>(shopService.getAllMenuCategoriesOfShopForAdmin(shopId), HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 메뉴 카테고리 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/categories/{categoryId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<SuccessResponse> deleteMenuCategory(@PathVariable("shopId") Integer shopId, @PathVariable("categoryId") Integer menuCategoryId) throws Exception {

        return new ResponseEntity<>(shopService.deleteMenuCategoryForAdmin(shopId, menuCategoryId), HttpStatus.OK);
    }

    // =============================================== 메뉴 =================================================

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ParamValid
    @ApiOperation(value = "특정 상점의 메뉴 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<SuccessCreateResponse> createMenu(@PathVariable("id") Integer shopId, @RequestBody @Valid CreateShopMenuRequest request, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<>(shopService.createMenuForAdmin(shopId, (CreateShopMenuRequest) xssPrevent(request)), HttpStatus.CREATED);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 메뉴 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<MenuResponse> getMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId) throws Exception {

        return new ResponseEntity<>(shopService.getMenuForAdmin(shopId, menuId), HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ParamValid
    @ApiOperation(value = "특정 상점의 메뉴 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.PUT)
    ResponseEntity<SuccessResponse> updateMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId, @RequestBody @Valid UpdateShopMenuRequest request, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<>(shopService.updateMenuForAdmin(shopId, menuId, (UpdateShopMenuRequest) xssPrevent(request)), HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 메뉴 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<SuccessResponse> deleteMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId) throws Exception {

        return new ResponseEntity<>(shopService.deleteMenuForAdmin(shopId, menuId), HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 메뉴 숨김 또는 숨김 해제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}/hide", method = RequestMethod.PATCH)
    public @ResponseBody
    ResponseEntity<SuccessResponse> hideMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId, @RequestParam("hidden") Boolean hidden) throws Exception {

        return new ResponseEntity<>(shopService.hideMenuForAdmin(shopId, menuId, hidden), HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 모든 메뉴 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenusOfShopResponse> getAllMenusOfShop(@PathVariable("id") Integer shopId) throws Exception {

        return new ResponseEntity<>(shopService.getAllMenusOfShopForAdmin(shopId), HttpStatus.OK);
    }

    @ApiOperation(value = "상점 카테고리 이미지 업로드", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories/image", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<UploadImageResponse> uploadShopCategoryImage(@RequestPart("image") MultipartFile image) throws Exception {

        return new ResponseEntity<>(shopService.uploadShopCategoryImage(image), HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점 메뉴 이미지 다중 업로드", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/menus/images", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<UploadImagesResponse> uploadShopMenuImages(@RequestPart("images") List<MultipartFile> images) throws Exception {

        return new ResponseEntity<>(shopService.uploadShopMenuImages(images), HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점 이미지 다중 업로드", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<UploadImagesResponse> uploadShopImages(@RequestPart("images") List<MultipartFile> images) throws Exception {

        return new ResponseEntity<>(shopService.uploadShopImages(images), HttpStatus.CREATED);
    }
}
