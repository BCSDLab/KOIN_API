package koreatech.in.controller;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.dto.EmptyResponse;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.normal.shop.request.CreateMenuCategoryRequest;
import koreatech.in.dto.normal.shop.response.AllMenuCategoriesOfShopResponse;
import koreatech.in.service.OwnerShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Auth(role = Auth.Role.OWNER, authority = Auth.Authority.SHOP)
@Api(tags = "(Normal) Owner Shop", description = "상점 (사장님 전용)")
@Controller
@RequestMapping("/owner/shops")
public class OwnerShopController {
    @Autowired
    private OwnerShopService ownerShopService;

    @ApiOperation(value = "메뉴 카테고리 생성", notes = "한 상점당 최대 20개까지 등록 가능", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 존재하지 않을 때 (code: 104000)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "- 해당 상점에 중복되는 이름의 메뉴 카테고리가 이미 존재할 때 (code: 104011) \n" +
                                               "- 한 상점당 등록할 수 있는 메뉴 카테고리의 최대 개수(20)를 초과하였을 때 (code: 104013)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 요청 데이터 제약조건을 위반하였을 때 (code: 100000)", response = ExceptionResponse.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @ParamValid
    @RequestMapping(value = "/{id}/menus/categories", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EmptyResponse> createMenuCategory(
            @ApiParam(name = "상점 고유 id", required = true) @PathVariable("id") Integer shopId,
            @ApiParam(name = "메뉴 카테고리 정보 JSON", required = true) @RequestBody @Valid CreateMenuCategoryRequest request, BindingResult bindingResult) {
        ownerShopService.createMenuCategory(shopId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "상점의 모든 메뉴 카테고리 조회", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 401, message = "- 잘못된 접근일 때 (code: 100001) \n" +
                                               "- 액세스 토큰이 만료되었을 때 (code: 100004) \n" +
                                               "- 액세스 토큰이 변경되었을 때 (code: 100005)", response = ExceptionResponse.class),
            @ApiResponse(code = 403, message = "- 권한이 없을 때 (code: 100003)", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "- 상점이 존재하지 않을 때 (code: 104000)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "{id}/menus/categories", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<AllMenuCategoriesOfShopResponse> getAllMenuCategoriesOfShop(@ApiParam(name = "상점 고유 id", required = true) @PathVariable("id") Integer shopId) {
        AllMenuCategoriesOfShopResponse response = ownerShopService.getAllMenuCategoriesOfShop(shopId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "상점의 메뉴 카테고리 삭제", authorizations = {@Authorization("Authorization")})
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
            @ApiParam(name = "상점 고유 id", required = true) @PathVariable("shopId") Integer shopId,
            @ApiParam(name = "메뉴 카테고리 고유 id", required = true) @PathVariable("categoryId") Integer menuCategoryId) {
        ownerShopService.deleteMenuCategory(shopId, menuCategoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
