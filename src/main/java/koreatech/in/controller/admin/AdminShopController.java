package koreatech.in.controller.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.admin.shop.request.*;
import koreatech.in.dto.admin.shop.response.*;
import koreatech.in.service.admin.AdminShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

@Api(tags = "(Admin) Shop", description = "상점")
@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.SHOP)
@Controller
@RequestMapping("/admin/shops")
public class AdminShopController {
    @Inject
    private AdminShopService shopService;

    // ======================================= 상점 카테고리 ============================================

    @ApiOperation(value = "상점 카테고리 생성", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "잘못된 접근일 때 (code: 100001)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "중복되는 이름의 카테고리가 이미 존재할 때 (code: 104005)", response = ExceptionResponse.class),
            @ApiResponse(code = 412, message = "요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = ExceptionResponse.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @ParamValid
    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createShopCategory(@ApiParam(name = "상점 카테고리 정보 JSON", required = true) @RequestBody @Valid CreateShopCategoryRequest request, BindingResult bindingResult) {
        shopService.createShopCategory(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점 카테고리 조회", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "잘못된 접근일 때 (code: 100001)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "존재하지 않을 때 (code: 104004)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopCategoryResponse> getShopCategory(@ApiParam(name = "상점 카테고리 고유 id", required = true) @PathVariable("id") Integer shopCategoryId) {
        ShopCategoryResponse response = shopService.getShopCategory(shopCategoryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "상점 카테고리 리스트 조회 (페이지네이션)", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "잘못된 접근일 때 (code: 100001)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "유효하지 않은 페이지일 때 (code: 100002)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopCategoriesResponse> getShopCategories(ShopCategoriesCondition condition) {
        condition.checkDataConstraintViolation();

        ShopCategoriesResponse response = shopService.getShopCategories(condition);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "상점 카테고리 수정", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "잘못된 접근일 때 (code: 100001)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "존재하지 않을 때 (code: 104004)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "이름이 중복되는 카테고리가 이미 존재할 때 (code: 104005)", response = ExceptionResponse.class),
            @ApiResponse(code = 412, message = "요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = ExceptionResponse.class)
    })
    @ParamValid
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<EmptyResponse> updateShopCategory(
            @ApiParam(name = "상점 카테고리 고유 id", required = true) @PathVariable("id") Integer shopCategoryId,
            @ApiParam(name = "상점 카테고리 정보 JSON", required = true) @RequestBody @Valid UpdateShopCategoryRequest request, BindingResult bindingResult) {
        shopService.updateShopCategory(shopCategoryId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "상점 카테고리 삭제", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "잘못된 접근일 때 (code: 100001)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "존재하지 않을 때 (code: 104004)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "해당 카테고리를 사용하고 있는 상점이 존재하여 삭제할 수 없는 경우 (code: 104006)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteShopCategory(@ApiParam(name = "상점 카테고리 고유 id", required = true) @PathVariable("id") Integer shopCategoryId) {
        shopService.deleteShopCategory(shopCategoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ============================================ 상점 ================================================

    @ParamValid
    @ApiOperation(value = "상점 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createShop(@RequestBody @Valid CreateShopRequest request, BindingResult bindingResult) {
        request.checkDataConstraintViolation(); // javax validation으로 판단할 수 없는 제약조건 검사

        shopService.createShop(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopResponse> getShop(@PathVariable("id") Integer shopId) {
        ShopResponse response = shopService.getShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "상점 리스트 조회 (페이지네이션)", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopsResponse> getShops(ShopsCondition condition) {
        ShopsResponse response = shopService.getShops(condition);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "상점 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<EmptyResponse> updateShop(@PathVariable("id") Integer shopId, @RequestBody @Valid UpdateShopRequest request, BindingResult bindingResult) throws Exception {
        request.checkDataConstraintViolation(); // javax validation으로 판단할 수 없는 제약조건 검사

        shopService.updateShop(shopId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "상점 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteShop(@PathVariable("id") Integer shopId) {
        shopService.deleteShop(shopId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "상점 삭제 해제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/undelete", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> undeleteShop(@PathVariable("id") Integer shopId) {
        shopService.undeleteOfShop(shopId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ============================================= 메뉴 카테고리 =============================================

    @ParamValid
    @ApiOperation(value = "특정 상점의 메뉴 카테고리 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createMenuCategory(@PathVariable("id") Integer shopId, @RequestBody @Valid CreateShopMenuCategoryRequest request, BindingResult bindingResult) throws Exception {
        shopService.createMenuCategory(shopId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "특정 상점의 모든 메뉴 카테고리 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenuCategoriesOfShopResponse> getAllMenuCategoriesOfShop(@PathVariable("id") Integer shopId) throws Exception {
        AllMenuCategoriesOfShopResponse response = shopService.getAllMenuCategoriesOfShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 상점의 메뉴 카테고리 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/categories/{categoryId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteMenuCategory(@PathVariable("shopId") Integer shopId, @PathVariable("categoryId") Integer menuCategoryId) throws Exception {
        shopService.deleteMenuCategory(shopId, menuCategoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // =============================================== 메뉴 =================================================

    @ParamValid
    @ApiOperation(value = "특정 상점의 메뉴 생성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createMenu(@PathVariable("id") Integer shopId, @RequestBody @Valid CreateShopMenuRequest request, BindingResult bindingResult) throws Exception {
        request.checkDataConstraintViolation(); // javax validation으로 판단할 수 없는 제약조건 검사

        shopService.createMenu(shopId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "특정 상점의 메뉴 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<MenuResponse> getMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId) throws Exception {
        MenuResponse response = shopService.getMenu(shopId, menuId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 상점의 모든 메뉴 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenusOfShopResponse> getAllMenusOfShop(@PathVariable("id") Integer shopId) throws Exception {
        AllMenusOfShopResponse response = shopService.getAllMenusOfShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "특정 상점의 메뉴 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.PUT)
    ResponseEntity<EmptyResponse> updateMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId, @RequestBody @Valid UpdateShopMenuRequest request, BindingResult bindingResult) throws Exception {
        request.checkDataConstraintViolation(); // javax validation으로 판단할 수 없는 제약조건 검사

        shopService.updateMenu(shopId, menuId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "특정 상점의 메뉴 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId) {
        shopService.deleteMenu(shopId, menuId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "특정 상점의 메뉴 숨김", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}/hide", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> hideMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId) {
        shopService.hideMenu(shopId, menuId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "특정 상점의 메뉴 숨김 해제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "{shopId}/menus/{menuId}/reveal", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> revealMenu(@PathVariable("shopId") Integer shopId, @PathVariable("menuId") Integer menuId) {
        shopService.revealMenu(shopId, menuId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
