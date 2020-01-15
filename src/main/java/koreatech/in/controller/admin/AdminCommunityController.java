package koreatech.in.controller.admin;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Community.Article;
import koreatech.in.domain.Community.Board;
import koreatech.in.domain.Community.Comment;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.service.CommunityService;
import koreatech.in.util.StringXssChecker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

@Auth(role = Auth.Role.ADMIN, authority = Auth.Authority.COMMUNITY)
@Controller
public class AdminCommunityController {
    @Inject
    CommunityService communityService;

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/boards", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createBoard(@ApiParam(value = "(required: tag, name), (optional: is_anonymous, is_notice, seq, is_deleted, parent_id)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Board board, BindingResult bindingResult) throws Exception {

        return new ResponseEntity<Board>(communityService.createBoardForAdmin(board), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/boards", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getBoards(@ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<Map<String, Object>>(communityService.getBoardsForAdmin(criteria), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/boards/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getBoard(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<Board>(communityService.getBoardForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/boards/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateBoard(@ApiParam(value = "(optional: tag, name, is_anonymous, is_notice, seq, is_deleted, parent_id)", required = true) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Board board, BindingResult bindingResult, @ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<Board>(communityService.updateBoardForAdmin(board, id), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/boards/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteBoard(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(communityService.deleteBoardForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/articles", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createArticle(@ApiParam(value = "(required: board_id, title, content), (optional: is_solved, is_deleted)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Article article, BindingResult bindingResult) throws Exception {
        Article clear = new Article();
        return new ResponseEntity<Article>(communityService.createArticleForAdmin((Article) StringXssChecker.xssCheck(article, clear)), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/articles", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getArticles(@ApiParam(required = true) @RequestParam(value = "boardId") int boardId,
                               @ModelAttribute("criteria") Criteria criteria) throws Exception {

        return new ResponseEntity<Map<String, Object>>(communityService.getArticlesForAdmin(boardId, criteria), HttpStatus.OK);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/articles/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getArticle(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(communityService.getArticleForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/articles/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateArticle(@ApiParam(value = "(required: board_id), (optional: title, content, is_solved, is_deleted)", required = true) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Article article, BindingResult bindingResult, @ApiParam(required = true) @PathVariable int id) throws Exception {
        Article clear = new Article();
        return new ResponseEntity<Article>(communityService.updateArticleForAdmin((Article) StringXssChecker.xssCheck(article, clear), id), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/articles/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteArticle(@ApiParam(required = true) @PathVariable int id) throws Exception {

        return new ResponseEntity<Map<String, Object>>(communityService.deleteArticleForAdmin(id), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/articles/{articleId}/comments", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createComment(@ApiParam(value = "(required: content), (optional: is_deleted)", required = true) @RequestBody @Validated(ValidationGroups.CreateAdmin.class) Comment comment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "articleId") int articleId) throws Exception {
        Comment clear = new Comment();
        return new ResponseEntity<Comment>(communityService.createCommentForAdmin((Comment)StringXssChecker.xssCheck(comment,clear), articleId), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/articles/{articleId}/comments/{commentId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getComment(@ApiParam(required = true) @PathVariable int articleId, @ApiParam(required = true) @PathVariable int commentId) throws Exception {

        return new ResponseEntity<Comment>(communityService.getCommentForAdmin(articleId, commentId), HttpStatus.OK);
    }

    @ParamValid
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/articles/{articleId}/comments/{commentId}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateComment(@ApiParam(value = "(optional: content)", required = false) @RequestBody @Validated(ValidationGroups.UpdateAdmin.class) Comment comment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable int articleId, @ApiParam(required = true) @PathVariable int commentId) throws Exception {
        Comment clear = new Comment();
        return new ResponseEntity<Comment>(communityService.updateCommentForAdmin((Comment)StringXssChecker.xssCheck(comment,clear), articleId, commentId), HttpStatus.CREATED);
    }

    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/admin/articles/{articleId}/comments/{commentId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteComment(@ApiParam(required = true) @PathVariable int articleId, @ApiParam(required = true) @PathVariable int commentId) throws Exception {

        return new ResponseEntity<Map<String, Object>>(communityService.deleteCommentForAdmin(articleId, commentId), HttpStatus.OK);
    }
}
