package koreatech.in.controller;

import io.swagger.annotations.*;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.LostAndFound.LostItem;
import koreatech.in.domain.LostAndFound.LostItemComment;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.UserMapper;
import koreatech.in.service.LostAndFoundService;
import koreatech.in.util.StringXssChecker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Auth(role = Auth.Role.USER)
@Controller
public class LostAndFoundController {
    @Inject
    private LostAndFoundService lostAndFoundService;

    @AuthExcept
    @RequestMapping(value = "/lost/lostItems", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getLostItems(@ApiParam(value = "서비스 타입(0: 습득 서비스, 1: 분실 서비스)", required = false) @RequestParam(value = "type", required = false, defaultValue="3") int type,
                                @ModelAttribute("criteria") Criteria criteria) throws Exception {

        //TODO: 현재 lost_items 테이블 date 컬럼이 DATE 형식이라 0000-00-00 select시 에러발생, Varchar로 변경
        return new ResponseEntity<Map<String, Object>>(lostAndFoundService.getLostItems(type, criteria), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lost/lostItems", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createLostItem(@ApiParam(value = "(required: type, title), (optional: location, date, content, state, phone, is_phone_open, thumbnail)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) LostItem lostItem, BindingResult result) throws Exception {
        LostItem clear = new LostItem();
        return new ResponseEntity<LostItem>(lostAndFoundService.createLostItem((LostItem) StringXssChecker.xssCheck(lostItem, clear)), HttpStatus.CREATED);
    }

    @AuthExcept
    @RequestMapping(value = "/lost/lostItems/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getLostItem(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(lostAndFoundService.getLostItem(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lost/lostItems/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateLostItem(@ApiParam(value="(required: type), (optional: title, location, date, content, state, phone, is_phone_open, thumbnail)", required = false) @RequestBody @Validated(ValidationGroups.Update.class) LostItem lostItem, BindingResult bindingResult, @ApiParam(required = true) @PathVariable int id) throws Exception {
        LostItem clear = new LostItem();
        return new ResponseEntity<LostItem>(lostAndFoundService.updateLostItem((LostItem) StringXssChecker.xssCheck(lostItem, clear), id), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lost/lostItems/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteLostItem(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(lostAndFoundService.deleteLostItem(id), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lost/lostItems/{id}/state", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateStateOfLostItem(@ApiParam(value = "(required: state)", required = true) @RequestBody LostItem state, @ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<LostItem>(lostAndFoundService.updateStateOfLostItem(state.getState(), id), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lost/my/lostItems", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getMyLostItemList(@ApiParam(value = "서비스 타입(0: 습득 서비스, 1: 분실 서비스)", required = true) @RequestParam(value = "type") int type,
                                     @ModelAttribute("criteria") Criteria criteria) throws Exception {
        Map<String, Object> map = lostAndFoundService.getMyLostItemList(type, criteria);

        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lost/lostItems/{lostItemId}/comments", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createLostItemComment(@ApiParam(value = "(optional: content)", required = false) @RequestBody @Validated(ValidationGroups.Create.class) LostItemComment lostItemComment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "lostItemId") int lost_item_id) throws Exception {
        LostItemComment clear = new LostItemComment();
        return new ResponseEntity<LostItemComment>(lostAndFoundService.createLostItemComment((LostItemComment)StringXssChecker.xssCheck(lostItemComment, clear), lost_item_id), HttpStatus.CREATED);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lost/lostItems/{lostItemId}/comments/{commentId}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateLostItemComment(@ApiParam(value = "(optional: content)", required = false) @RequestBody @Validated(ValidationGroups.Update.class) LostItemComment lostItemComment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "lostItemId") int lost_item_id, @ApiParam(required = true) @PathVariable(value = "commentId") int commentId) throws Exception {
        LostItemComment clear = new LostItemComment();
        return new ResponseEntity<LostItemComment>(lostAndFoundService.updateLostItemComment((LostItemComment)StringXssChecker.xssCheck(lostItemComment, clear), lost_item_id, commentId), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lost/lostItems/{lostItemId}/comments/{commentId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getLostItemComment(@ApiParam(required = true) @PathVariable(value = "lostItemId") int lost_item_id, @ApiParam(required = true) @PathVariable(value = "commentId") int commentId) throws Exception {

        return new ResponseEntity<LostItemComment>(lostAndFoundService.getLostItemComment(lost_item_id, commentId), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lost/lostItems/{lostItemId}/comments/{commentId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteLostItemComment(@ApiParam(required = true) @PathVariable(value = "lostItemId") int lost_item_id, @ApiParam(required = true) @PathVariable(value = "commentId") int commentId) throws Exception {

        return new ResponseEntity<Map<String, Object>>(lostAndFoundService.deleteLostItemComment(lost_item_id, commentId), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lost/lostItems/grant/check", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity checkGrantEditItem(@ApiParam(required = true) @RequestBody Map<String, Integer> lostItem_id) throws Exception {
        if (lostItem_id == null || !lostItem_id.containsKey("lostItem_id"))
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 데이터입니다.", 0));

        return new ResponseEntity<Map<String, Boolean>>(lostAndFoundService.checkGrantEditLostItem(lostItem_id.get("lostItem_id")), HttpStatus.OK);
    }

    @ApiImplicitParams(
            @ApiImplicitParam(name = "mtfRequest", required = true, paramType = "form", dataType = "file")
    )
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lost/lostItems/image/upload", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity itemImagesUpload(@ApiParam(required = true) MultipartHttpServletRequest mtfRequest) throws Exception {
        List<MultipartFile> fileList = mtfRequest.getFiles("image");

        return new ResponseEntity<Map<String, Object>>(lostAndFoundService.lostItemImagesUpload(fileList), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/lost/lostItems/image/thumbnail/upload", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity itemThumbnailImageUpload(@ApiParam(required = true) @RequestParam(value = "image") MultipartFile image) throws Exception {
        return new ResponseEntity<Map<String, Object>>(lostAndFoundService.lostItemThumbnailImageUpload(image), HttpStatus.CREATED);
    }

}
