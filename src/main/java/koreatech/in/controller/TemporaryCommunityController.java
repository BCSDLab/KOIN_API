package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.ParamValid;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Criteria.Criteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.TemporaryCommunity.TempArticle;
import koreatech.in.domain.TemporaryCommunity.TempComment;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.service.TemporaryCommunityService;
import koreatech.in.util.StringXssChecker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springfox.documentation.annotations.ApiIgnore;

import javax.inject.Inject;
import java.util.Map;

@ApiOff @ApiIgnore @Deprecated
@Api(tags = "(Normal) Temporary Community", description = "익명 커뮤니티")
@Controller
public class TemporaryCommunityController {
    @Inject
    private TemporaryCommunityService tempCommunityService;

    @ApiOff
    @RequestMapping(value = "/temp/articles", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getArticles(@ModelAttribute("criteria") Criteria criteria) throws Exception {

        Map<String, Object> map = tempCommunityService.getArticles(criteria);

        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @RequestMapping(value = "/temp/articles", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createArticle(@ApiParam(value = "(required: title, nickname, password), (optional: content)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) TempArticle article, BindingResult bindingResult) throws Exception {
        //TODO:  board_id, title, content Notnull 체크
        TempArticle clear = new TempArticle();
        return new ResponseEntity<TempArticle>(tempCommunityService.createArticle((TempArticle)StringXssChecker.xssCheck(article, clear)), HttpStatus.CREATED);
    }

    @ApiOff
    @RequestMapping(value = "/temp/articles/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getArticle(@ApiParam(required = true) @PathVariable int id) throws Exception {
        Map<String, Object> article = tempCommunityService.getArticle(id);

        return new ResponseEntity<Map<String, Object>>(article, HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @RequestMapping(value = "/temp/articles/{id}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateArticle(@ApiParam(value = "(required: password), (optional: title, content)", required = true) @RequestBody @Validated(ValidationGroups.Update.class) TempArticle article, BindingResult bindingResult, @ApiParam(required = true) @PathVariable int id) throws Exception {
        TempArticle clear = new TempArticle();
        return new ResponseEntity<TempArticle>(tempCommunityService.updateArticle((TempArticle)StringXssChecker.xssCheck(article, clear), id), HttpStatus.CREATED);
    }

    //TODO: password가 한글로 입력되었을 경우 header에 실을때 에러
    @ApiOff
    @RequestMapping(value = "/temp/articles/{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteArticle(@ApiParam(required = true) @PathVariable int id,
                                 @ApiParam(required = true) @RequestHeader(value="password") String password) throws Exception {

        return new ResponseEntity<Map<String, Object>>(tempCommunityService.deleteArticle(id, password), HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @RequestMapping(value = "/temp/articles/{articleId}/comments", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity createComment(@ApiParam(value = "(required: content, nickname, password)", required = true) @RequestBody @Validated(ValidationGroups.Create.class) TempComment comment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable(value = "articleId") int articleId) throws Exception {
        TempComment clear = new TempComment();
        return new ResponseEntity<TempComment>(tempCommunityService.createComment((TempComment)StringXssChecker.xssCheck(comment,clear), articleId), HttpStatus.CREATED);
    }

    @ApiOff
    @RequestMapping(value = "/temp/articles/{articleId}/comments/{commentId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity getComment(@ApiParam(required = true) @PathVariable int articleId, @ApiParam(required = true) @PathVariable int commentId) throws Exception {

        return new ResponseEntity<Map<String, Object>>(tempCommunityService.getComment(articleId, commentId), HttpStatus.OK);
    }

    @ApiOff
    @ParamValid
    @RequestMapping(value = "/temp/articles/{articleId}/comments/{commentId}", method = RequestMethod.PUT)
    public @ResponseBody
    ResponseEntity updateComment(@ApiParam(value = "(required: password), (optional: content)", required = true) @RequestBody @Validated(ValidationGroups.Update.class) TempComment comment, BindingResult bindingResult, @ApiParam(required = true) @PathVariable int articleId, @ApiParam(required = true) @PathVariable int commentId) throws Exception {
        TempComment clear = new TempComment();
        return new ResponseEntity<TempComment>(tempCommunityService.updateComment((TempComment)StringXssChecker.xssCheck(comment,clear), articleId, commentId), HttpStatus.CREATED);
    }

    @ApiOff
    @RequestMapping(value = "/temp/articles/{articleId}/comments/{commentId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity deleteComment(@ApiParam(required = true) @PathVariable int articleId,
                                 @ApiParam(required = true) @PathVariable int commentId,
                                 @ApiParam(required = true) @RequestHeader(value="password") String password) throws Exception {

        return new ResponseEntity<Map<String, Object>>(tempCommunityService.deleteComment(articleId, commentId, password), HttpStatus.OK);
    }

    @ApiOff
    @RequestMapping(value = "/temp/articles/grant/check", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity checkGrantEditArticle(@ApiParam(value = "(required: article_id, password)", required = true) @RequestBody Map<String, Object> param) throws Exception {
        if (param == null || !param.containsKey("article_id") || !param.containsKey("password"))
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 데이터입니다.", 0));

        Integer article_id = Integer.parseInt(param.get("article_id").toString());
        String password = param.get("password").toString();

        return new ResponseEntity<Map<String, Boolean>>(tempCommunityService.checkGrantEditArticle(article_id, password), HttpStatus.OK);
    }

    @ApiOff
    @RequestMapping(value = "/temp/comments/grant/check", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity checkGrantEditComment(@ApiParam(value = "(required: comment_id, password)", required = true) @RequestBody Map<String, Object> param) throws Exception {
        if (param == null || !param.containsKey("comment_id") || !param.containsKey("password"))
            throw new PreconditionFailedException(new ErrorMessage("올바르지 않은 데이터입니다.", 0));

        Integer comment_id = Integer.parseInt(param.get("comment_id").toString());
        String password = param.get("password").toString();

        return new ResponseEntity<Map<String, Boolean>>(tempCommunityService.checkGrantEditComment(comment_id, password), HttpStatus.OK);
    }

    @ApiOff
    @ApiImplicitParams(
            @ApiImplicitParam(name = "mtfRequest", required = true, paramType = "form", dataType = "file")
    )
    @RequestMapping(value = "/temp/items/image/upload", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity itemImagesUpload(@ApiParam(required = true) MultipartHttpServletRequest mtfRequest) throws Exception {
        Map<String, MultipartFile> fileMap = mtfRequest.getFileMap();

        return new ResponseEntity<Map<String, Object>>(tempCommunityService.itemImagesUpload(fileMap), HttpStatus.CREATED);
    }

    @ApiOff
    @RequestMapping(value = "/temp/items/image/thumbnail/upload", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity itemThumbnailImageUpload(@ApiParam(required = true) MultipartFile image) throws Exception {

        return new ResponseEntity<Map<String, Object>>(tempCommunityService.itemThumbnailImageUpload(image), HttpStatus.CREATED);
    }
}