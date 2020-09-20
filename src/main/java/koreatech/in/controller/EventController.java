package koreatech.in.controller;

import io.swagger.annotations.*;
import koreatech.in.annotation.*;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Event.EventArticle;
import koreatech.in.domain.Event.EventComment;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.service.EventService;
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
import java.util.List;
import java.util.Map;

@Auth(role = Auth.Role.OWNER)
@Controller
public class EventController {
    @Inject
    private EventService eventService;

    @ApiOff
    @AuthExcept
    @ApiOperation(value = "전체 홍보글 조회")
    @RequestMapping(value = "/events", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> getEventArticles(@ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<>(eventService.getEventArticles(criteria), HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "홍보글 작성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/events", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EventArticle> createEventArticle(@ApiParam(value = "(required: title, event_title, content, shop_id, start_date, end_date), (optional: thumbnail)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) EventArticle eventArticle, BindingResult result) throws Exception {
        EventArticle clear = new EventArticle();

        return new ResponseEntity<>(eventService.createEventArticle((EventArticle) StringXssChecker.xssCheck(eventArticle, clear)), HttpStatus.CREATED);
    }

    @ApiOff
    @AuthExcept
    @ApiOperation(value = "특정 홍보글 조회")
    @RequestMapping(value = "/events/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> getEventArticle(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<>(eventService.getEventArticle(id), HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @ApiOperation(value = "홍보글 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/events/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<EventArticle> updateEventArticle(@ApiParam(value = "(optional: title, event_title, content, start_date, end_date)", required = false) @RequestBody @Validated(ValidationGroups.Update.class) EventArticle eventArticle, BindingResult bindingResult, @ApiParam(required = true) @PathVariable int id) throws Exception {
        EventArticle clear = new EventArticle();
        return new ResponseEntity<>(eventService.updateEventArticle((EventArticle) StringXssChecker.xssCheck(eventArticle, clear), id), HttpStatus.CREATED);
    }

    @ApiOff
    @ApiOperation(value = "홍보글 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/events/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> deleteEventArticle(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<>(eventService.deleteEventArticle(id), HttpStatus.OK);
    }

    @ApiOff
    @Auth(role = Auth.Role.USER)
    @ParamValid
    @ApiOperation(value = "홍보글 댓글 작성", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/events/{articleId}/comments", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<EventComment> createEventComment(@ApiParam(value = "(required: content)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) EventComment comment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "articleId") int articleId) throws Exception {
        EventComment clear = new EventComment();
        return new ResponseEntity<>(eventService.createEventComment((EventComment) StringXssChecker.xssCheck(comment, clear), articleId), HttpStatus.CREATED);
    }

//    @AuthExcept
//    @ApiOperation(value = "특정 홍보글 댓글 조회", authorizations = {@Authorization(value = "Authorization")})
//    @RequestMapping(value = "/events/{articleId}/comments/{commentId}", method = RequestMethod.GET)
//    public @ResponseBody
//    ResponseEntity<EventComment> getEventComment(@ApiParam(required = true) @PathVariable int articleId, @ApiParam(required = true) @PathVariable int commentId) throws Exception {
//
//        return new ResponseEntity<>(eventService.getEventComment(articleId, commentId), HttpStatus.OK);
//    }

    @ApiOff
    @Auth(role = Auth.Role.USER)
    @ParamValid
    @ApiOperation(value = "홍보글 댓글 수정", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/events/{articleId}/comments/{commentId}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity<EventComment> updateEventComment(@ApiParam(value = "(optional: content)", required = false) @RequestBody @Validated(ValidationGroups.Update.class) EventComment comment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable int articleId, @ApiParam(required = true) @PathVariable int commentId) throws Exception {
        EventComment clear = new EventComment();
        return new ResponseEntity<>(eventService.updateEventComment((EventComment) StringXssChecker.xssCheck(comment, clear), articleId, commentId), HttpStatus.CREATED);
    }

    @ApiOff
    @Auth(role = Auth.Role.USER)
    @ApiOperation(value = "홍보글 댓글 삭제", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/events/{articleId}/comments/{commentId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> deleteEventComment(@ApiParam(required = true) @PathVariable int articleId, @ApiParam(required = true) @PathVariable int commentId) throws Exception {

        return new ResponseEntity<>(eventService.deleteEventComment(articleId, commentId), HttpStatus.OK);
    }

    @ApiOff
    @ApiOperation(value = "점주의 가게 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/events/my/shops", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> getMyShops() {

        return new ResponseEntity<>(eventService.getMyShops(), HttpStatus.OK);
    }

    @ApiOff
    @ApiOperation(value = "점주의 진행 중인 홍보 조회", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/events/pending/my", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> getMyPendingEvent(@ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<>(eventService.getMyPendingEvent(criteria), HttpStatus.OK);
    }

    @ApiOff
    @AuthExcept
    @ApiOperation(value = "진행 중인 전체 홍보글 조회")
    @RequestMapping(value = "/events/pending", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> getPendingEvents(@ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<>(eventService.getPendingEvents(criteria), HttpStatus.OK);
    }

    @ApiOff
    @AuthExcept
    @ApiOperation(value = "마감된 전체 홍보글 조회")
    @RequestMapping(value = "/events/closed", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Map<String, Object>> getClosedEvents(@ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<>(eventService.getClosedEvents(criteria), HttpStatus.OK);
    }

    @ApiOff
    @ApiOperation(value = "특정 홍보글 수정 권한 확인", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/events/grant/check", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Map<String, Boolean>> checkGrantEditEvent(@ApiParam(required = true) @RequestBody Map<String, Integer> article_id) throws Exception {
        if (article_id == null || !article_id.containsKey("article_id"))
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 데이터입니다.", 0));

        return new ResponseEntity<>(eventService.checkGrantEditEvent(article_id.get("article_id")), HttpStatus.OK);
    }

    @ApiOff
    @AuthExcept
    @ApiOperation(value = "진행 중인 이벤트 랜덤 조회")
    @RequestMapping(value = "/events/pending/random", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<EventArticle> getRandomPendingEvent() {

        return new ResponseEntity<>(eventService.getRandomPendingEvent(), HttpStatus.OK);
    }
}
