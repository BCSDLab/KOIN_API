package koreatech.in.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.LostAndFound.LostItem;
import koreatech.in.domain.LostAndFound.LostItemComment;
import koreatech.in.service.LostAndFoundService;
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

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.LOST)
@Controller
public class AdminLostAndFoundController {
    @Inject
    LostAndFoundService lostAndFoundService;

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lost/lostItems", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createLostItem(@ApiParam(value = "(required: type, title), (optional: location, date, content, state, phone, is_phone_open, thumbnail, is_deleted)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) LostItem lostItem, BindingResult result) throws Exception {
        LostItem clear = new LostItem();
        return new ResponseEntity<LostItem>(lostAndFoundService.createLostItemForAdmin((LostItem) StringXssChecker.xssCheck(lostItem, clear)), HttpStatus.CREATED);
    }

    @ApiOff
    @RequestMapping(value = "/admin/lost/lostItems", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getLostItems(@ApiParam(value = "서비스 타입(0: 습득 서비스, 1: 분실 서비스)", required = false) @RequestParam(value = "type", required = false, defaultValue="3") int type,
                                @ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<Map<String, Object>>(lostAndFoundService.getLostItemsForAdmin(type, criteria), HttpStatus.OK);
    }

    @ApiOff
    @RequestMapping(value = "/admin/lost/lostItems/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getLostItem(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(lostAndFoundService.getLostItemForAdmin(id), HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lost/lostItems/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateLostItem(@ApiParam(value="(required: type), (optional: title, location, date, content, state, phone, is_phone_open, thumbnail, is_deleted)", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) LostItem lostItem, BindingResult bindingResult, @ApiParam(required = true) @PathVariable int id) throws Exception {
        LostItem clear = new LostItem();
        return new ResponseEntity<LostItem>(lostAndFoundService.updateLostItemForAdmin((LostItem) StringXssChecker.xssCheck(lostItem, clear), id), HttpStatus.CREATED);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lost/lostItems/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteLostItem(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(lostAndFoundService.deleteLostItemForAdmin(id), HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lost/lostItems/{lostItemId}/comments", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createLostItemComment(@ApiParam(value = "(optional: content)", required = false) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) LostItemComment lostItemComment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "lostItemId") int lost_item_id) throws Exception {
        LostItemComment clear = new LostItemComment();
        return new ResponseEntity<LostItemComment>(lostAndFoundService.createLostItemCommentForAdmin((LostItemComment)StringXssChecker.xssCheck(lostItemComment, clear), lost_item_id), HttpStatus.CREATED);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lost/lostItems/{lostItemId}/comments/{commentId}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateLostItemComment(@ApiParam(value = "(optional: content)", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) LostItemComment lostItemComment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "lostItemId") int lost_item_id, @ApiParam(required = true) @PathVariable(value = "commentId") int commentId) throws Exception {
        LostItemComment clear = new LostItemComment();
        return new ResponseEntity<LostItemComment>(lostAndFoundService.updateLostItemCommentForAdmin((LostItemComment)StringXssChecker.xssCheck(lostItemComment, clear), lost_item_id, commentId), HttpStatus.CREATED);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lost/lostItems/{lostItemId}/comments/{commentId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getLostItemComment(@ApiParam(required = true) @PathVariable(value = "lostItemId") int lost_item_id, @ApiParam(required = true) @PathVariable(value = "commentId") int commentId) throws Exception {

        return new ResponseEntity<LostItemComment>(lostAndFoundService.getLostItemCommentForAdmin(lost_item_id, commentId), HttpStatus.OK);
    }

    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/lost/lostItems/{lostItemId}/comments/{commentId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteLostItemComment(@ApiParam(required = true) @PathVariable(value = "lostItemId") int lost_item_id, @ApiParam(required = true) @PathVariable(value = "commentId") int commentId) throws Exception {

        return new ResponseEntity<Map<String, Object>>(lostAndFoundService.deleteLostItemCommentForAdmin(lost_item_id, commentId), HttpStatus.OK);
    }
}
