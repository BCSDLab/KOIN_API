package koreatech.in.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.UploadImageResponse;
import koreatech.in.dto.UploadImagesResponse;
import koreatech.in.dto.shop.admin.request.*;
import koreatech.in.dto.shop.admin.response.*;
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

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.SHOP)
@Controller
@RequestMapping("/admin/shops")
public class AdminShopController {
    @Inject
    private ShopService shopService;

    // ======================================= 상점 카테고리 ============================================

    @ParamValid
    @ApiOperation(value = "상점 카테고리 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Void> createShopCategory(@RequestBody @Valid CreateShopCategoryRequest request, BindingResult bindingResult) throws Exception {
        shopService.createShopCategoryForAdmin((CreateShopCategoryRequest) xssPrevent(request));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점 카테고리 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopCategoryResponse> getShopCategory(@PathVariable("id") Integer shopCategoryId) throws Exception {
        ShopCategoryResponse response = shopService.getShopCategoryForAdmin(shopCategoryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO: 카테고리 순서 확정 후에 마이그레이션 SQL 변경
    @ApiOperation(value = "상점 카테고리 리스트 조회 (페이지네이션)", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopCategoriesResponse> getShopCategories(ShopCategoriesCondition condition) throws Exception {
        ShopCategoriesResponse response = shopService.getShopCategoriesForAdmin(condition);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "상점 카테고리 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<Void> updateShopCategory(@PathVariable("id") Integer shopCategoryId, @RequestBody @Valid UpdateShopCategoryRequest request, BindingResult bindingResult) throws Exception {
        shopService.updateShopCategoryForAdmin(shopCategoryId, (UpdateShopCategoryRequest) xssPrevent(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "상점 카테고리 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<Void> deleteShopCategory(@PathVariable("id") Integer shopCategoryId) throws Exception {
        shopService.deleteShopCategoryForAdmin(shopCategoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ============================================ 상점 ================================================

    // TODO: 로그인 코드와 merge되면 개발 재개
    @ParamValid
    @ApiOperation(value = "상점과 특정 사장님 매칭", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/owners/match", method = RequestMethod.PATCH)
    public @ResponseBody
    ResponseEntity<Void> matchShopWithOwner(@PathVariable("id") Integer shopId, @RequestBody @Valid MatchShopWithOwnerRequest request, BindingResult bindingResult) throws Exception {
        shopService.matchShopWithOwner(shopId, (MatchShopWithOwnerRequest) xssPrevent(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
         TODO: 사장님 권한은 자신의 상점 아니면 403,
               로그인 코드와 merge되면 개발 재개
     */
    @ParamValid
    @ApiOperation(value = "상점 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Void> createShop(@RequestBody @Valid CreateShopRequest request, BindingResult bindingResult) throws Exception {
        shopService.createShopForAdmin((CreateShopRequest) xssPrevent(request));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /*
        TODO: 사장님 권한은 자신의 상점 아니면 403,
              로그인 코드와 merge되면 개발 재개
     */
    @ApiOperation(value = "상점 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopResponse> getShop(@PathVariable("id") Integer shopId) throws Exception {
        ShopResponse response = shopService.getShopForAdmin(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO: 로그인 코드와 merge되면 개발 재개
    @ParamValid
    @ApiOperation(value = "상점 리스트 조회 (페이지네이션)", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopsResponse> getShops(ShopsCondition condition) throws Exception {
        ShopsResponse response = shopService.getShopsForAdmin(condition);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ParamValid
    @ApiOperation(value = "상점 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<Void> updateShop(@PathVariable("id") Integer shopId, @RequestBody @Valid UpdateShopRequest request, BindingResult bindingResult) throws Exception {
        shopService.updateShopForAdmin(shopId, (UpdateShopRequest) xssPrevent(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "상점 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<Void> deleteShop(@PathVariable("id") Integer shopId) throws Exception {
        shopService.deleteShopForAdmin(shopId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "상점 삭제 해제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/undelete", method = RequestMethod.PATCH)
    public @ResponseBody
    ResponseEntity<Void> undeleteShop(@PathVariable("id") Integer shopId) throws Exception {
        shopService.undeleteOfShopForAdmin(shopId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ============================================= 메뉴 카테고리 =============================================

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ParamValid
    @ApiOperation(value = "특정 상점의 메뉴 카테고리 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Void> createMenuCategory(@PathVariable("id") Integer shopId, @RequestBody @Valid CreateShopMenuCategoryRequest request, BindingResult bindingResult) throws Exception {
        shopService.createMenuCategoryForAdmin(shopId, (CreateShopMenuCategoryRequest) xssPrevent(request));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 모든 메뉴 카테고리 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenuCategoriesOfShopResponse> getAllMenuCategoriesOfShop(@PathVariable("id") Integer shopId) throws Exception {
        AllMenuCategoriesOfShopResponse response = shopService.getAllMenuCategoriesOfShopForAdmin(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 메뉴 카테고리 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/categories/{categoryId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<Void> deleteMenuCategory(@PathVariable("shopId") Integer shopId, @PathVariable("categoryId") Integer menuCategoryId) throws Exception {
        shopService.deleteMenuCategoryForAdmin(shopId, menuCategoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // =============================================== 메뉴 =================================================

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ParamValid
    @ApiOperation(value = "특정 상점의 메뉴 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Void> createMenu(@PathVariable("id") Integer shopId, @RequestBody @Valid CreateShopMenuRequest request, BindingResult bindingResult) throws Exception {
        shopService.createMenuForAdmin(shopId, (CreateShopMenuRequest) xssPrevent(request));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 메뉴 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<MenuResponse> getMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId) throws Exception {
        MenuResponse response = shopService.getMenuForAdmin(shopId, menuId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 모든 메뉴 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenusOfShopResponse> getAllMenusOfShop(@PathVariable("id") Integer shopId) throws Exception {
        AllMenusOfShopResponse response = shopService.getAllMenusOfShopForAdmin(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ParamValid
    @ApiOperation(value = "특정 상점의 메뉴 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.PUT)
    ResponseEntity<Void> updateMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId, @RequestBody @Valid UpdateShopMenuRequest request, BindingResult bindingResult) throws Exception {
        shopService.updateMenuForAdmin(shopId, menuId, (UpdateShopMenuRequest) xssPrevent(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 메뉴 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<Void> deleteMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId) throws Exception {
        shopService.deleteMenuForAdmin(shopId, menuId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO: 사장님 권한은 자신의 상점 아니면 403
    @ApiOperation(value = "특정 상점의 메뉴 숨김 또는 숨김 해제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}/hide", method = RequestMethod.PATCH)
    public @ResponseBody
    ResponseEntity<Void> hideMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId, @RequestParam("hidden") Boolean hidden) throws Exception {
        shopService.hideMenuForAdmin(shopId, menuId, hidden);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "상점 카테고리 이미지 업로드", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/categories/image", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<UploadImageResponse> uploadShopCategoryImage(@RequestPart("image") MultipartFile image) throws Exception {
        UploadImageResponse response = shopService.uploadShopCategoryImage(image);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점 메뉴 이미지 다중 업로드", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/menus/images", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<UploadImagesResponse> uploadShopMenuImages(@RequestPart("images") List<MultipartFile> images) throws Exception {
        UploadImagesResponse response = shopService.uploadShopMenuImages(images);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점 이미지 다중 업로드", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<UploadImagesResponse> uploadShopImages(@RequestPart("images") List<MultipartFile> images) throws Exception {
        UploadImagesResponse response = shopService.uploadShopImages(images);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
