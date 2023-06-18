package koreatech.in.controller;

import javax.validation.Valid;

import koreatech.in.dto.normal.shop.request.UpdateMenuCategoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.RequestDataInvalidResponse;
import koreatech.in.dto.normal.shop.request.CreateMenuCategoryRequest;
import koreatech.in.dto.normal.shop.request.CreateMenuRequest;
import koreatech.in.dto.normal.shop.request.CreateShopRequest;
import koreatech.in.dto.normal.shop.request.UpdateMenuRequest;
import koreatech.in.dto.normal.shop.request.UpdateShopRequest;
import koreatech.in.dto.normal.shop.response.AllMenuCategoriesOfShopResponse;
import koreatech.in.dto.normal.shop.response.AllMenusOfShopResponse;
import koreatech.in.dto.normal.shop.response.AllShopsOfOwnerResponse;
import koreatech.in.dto.normal.shop.response.MenuResponse;
import koreatech.in.dto.normal.shop.response.ShopResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.service.OwnerShopService;
import koreatech.in.util.StringXssChecker;

@Auth(role = Auth.Role.OWNER, authority = Auth.Authority.SHOP)
@Api(tags = "(Normal) Owner Shop", description = "상점 (점주 전용)")
@Controller
@RequestMapping("/owner/shops")
public class OwnerShopController {
    @Autowired
    private OwnerShopService ownerShopService;

    // =============================================== 상점 =================================================

    @ApiOperation(value = "상점 생성", notes = "- 사장님 권한만 허용", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                    "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                    "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class)
    })
    @ParamValid
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createShop(@ApiParam(name = "상점 정보 JSON", required = true) @RequestBody @Valid CreateShopRequest request, BindingResult bindingResult) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        request.checkDataConstraintViolation(); // javax validation으로 판단할 수 없는 제약조건 검사

        ownerShopService.createShop(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점 조회", notes = "- 사장님 권한만 허용\n- 인증 정보에 대한 신원이 해당 상점의 점주가 아니라면 403(Forbidden) 응답", authorizations = {@Authorization("Authorization")})
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
        ShopResponse response = ownerShopService.getShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "자신의 모든 상점 리스트 조회", notes = "- 사장님 권한만 허용", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllShopsOfOwnerResponse> getAllShopsOfOwner() {
        AllShopsOfOwnerResponse response = ownerShopService.getAllShopsOfOwner();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "상점 수정", notes = "- 사장님 권한만 허용\n- 인증 정보에 대한 신원이 해당 상점의 점주가 아니라면 403(Forbidden) 응답", authorizations = {@Authorization("Authorization")})
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

        ownerShopService.updateShop(shopId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // =============================================== 메뉴 카테고리 =================================================

    @ApiOperation(value = "상점 메뉴 카테고리 생성", notes = "- 사장님 권한만 허용\n- 인증 정보에 대한 신원이 해당 상점의 점주가 아니라면 403(Forbidden) 응답", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 존재하지 않을 때 (code: 104000)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 해당 상점에 중복되는 이름의 메뉴 카테고리가 이미 존재할 때 (code: 104011) \n" +
                                               "- 한 상점당 등록할 수 있는 메뉴 카테고리의 최대 개수(20)를 초과하였을 때 (code: 104013)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @ParamValid
    @RequestMapping(value = "/{id}/menus/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createMenuCategory(
            @ApiParam(required = true) @PathVariable("id") Integer shopId,
            @ApiParam(name = "메뉴 카테고리 정보 JSON", required = true) @RequestBody @Valid CreateMenuCategoryRequest request, BindingResult bindingResult) {
        ownerShopService.createMenuCategory(shopId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점의 모든 메뉴 카테고리 조회", notes = "- 사장님 권한만 허용\n- 인증 정보에 대한 신원이 해당 상점의 점주가 아니라면 403(Forbidden) 응답", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 존재하지 않을 때 (code: 104000)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/{id}/menus/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenuCategoriesOfShopResponse> getAllMenuCategoriesOfShop(@ApiParam(required = true) @PathVariable("id") Integer shopId) {
        AllMenuCategoriesOfShopResponse response = ownerShopService.getAllMenuCategoriesOfShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "상점 메뉴 카테고리 수정", notes = "- 사장님 권한만 허용\n- 인증 정보에 대한 신원이 해당 상점의 점주가 아니라면 403(Forbidden) 응답", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(
                    code = 401 , message
                    = "- 잘못된 접근일 때 (code: 100001) \n"
                    + "- 액세스 토큰이 만료되었을 때 (code: 100004) \n"
                    , response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 존재하지 않을 때 (code: 104000)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 해당 상점에 중복되는 이름의 메뉴 카테고리가 이미 존재할 때 (code: 104011) \n", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = RequestDataInvalidResponse.class)
    })
    @ParamValid
    @RequestMapping(value = "/{shopId}/menus/categories", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<EmptyResponse> updateMenuCategory(
            @ApiParam(required = true) @PathVariable("shopId") Integer shopId,
            @ApiParam(name = "메뉴 카테고리 정보 JSON", required = true) @RequestBody @Valid UpdateMenuCategoryRequest request, BindingResult bindingResult) {
        try {
            request = StringXssChecker.xssCheck(request, request.getClass().newInstance());
        } catch (Exception exception) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }

        ownerShopService.updateMenuCategory(shopId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "상점의 메뉴 카테고리 삭제", notes = "- 사장님 권한만 허용\n- 인증 정보에 대한 신원이 해당 상점의 점주가 아니라면 403(Forbidden) 응답", authorizations = {@Authorization("Authorization")})
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
    @RequestMapping(value = "/{shopId}/menus/categories/{categoryId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<EmptyResponse> deleteMenuCategory(
            @ApiParam(required = true) @PathVariable("shopId") Integer shopId,
            @ApiParam(required = true) @PathVariable("categoryId") Integer menuCategoryId) {
        ownerShopService.deleteMenuCategory(shopId, menuCategoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // =============================================== 메뉴 =================================================

    @ApiOperation(value = "상점의 메뉴 생성", notes = "- 사장님 권한만 허용\n- 인증 정보에 대한 신원이 해당 상점의 점주가 아니라면 403(Forbidden) 응답", authorizations = {@Authorization("Authorization")})
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
            @ApiParam(name = "메뉴 정보 JSON", required = true) @RequestBody @Valid CreateMenuRequest request, BindingResult bindingResult) {
        request.checkDataConstraintViolation(); // javax validation 으로 판단할 수 없는 제약조건 검사

        ownerShopService.createMenu(shopId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점의 메뉴 조회", notes = "- 사장님 권한만 허용\n- 인증 정보에 대한 신원이 해당 상점의 점주가 아니라면 403(Forbidden) 응답", authorizations = {@Authorization("Authorization")})
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
    ResponseEntity<MenuResponse> getMenu(
            @ApiParam(required = true) @PathVariable("shopId") Integer shopId,
            @ApiParam(required = true) @PathVariable("menuId") Integer menuId) {
        MenuResponse response = ownerShopService.getMenu(shopId, menuId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "상점의 모든 메뉴 조회", notes = "- 사장님 권한만 허용\n- 인증 정보에 대한 신원이 해당 상점의 점주가 아니라면 403(Forbidden) 응답", authorizations = {@Authorization("Authorization")})
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
        AllMenusOfShopResponse response = ownerShopService.getAllMenusOfShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "상점의 메뉴 수정", notes = "- 사장님 권한만 허용\n- 인증 정보에 대한 신원이 해당 상점의 점주가 아니라면 403(Forbidden) 응답", authorizations = {@Authorization("Authorization")})
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
            @ApiParam(name = "메뉴 정보 JSON", required = true) @RequestBody @Valid UpdateMenuRequest request, BindingResult bindingResult) {
        request.checkDataConstraintViolation(); // javax validation 으로 판단할 수 없는 제약조건 검사

        ownerShopService.updateMenu(shopId, menuId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "상점의 메뉴 삭제", notes = "- 사장님 권한만 허용\n- 인증 정보에 대한 신원이 해당 상점의 점주가 아니라면 403(Forbidden) 응답", authorizations = {@Authorization("Authorization")})
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
        ownerShopService.deleteMenu(shopId, menuId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
