package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.*;
import koreatech.in.domain.Community.Article;
import koreatech.in.domain.Community.Board;
import koreatech.in.domain.Community.Comment;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.service.CommunityService;
import koreatech.in.util.StringXssChecker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Api(tags = "(Normal) Community", description = "커뮤니티")
@Auth(role = Auth.Role.USER)
@Controller
public class CommunityController {
    @Inject
    private CommunityService communityService;

    @ApiIgnore
    @ApiOff
    @AuthExcept
    @RequestMapping(value = "/boards", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getBoards() throws Exception {

        return new ResponseEntity<List<Board>>(communityService.getBoards(), HttpStatus.OK);
    }

    @ApiIgnore
    @ApiOff
    @AuthExcept
    @RequestMapping(value = "/boards/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getBoard(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<Board>(communityService.getBoard(id), HttpStatus.OK);
    }

    @AuthExcept
    @RequestMapping(value = "/articles", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getArticles(@ApiParam(required = true) @RequestParam(value = "boardId") int boardId,
                               @ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<Map<String, Object>>(communityService.getArticles(boardId, criteria), HttpStatus.OK);
    }

    @ApiIgnore
    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/articles", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createArticle(@ApiParam(value = "(required: board_id, title, content)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) Article article, BindingResult bindingResult) throws Exception {
        Article clear = new Article();
        return new ResponseEntity<Article>(communityService.createArticle((Article)StringXssChecker.xssCheck(article, clear)), HttpStatus.CREATED);
    }

    @ApiIgnore
    @ApiOff
    @AuthExcept
    @RequestMapping(value = "/articles/new/list", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getNewArticles(@ApiParam(required = true) @RequestParam(value = "boardId") int boardId,
                                  @ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<List<Article>>(communityService.getNewArticles(boardId, criteria), HttpStatus.OK);
    }

    @AuthExcept
    @RequestMapping(value = "/articles/hot/list", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getHotArticles() throws Exception {

        return new ResponseEntity<List<Map<String, Object>>>(communityService.getCachedHotArticle(), HttpStatus.OK);
    }

    @AuthExcept
    @RequestMapping(value = "/articles/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getArticle(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(communityService.getArticle(id), HttpStatus.OK);
    }

    @ApiIgnore
    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/articles/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateArticle(@ApiParam(value = "(required: board_id), (optional: title, content)", required = true) @RequestBody @Validated(ValidationGroups.Update.class) Article article, BindingResult bindingResult, @ApiParam(required = true) @PathVariable int id) throws Exception {
        Article clear = new Article();
        return new ResponseEntity<Article>(communityService.updateArticle((Article)StringXssChecker.xssCheck(article, clear), id), HttpStatus.CREATED);
    }

    @ApiIgnore
    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/articles/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteArticle(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(communityService.deleteArticle(id), HttpStatus.OK);
    }

    @ApiIgnore
    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/articles/{articleId}/comments", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createComment(@ApiParam(value = "(required: content)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) Comment comment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "articleId") int articleId) throws Exception {
        Comment clear = new Comment();
        return new ResponseEntity<Comment>(communityService.createComment((Comment)StringXssChecker.xssCheck(comment,clear), articleId), HttpStatus.CREATED);
    }

    @ApiIgnore
    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/articles/{articleId}/comments/{commentId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getComment(@ApiParam(required = true) @PathVariable int articleId, @ApiParam(required = true) @PathVariable int commentId) throws Exception {

        return new ResponseEntity<Comment>(communityService.getComment(articleId, commentId), HttpStatus.OK);
    }

    @ApiIgnore
    @ApiOff
    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/articles/{articleId}/comments/{commentId}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateComment(@ApiParam(value = "(optional: content)", required = false) @RequestBody @Validated(ValidationGroups.Update.class) Comment comment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable int articleId, @ApiParam(required = true) @PathVariable int commentId) throws Exception {
        Comment clear = new Comment();
        return new ResponseEntity<Comment>(communityService.updateComment((Comment)StringXssChecker.xssCheck(comment,clear), articleId, commentId), HttpStatus.CREATED);
    }

    @ApiIgnore
    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/articles/{articleId}/comments/{commentId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteComment(@ApiParam(required = true) @PathVariable int articleId, @ApiParam(required = true) @PathVariable int commentId) throws Exception {

        return new ResponseEntity<Map<String, Object>>(communityService.deleteComment(articleId, commentId), HttpStatus.OK);
    }

    @ApiIgnore
    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/articles/grant/check", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity checkGrantEditArticle(@ApiParam(value = "(required: article_id)", required = true) @RequestBody Map<String, Integer> article_id) throws Exception {
        if (article_id == null || !article_id.containsKey("article_id"))
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 데이터입니다.", 0));

        return new ResponseEntity<Map<String, Boolean>>(communityService.checkGrantEditArticle(article_id.get("article_id")), HttpStatus.OK);
    }
}
