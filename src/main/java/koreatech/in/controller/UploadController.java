package koreatech.in.controller;

import io.swagger.annotations.*;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.util.UploadFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Auth(role = Auth.Role.USER)
@Controller
public class UploadController {
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Inject
    private UploadFileUtils uploadFileUtils;

    // 단일 이미지 업로드
    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/upload/image", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity uploadImage(@ApiParam(required = true) MultipartFile image) throws Exception {
        String uploadpath = "upload";

        String img_path = uploadFileUtils.uploadFile(uploadpath, image.getOriginalFilename(), image.getBytes());
        String url = uploadFileUtils.getDomain() + "/" + uploadpath + img_path;

        return new ResponseEntity<String>(url, HttpStatus.OK);
    }

    // 다중 이미지 업로드
    @ApiOff
    @ApiImplicitParams(
            @ApiImplicitParam(name = "mtfRequest", required = true, paramType = "form", dataType = "file")
    )
    @ApiOperation(value = "", authorizations = {@Authorization(value="Authorization")})
    @RequestMapping(value = "/upload/images", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity uploadImages(@ApiParam(required = true) MultipartHttpServletRequest mtfRequest) throws Exception {
        List<MultipartFile> fileList = mtfRequest.getFiles("images");

        String uploadpath = "upload";

        List<String> urls = new ArrayList<>();

        for (MultipartFile mf : fileList) {
            String img_path = uploadFileUtils.uploadFile(uploadpath, mf.getOriginalFilename(), mf.getBytes());
            String url = uploadFileUtils.getDomain() + "/" + uploadpath + img_path;

            urls.add(url);
        }

        return new ResponseEntity<List<String>>(urls, HttpStatus.CREATED);
    }
}
