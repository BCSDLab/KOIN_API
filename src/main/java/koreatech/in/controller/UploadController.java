package koreatech.in.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import java.util.ArrayList;
import java.util.List;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.domain.Upload.DomainEnum;
import koreatech.in.dto.ExceptionResponse;
import koreatech.in.dto.normal.upload.request.UploadFileRequest;
import koreatech.in.dto.normal.upload.request.UploadFilesRequest;
import koreatech.in.dto.normal.upload.response.UploadFileResponse;
import koreatech.in.dto.normal.upload.response.UploadFilesResponse;
import koreatech.in.service.UploadService;
import koreatech.in.util.UploadFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private final static String UPLOAD_DIRECTORY_NAME = "upload";
    private final static String SLASH = "/";
    private final static String ADMIN_PATH = "/admin";

    @Autowired
    private UploadFileUtils uploadFileUtils;
    @Autowired
    private UploadService s3uploadService;

    // 단일 이미지 업로드
    @Deprecated
    @ApiOperation(value = "", authorizations = {@Authorization("Authorization")})
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
    @ApiOperation(value = "", authorizations = {@Authorization("Authorization")})
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


    // 단일 파일 업로드
    @ApiOperation(value = "", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "존재하지 않는 도메인일 때 \n"
                    + "(error code: 110000)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "유효하지 않은 파일일 때 \n"
                    + "(error code: 110001)", response = ExceptionResponse.class),
            @ApiResponse(code = 413, message = "도메인의 허용가능한 크기보다 파일의 크기가 클 때 \n"
                    + "(error code: 110004)", response = ExceptionResponse.class),
            @ApiResponse(code = 415, message = "도메인이 허용하는 콘텐츠 타입이 아닐 때 \n"
                    + "(error code: 110005)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/{domain}/upload/file", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<UploadFileResponse> upload(
            @ApiParam(value = "단일 파일", required = true) MultipartFile multipartFile,
            @ApiParam(value = "도메인 이름 \n\n"
                    + " (ContentType, MaxSize가 설정되지 않은 경우는 기본값[ContentType: `*/*`, MaxSize: `10mb`]으로 제한함.\n"
                    + "- `items`\n"
                    + "- `lands`\n"
                    + "- `circles`\n"
                    + "- `market`\n"
                    + "- `shops`\n"
                    + "- `members`\n"
                    + "- `owners`\n"
                    + "  - ContentType: `image/*`\n"
                    + "  - MaxSize: `10mb`\n"
                    , example = "items", required = true) @PathVariable String domain) {

        DomainEnum domainEnum = DomainEnum.mappingFor(domain);
        domainEnum.validateFor(multipartFile);

        UploadFileRequest uploadFileRequest = UploadFileRequest.of(enrichDomainPath(domain), multipartFile);

        UploadFileResponse uploadFileResponse = s3uploadService.uploadAndGetUrl(uploadFileRequest);

        return new ResponseEntity<>(uploadFileResponse, HttpStatus.CREATED);
    }

    // 다중 파일 업로드
    @ApiImplicitParams(
            @ApiImplicitParam(name = "files", required = true, paramType = "form",
                    dataType = "file",
                    value = "multipart/form-data 형식의 파일 리스트 (key name = `files`)")
    )
    @ApiOperation(value = "", notes = "**Swagger에서 파일 다중 선택이 불가능함.** \n" + "- 파일 개수는 최대 10개", authorizations = {
            @Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "존재하지 않는 도메인일 때 \n"
                    + "(error code: 110000)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 유효하지 않은 파일일 때"
                    + "(error code: 110001) \n" + "- 파일목록이 비어있을 때 \n"
                    + "(error code: 110002)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "파일들의 개수가 최대 개수를 초과하였을 때 \n"
                    + "(error code: 110003)", response = ExceptionResponse.class),
            @ApiResponse(code = 413, message = "도메인의 허용가능한 크기보다 파일의 크기가 클 때 \n"
                    + "(error code: 110004)", response = ExceptionResponse.class),
            @ApiResponse(code = 415, message = "도메인이 허용하는 콘텐츠 타입이 아닐 때 \n"
                    + "(error code: 110005)", response = ExceptionResponse.class),
    })
    @RequestMapping(value = "/{domain}/upload/files", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<UploadFilesResponse> uploadFiles(
            @ApiParam(required = true) @RequestPart List<MultipartFile> files,
            @ApiParam(value = "도메인 이름 \n\n"
                    + " (ContentType, MaxSize가 설정되지 않은 경우는 기본값[ContentType: `*/*`, MaxSize: `10mb`]으로 제한함.\n"
                    + "- `items`\n"
                    + "- `lands`\n"
                    + "- `circles`\n"
                    + "- `market`\n"
                    + "- `shops`\n"
                    + "- `members`\n"
                    + "- `owners`\n"
                    + "  - ContentType: `image/*`\n"
                    + "  - MaxSize: `10mb`\n"
                    , required = true) @PathVariable String domain) {

        DomainEnum domainEnum = DomainEnum.mappingFor(domain);
        files.forEach(domainEnum::validateFor);

        UploadFilesRequest uploadFilesRequest = UploadFilesRequest.of(files, enrichDomainPath(domain));

        UploadFilesResponse uploadFilesResponse = s3uploadService.uploadAndGetUrls(uploadFilesRequest);

        return new ResponseEntity<>(uploadFilesResponse, HttpStatus.CREATED);
    }

    private static String enrichDomainPath(String domain) {
        return UPLOAD_DIRECTORY_NAME + SLASH + domain.toLowerCase();
    }

    private static String enrichDomainPathForAdmin(String domain) {
        return UPLOAD_DIRECTORY_NAME + SLASH + domain.toLowerCase() + ADMIN_PATH;
    }

    // 업로드 전용 단일 파일 업로드
    // 어드민 전용 설정을 원한다면, Auth를 메서드별로 설정 & 인터셉터에서 인식 가능케 코드 변경 & Upload에 대한 Authority DB, enum, 인터셉터에 추가 해야 함.
    @Deprecated
    @ApiOff
    @ApiOperation(value = "", authorizations = {@Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "존재하지 않는 도메인일 때 \n"
                    + "(error code: 110000)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "- 유효하지 않은 파일일 때"
                    + "(error code: 110002)", response = ExceptionResponse.class),
            @ApiResponse(code = 413, message = "도메인의 허용가능한 크기보다 파일의 크기가 클 때 \n"
                    + "(error code: 110004)", response = ExceptionResponse.class),
            @ApiResponse(code = 415, message = "도메인이 허용하는 콘텐츠 타입이 아닐 때 \n"
                    + "(error code: 110005)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/{domain}/upload/file", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<UploadFileResponse> uploadFileAdminForAdmin(
            @ApiParam(value = "단일 파일", required = true) MultipartFile multipartFile,
            @ApiParam(value = "도메인 이름 \n\n"
                    + " (ContentType, MaxSize가 설정되지 않은 경우는 기본값[ContentType: `*/*`, MaxSize: `10mb`]으로 제한함.\n"
                    + "- `items`\n"
                    + "- `lands`\n"
                    + "- `circles`\n"
                    + "- `market`\n"
                    + "- `shops`\n"
                    + "- `members`\n"
                    + "- `owners`\n"
                    + "  - ContentType: `image/*`\n"
                    + "  - MaxSize: `10mb`\n"
                    , required = true) @PathVariable String domain)
            throws Exception {

        DomainEnum domainEnum = DomainEnum.mappingFor(domain);
        domainEnum.validateFor(multipartFile);

        String fileUrl = uploadFileUtils.uploadFile(enrichDomainPathForAdmin(domain),
                multipartFile.getOriginalFilename(),
                multipartFile.getBytes());

        UploadFileResponse uploadFileResponse = new UploadFileResponse(fileUrl);

        //CREATED 가 낫지 않을까?
        return new ResponseEntity<>(uploadFileResponse, HttpStatus.CREATED);
    }

    // 다중 파일 업로드
    @Deprecated
    @ApiOff
    @ApiImplicitParams(
            @ApiImplicitParam(name = "files", required = true, paramType = "form",
                    dataType = "file",
                    value = "multipart/form-data 형식의 파일 리스트 (key name = `files`)")
    )
    @ApiOperation(value = "", notes = "**Swagger에서 파일 다중 선택이 불가능함.** \n" + "- 파일 개수는 최대 10개", authorizations = {
            @Authorization("Authorization")})
    @ApiResponses({
            @ApiResponse(code = 404, message = "존재하지 않는 도메인일 때 \n"
                    + "(error code: 110000)", response = ExceptionResponse.class),
            @ApiResponse(code = 422, message = "유효하지 않은 파일일 때 (error code: 110001)\n"
                    + "파일목록이 비어있을 때(error code: 110002)", response = ExceptionResponse.class),
            @ApiResponse(code = 409, message = "파일들의 개수가 최대 개수를 초과하였을 때 \n"
                    + "(error code: 110003)", response = ExceptionResponse.class),
            @ApiResponse(code = 413, message = "도메인의 허용가능한 크기보다 파일의 크기가 클 때 \n"
                    + "(error code: 110004)", response = ExceptionResponse.class),
            @ApiResponse(code = 415, message = "도메인이 허용하는 콘텐츠 타입이 아닐 때 \n"
                    + "(error code: 110005)", response = ExceptionResponse.class)
    })
    @RequestMapping(value = "/admin/{domain}/upload/files", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    ResponseEntity<UploadFilesResponse> uploadFilesForAdmin(
            @ApiParam(required = true) @RequestPart
            List<MultipartFile> files,
            @ApiParam(value = "도메인 이름 \n\n"
                    + " (ContentType, MaxSize가 설정되지 않은 경우는 기본값[ContentType: `*/*`, MaxSize: `10mb`]으로 제한함.\n"
                    + "- `items`\n"
                    + "- `lands`\n"
                    + "- `circles`\n"
                    + "- `market`\n"
                    + "- `shops`\n"
                    + "- `members`\n"
                    + "- `owners`\n"
                    + "  - ContentType: `image/*`\n"
                    + "  - MaxSize: `10mb`\n"
                    , required = true) @PathVariable String domain)
            throws Exception {

        DomainEnum domainEnum = DomainEnum.mappingFor(domain);
        files.forEach(domainEnum::validateFor);

        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile multipartFile : files) {
            String fileUrl = uploadFileUtils.uploadFile(enrichDomainPathForAdmin(domain),
                    multipartFile.getOriginalFilename(),
                    multipartFile.getBytes());

            fileUrls.add(fileUrl);
        }

        return new ResponseEntity<>(new UploadFilesResponse(fileUrls), HttpStatus.CREATED);
    }
}
