package koreatech.in.dto.admin.user.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateAuthorityRequest {
    @ApiModelProperty(notes = "회원 서비스 관리 권한 \n" +
                              "- null일 경우 false로 요청됨", example = "true")
    private Boolean grant_user = false;

    @ApiModelProperty(notes = "콜밴 서비스 관리 권한 \n" +
                              "- null일 경우 false로 요청됨", example = "false")
    private Boolean grant_callvan = false;

    @ApiModelProperty(notes = "복덕방 서비스 관리 권한 \n" +
                              "- null일 경우 false로 요청됨", example = "true")
    private Boolean grant_land = false;

    @ApiModelProperty(notes = "커뮤니티 서비스 관리 권한 \n" +
                              "- null일 경우 false로 요청됨", example = "false")
    private Boolean grant_community = false;

    @ApiModelProperty(notes = "상점 서비스 관리 권한 \n" +
                              "- null일 경우 false로 요청됨", example = "true")
    private Boolean grant_shop = false;

    @ApiModelProperty(notes = "버전 서비스 관리 권한 \n" +
                              "- null일 경우 false로 요청됨", example = "false")
    private Boolean grant_version = false;

    @ApiModelProperty(notes = "중고장터 서비스 관리 권한 \n" +
                              "- null일 경우 false로 요청됨", example = "true")
    private Boolean grant_market = false;

    @ApiModelProperty(notes = "동아리 서비스 관리 권한 \n" +
                              "- null일 경우 false로 요청됨", example = "false")
    private Boolean grant_circle = false;

    @ApiModelProperty(notes = "분실물 서비스 관리 권한 \n" +
                              "- null일 경우 false로 요청됨", example = "true")
    private Boolean grant_lost = false;

    @ApiModelProperty(notes = "설문조사 서비스 관리 권한 \n" +
                              "- null일 경우 false로 요청됨", example = "false")
    private Boolean grant_survey = false;

    @ApiModelProperty(notes = "bcsdlab 페이지 서비스 관리 권한 \n" +
                              "- null일 경우 false로 요청됨", example = "true")
    private Boolean grant_bcsdlab = false;

    @ApiModelProperty(notes = "이벤트 서비스 관리 권한 \n" +
                              "- null일 경우 false로 요청됨", example = "false")
    private Boolean grant_event = false;
}
