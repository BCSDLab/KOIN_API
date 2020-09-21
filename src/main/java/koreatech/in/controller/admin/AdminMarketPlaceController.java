package koreatech.in.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.MarketPlace.Item;
import koreatech.in.domain.MarketPlace.ItemComment;
import koreatech.in.service.MarketPlaceService;
import koreatech.in.util.StringXssChecker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.MARKET)
@Controller
public class AdminMarketPlaceController {
    @Inject
    private MarketPlaceService marketPlaceService;

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/market/items", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createItem(@ApiParam(value = "(required: type, title), (optional: content, state, price, phone, is_phone_open, thumbnail, is_deleted)",required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Item item, BindingResult bindingResult) throws Exception {
        Item clear = new Item();
        return new ResponseEntity<Item>(marketPlaceService.createItemForAdmin((Item) StringXssChecker.xssCheck(item, clear)), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/admin/market/items", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getItems(@ApiParam(value = "서비스 타입(0: 팝니다 서비스, 1: 삽니다 서비스)", required = false) @RequestParam(value = "type", required = false, defaultValue="3") int type,
                            @ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<Map<String, Object>>(marketPlaceService.getItemsForAdmin(type, criteria), HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/market/items/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getItem(@ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(marketPlaceService.getItemForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/market/items/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateItem(@ApiParam(value = "(optional: title, content, state, price, phone, is_phone_open, thumbnail, is_deleted)",required = true) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Item item, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {
        Item clear = new Item();
        return new ResponseEntity<Item>(marketPlaceService.updateItemForAdmin((Item) StringXssChecker.xssCheck(item, clear), id), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/market/items/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteItem(@ApiParam(required = true) @PathVariable(value = "id") int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(marketPlaceService.deleteItemForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/market/items/{itemId}/comments", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createItemComment(@ApiParam(value = "(optional: content)", required = false) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) ItemComment itemComment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "itemId") int item_id) throws Exception {
        ItemComment clear = new ItemComment();
        return new ResponseEntity<ItemComment>(marketPlaceService.createItemCommentForAdmin((ItemComment)StringXssChecker.xssCheck(itemComment, clear), item_id), HttpStatus.CREATED);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/market/items/{itemId}/comments/{commentId}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateItemComment(@ApiParam(value = "(optional: content)", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) ItemComment itemComment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "itemId") int itemId, @ApiParam(required = true) @PathVariable(value = "commentId") int commentId) throws Exception {
        ItemComment clear = new ItemComment();
        return new ResponseEntity<ItemComment>(marketPlaceService.updateItemCommentForAdmin((ItemComment)StringXssChecker.xssCheck(itemComment, clear), itemId, commentId), HttpStatus.CREATED);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/market/items/{itemId}/comments/{commentId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getItemComment(@ApiParam(required = true) @PathVariable(value = "itemId") int itemId, @ApiParam(required = true) @PathVariable(value = "commentId") int commentId) throws Exception {

        return new ResponseEntity<ItemComment>(marketPlaceService.getItemCommentForAdmin(itemId, commentId), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/market/items/{itemId}/comments/{commentId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteItemComment(@ApiParam(required = true) @PathVariable(value = "itemId") int itemId, @ApiParam(required = true) @PathVariable(value = "commentId") int commentId) throws Exception {

        return new ResponseEntity<Map<String, Object>>(marketPlaceService.deleteItemCommentForAdmin(itemId, commentId), HttpStatus.OK);
    }
}
