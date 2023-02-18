package koreatech.in.controller.admin;

import io.swagger.annotations.*;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.RequestDataInvalidResponse;
import koreatech.in.dto.admin.shop.request.*;
import koreatech.in.dto.admin.shop.response.*;
import koreatech.in.service.admin.AdminShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 중복되는 이름의 카테고리가 이미 존재할 때 (code: 104005)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
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
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점 카테고리가 조회되지 않을 때 (code: 104004)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopCategoryResponse> getShopCategory(@ApiParam(required = true) @PathVariable("id") Integer shopCategoryId) {
        ShopCategoryResponse response = shopService.getShopCategory(shopCategoryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "상점 카테고리 리스트 조회 (페이지네이션)", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 유효하지 않은 페이지일 때 (code: 100002)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
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
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점 카테고리가 조회되지 않을 때 (code: 104004)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 이름이 중복되는 카테고리가 이미 존재할 때 (code: 104005)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @ParamValid
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<EmptyResponse> updateShopCategory(
            @ApiParam(required = true) @PathVariable("id") Integer shopCategoryId,
            @ApiParam(name = "상점 카테고리 정보 JSON", required = true) @RequestBody @Valid UpdateShopCategoryRequest request, BindingResult bindingResult) {
        shopService.updateShopCategory(shopCategoryId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "상점 카테고리 삭제", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점 카테고리가 조회되지 않을 때 (code: 104004)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 해당 카테고리를 사용하고 있는 상점이 존재하여 삭제할 수 없을 때 (code: 104006)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteShopCategory(@ApiParam(required = true) @PathVariable("id") Integer shopCategoryId) {
        shopService.deleteShopCategory(shopCategoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ============================================ 상점 ================================================

    @ApiOperation(value = "상점 생성", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- (category_ids 리스트에 있는 특정 id에 대한) 상점 카테고리가 조회되지 않는 경우가 있을 때 (code: 104004)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @ParamValid
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createShop(@ApiParam(name = "상점 정보 JSON", required = true) @RequestBody @Valid CreateShopRequest request, BindingResult bindingResult) {
        request.checkDataConstraintViolation(); // javax validation으로 판단할 수 없는 제약조건 검사

        shopService.createShop(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점 조회", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 존재하지 않을 때 (code: 104000)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopResponse> getShop(@ApiParam(required = true) @PathVariable("id") Integer shopId) {
        ShopResponse response = shopService.getShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "상점 리스트 조회 (페이지네이션)", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 유효하지 않은 페이지일 때 (code: 100002)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopsResponse> getShops(ShopsCondition condition) {
        condition.checkDataConstraintViolation();

        ShopsResponse response = shopService.getShops(condition);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "상점 수정", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 존재하지 않을 때 (code: 104000)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- (category_ids 리스트에 있는 특정 id에 대한) 상점 카테고리가 조회되지 않는 경우가 있을 때 (code: 104004)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @ParamValid
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<EmptyResponse> updateShop(
            @ApiParam(required = true) @PathVariable("id") Integer shopId,
            @ApiParam(name = "상점 정보 JSON", required = true) @RequestBody @Valid UpdateShopRequest request, BindingResult bindingResult) {
        request.checkDataConstraintViolation(); // javax validation으로 판단할 수 없는 제약조건 검사

        shopService.updateShop(shopId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "상점 삭제", notes = "상점을 soft delete 합니다.", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 존재하지 않을 때 (code: 104000)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 상점이 이미 삭제(soft delete) 되어있을 경우 (code: 104002)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteShop(@ApiParam(required = true) @PathVariable("id") Integer shopId) {
        shopService.deleteShop(shopId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "상점 삭제 해제", notes = "상점의 soft delete 상태를 해제합니다.", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 존재하지 않을 때 (code: 104000)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 삭제되어 있는 상점이 아닐 경우 (code: 104003)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/{id}/undelete", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> undeleteShop(@ApiParam(required = true) @PathVariable("id") Integer shopId) {
        shopService.undeleteOfShop(shopId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // ============================================= 메뉴 카테고리 =============================================

    @ApiOperation(value = "특정 상점의 메뉴 카테고리 생성", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 존재하지 않을 때 (code: 104000)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 중복되는 이름의 카테고리가 이미 존재할 때 (code: 104011) \n" +
                                               "- 한 상점당 등록할 수 있는 메뉴 카테고리의 최대 개수(20개)를 초과하였을 때 (code: 104013)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @ParamValid
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createMenuCategory(
            @ApiParam(required = true) @PathVariable("id") Integer shopId,
            @ApiParam(name = "메뉴 카테고리 정보 JSON", required = true) @RequestBody @Valid CreateShopMenuCategoryRequest request, BindingResult bindingResult) {
        shopService.createMenuCategory(shopId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "특정 상점의 모든 메뉴 카테고리 조회", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 존재하지 않을 때 (code: 104000)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenuCategoriesOfShopResponse> getAllMenuCategoriesOfShop(@ApiParam(required = true) @PathVariable("id") Integer shopId) {
        AllMenuCategoriesOfShopResponse response = shopService.getAllMenuCategoriesOfShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 상점의 메뉴 카테고리 삭제", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 존재하지 않을 때 (code: 104000) \n" +
                                               "- 메뉴 카테고리가 존재하지 않을 때 (code: 104010) \n" +
                                               "  - 만약 categoryId에 대한 카테고리가, shopId에 대한 상점에 속해있는 카테고리가 아닌 경우도 포함", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 해당 카테고리를 사용하고 있는 메뉴가 존재하여 삭제할 수 없는 경우 (code: 104012)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "{shopId}/menus/categories/{categoryId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteMenuCategory(
            @ApiParam(required = true) @PathVariable("shopId") Integer shopId,
            @ApiParam(required = true) @PathVariable("categoryId") Integer menuCategoryId) {
        shopService.deleteMenuCategory(shopId, menuCategoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // =============================================== 메뉴 =================================================

    @ApiOperation(value = "특정 상점의 메뉴 생성", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 조회되지 않을 때 (code: 104000) \n" +
                                               "- (category_ids 리스트에 있는 특정 id에 대한) 메뉴 카테고리가 조회되지 않는 경우가 있을 때 (code: 104010)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @ParamValid
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createMenu(
            @ApiParam(required = true) @PathVariable("id") Integer shopId,
            @ApiParam(name = "메뉴 정보 JSON", required = true) @RequestBody @Valid CreateShopMenuRequest request, BindingResult bindingResult) {
        request.checkDataConstraintViolation(); // javax validation 으로 판단할 수 없는 제약조건 검사

        shopService.createMenu(shopId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "특정 상점의 메뉴 조회", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 조회되지 않을 때 (code: 104000) \n" +
                                               "- 메뉴가 조회되지 않을 때 (code: 104007) \n" +
                                               "  - 만약 menuId에 대한 메뉴가, shopId에 대한 상점에 속해있는 메뉴가 아닌 경우도 포함", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ShopMenuResponse> getMenu(
            @ApiParam(required = true) @PathVariable("shopId") Integer shopId,
            @ApiParam(required = true) @PathVariable("menuId") Integer menuId) {
        ShopMenuResponse response = shopService.getMenu(shopId, menuId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 상점의 모든 메뉴 조회", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 조회되지 않을 때 (code: 104000)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/{id}/menus", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenusOfShopResponse> getAllMenusOfShop(@ApiParam(required = true) @PathVariable("id") Integer shopId) {
        AllMenusOfShopResponse response = shopService.getAllMenusOfShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 상점의 메뉴 수정", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 조회되지 않을 때 (code: 104000) \n" +
                                               "- 메뉴가 조회되지 않을 때 (code: 104007) \n" +
                                               "  - 만약 menuId에 대한 메뉴가, shopId에 대한 상점에 속해있는 메뉴가 아닌 경우도 포함 \n" +
                                               "- (category_ids 리스트에 있는 특정 id에 대한) 메뉴 카테고리가 조회되지 않는 경우가 있을 때 (code: 104010)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @ParamValid
    @RequestMapping(value = "/{shopId}/menus/{menuId}", method = RequestMethod.PUT)
    ResponseEntity<EmptyResponse> updateMenu(
            @ApiParam(required = true) @PathVariable("shopId") Integer shopId,
            @ApiParam(required = true) @PathVariable("menuId") Integer menuId,
            @ApiParam(name = "메뉴 정보 JSON", required = true) @RequestBody @Valid UpdateShopMenuRequest request, BindingResult bindingResult) {
        request.checkDataConstraintViolation(); // javax validation 으로 판단할 수 없는 제약조건 검사

        shopService.updateMenu(shopId, menuId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "특정 상점의 메뉴 삭제", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 조회되지 않을 때 (code: 104000) \n" +
                                               "- 메뉴가 조회되지 않을 때 (code: 104007) \n" +
                                               "  - 만약 menuId에 대한 메뉴가, shopId에 대한 상점에 속해있는 메뉴가 아닌 경우도 포함", response = ExceptionResponse.class),
    })
    @RequestMapping(value = "{shopId}/menus/{menuId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteMenu(
            @ApiParam(required = true) @PathVariable("shopId") Integer shopId,
            @ApiParam(required = true) @PathVariable("menuId") Integer menuId) {
        shopService.deleteMenu(shopId, menuId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOff @ApiIgnore @Deprecated
    @ApiOperation(value = "특정 상점의 메뉴 숨김", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 조회되지 않을 때 (code: 104000) \n" +
                                               "- 메뉴가 조회되지 않을 때 (code: 104007) \n" +
                                               "  - 만약 menuId에 대한 메뉴가, shopId에 대한 상점에 속해있는 메뉴가 아닌 경우도 포함", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 이미 숨김 처리 되어있을 때 (code: 104008)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "{shopId}/menus/{menuId}/hide", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> hideMenu(
            @ApiParam(required = true) @PathVariable("shopId") Integer shopId,
            @ApiParam(required = true) @PathVariable("menuId") Integer menuId) {
        shopService.hideMenu(shopId, menuId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOff @ApiIgnore @Deprecated
    @ApiOperation(value = "특정 상점의 메뉴 숨김 해제", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 조회되지 않을 때 (code: 104000) \n" +
                                               "- 메뉴가 조회되지 않을 때 (code: 104007) \n" +
                                               "  - 만약 menuId에 대한 메뉴가, shopId에 대한 상점에 속해있는 메뉴가 아닌 경우도 포함", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 숨김 처리되어있는 메뉴가 아닐 때 (code: 104009)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "{shopId}/menus/{menuId}/reveal", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> revealMenu(
            @ApiParam(required = true) @PathVariable("shopId") Integer shopId,
            @ApiParam(required = true) @PathVariable("menuId") Integer menuId) {
        shopService.revealMenu(shopId, menuId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
