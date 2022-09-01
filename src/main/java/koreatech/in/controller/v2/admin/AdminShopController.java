package koreatech.in.controller.v2.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.controller.v2.dto.CreateShopDTO;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.Shop.Shop;
import koreatech.in.service.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
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
