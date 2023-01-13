package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.domain.Upload.DomainEnum;
import koreatech.in.dto.upload.request.UploadFileRequest;
import koreatech.in.dto.upload.request.UploadFilesRequest;
import koreatech.in.dto.upload.response.UploadFileResponse;
import koreatech.in.dto.upload.response.UploadFilesResponse;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import koreatech.in.service.UploadService;
import koreatech.in.util.UploadFileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Api(tags = "(Normal) Upload", description = "업로드")
@Auth(role = Auth.Role.USER)
@Controller
public class UploadController {
    public static final String KEY_NAME = "file";
    private final static String UPLOAD_DIRECTORY_NAME = "upload";
    private final static String SLASH = "/";
    private final static String ADMIN_PATH = "/admin";

    @Inject
    private UploadFileUtils uploadFileUtils;
    @Inject
    private UploadService s3uploadService;

    // 단일 이미지 업로드
    @Deprecated
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/upload/image", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity uploadImage(@ApiParam(required = true) MultipartFile image) throws Exception {
        String uploadpath = "upload";

        String img_path = uploadFileUtils.uploadFile(uploadpath, image.getOriginalFilename(), image.getBytes());
        String url = uploadFileUtils.getDomain() + "/" + uploadpath + img_path;

        return new ResponseEntity<String>(url, HttpStatus.OK);
    }

    // 다중 이미지 업로드
    @Deprecated
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


    // 단일 이미지 업로드
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{domain}/upload/file", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<UploadFileResponse> upload(@ApiParam(required = true) MultipartFile multipartFile,
                                                  @PathVariable String domain) {
        DomainEnum.validate(domain);

        UploadFileRequest uploadFileRequest = UploadFileRequest.of(enrichDomainPath(domain), multipartFile.getOriginalFilename(),
                dataFor(multipartFile));

        UploadFileResponse uploadFileResponse = s3uploadService.uploadFile(uploadFileRequest);

        return new ResponseEntity<>(uploadFileResponse, HttpStatus.CREATED);
    }

    // 다중 이미지 업로드
    @ApiImplicitParams(
            @ApiImplicitParam(name = "multipleFile", required = true, paramType = "form", dataType = "file")
    )
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/{domain}/upload/files", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<UploadFilesResponse> uploadFiles(@ApiParam(required = true) MultipartHttpServletRequest multipartFiles,
                                                    @PathVariable String domain){

        DomainEnum.validate(domain);
        String enrichedDomain = enrichDomainPath(domain);

        UploadFilesRequest uploadFilesRequest = UploadFilesRequest.from(new ArrayList<>());
        fillUploadFilesRequest(multipartFiles, enrichedDomain, uploadFilesRequest);

        UploadFilesResponse uploadFilesResponse = s3uploadService.uploadFiles(uploadFilesRequest);

        return new ResponseEntity<>(uploadFilesResponse, HttpStatus.CREATED);
    }

    private static void fillUploadFilesRequest(MultipartHttpServletRequest multipartHttpServletRequest, String enrichedDomain,
                                  UploadFilesRequest uploadFilesRequest) {
        for (MultipartFile multipartFile : multipartHttpServletRequest.getFiles(KEY_NAME)) {

            UploadFileRequest uploadFileRequest = UploadFileRequest.of(enrichedDomain, multipartFile.getOriginalFilename(),
                    dataFor(multipartFile));

            uploadFilesRequest.append(uploadFileRequest);
        }
    }


    // 업로드 전용 단일 파일 업로드
    // 어드민 전용 설정을 원한다면, Auth를 메서드별로 설정 & 인터셉터에서 인식 가능케 코드 변경 & Upload에 대한 Authority DB, enum, 인터셉터에 추가 해야 함.
    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/{domain}/upload/file", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<UploadFileResponse> uploadFileAdminForAdmin(@ApiParam(required = true) MultipartFile multipartFile,
                                                               @PathVariable String domain) throws Exception {
        DomainEnum.validate(domain);

        String fileUrl = uploadFileUtils.uploadFile(enrichDomainPathForAdmin(domain),
                multipartFile.getOriginalFilename(),
                multipartFile.getBytes());

        UploadFileResponse uploadFileResponse = UploadFileResponse.from(fileUrl);

        //CREATED 가 낫지 않을까?
        return new ResponseEntity<>(uploadFileResponse, HttpStatus.CREATED);
    }
    private static String enrichDomainPath(String domain) {
        return UPLOAD_DIRECTORY_NAME + SLASH + domain.toLowerCase();
    }

    private static String enrichDomainPathForAdmin(String domain) {
        return UPLOAD_DIRECTORY_NAME + SLASH + domain.toLowerCase() + ADMIN_PATH;
    }

    private static byte[] dataFor(MultipartFile multipartFile) {
        if(multipartFile == null || multipartFile.isEmpty()) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }
        try {
            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new BaseException(ExceptionInformation.REQUEST_DATA_INVALID);
        }
    }
    // 다중 이미지 업로드
    @ApiOff
    @ApiImplicitParams(
            @ApiImplicitParam(name = "filesRequest", required = true, paramType = "form", dataType = "file")
    )
    @ApiOperation(value = "", authorizations = {@Authorization(value = "Authorization")})
    @RequestMapping(value = "/admin/{domain}/upload/files", method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<UploadFilesResponse> uploadFilesForAdmin(
            @ApiParam(required = true) @RequestPart("file") MultipartFile[] multipartFiles, @PathVariable String domain)
            throws Exception {

        DomainEnum.validate(domain);

        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String fileUrl = uploadFileUtils.uploadFile(enrichDomainPathForAdmin(domain),
                    multipartFile.getOriginalFilename(),
                    multipartFile.getBytes());

            fileUrls.add(fileUrl);
        }

        return new ResponseEntity<>(UploadFilesResponse.from(fileUrls), HttpStatus.CREATED);
    }
}
