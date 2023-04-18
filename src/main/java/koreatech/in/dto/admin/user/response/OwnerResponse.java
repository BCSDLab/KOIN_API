package koreatech.in.dto.admin.user.response;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.domain.User.UserType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel("Admin_OwnerResponse")
public class OwnerResponse {

    //-------------------- User --------------------
    @ApiModelProperty(notes = "고유 id", example = "1")
    private Integer id;

    @ApiModelProperty(notes = "이메일", example = "string")
    private String email;

    @ApiModelProperty(notes = "닉네임" + "(50자 이내)", example = "bbo")
    private String nickname;

    @ApiModelProperty(notes = "이름" + "(50자 이내)", example = "정보혁", required = true)
    private String name;

    @ApiModelProperty(notes = "휴대폰 번호", example = "010-0000-0000")
    private String phoneNumber;

    @ApiModelProperty(example = "STUDENT", required = true)
    private UserType userType;

    @ApiModelProperty(notes = "성별(남:0, 여:1)", example = "1")
    private Integer gender;

    @ApiModelProperty(notes = "인증 여부", example = "ture", required = true)
    private Boolean isAuthed;

    @ApiModelProperty(notes = "최근 로그인 날짜", example = "1680585195000")
    private Date lastLoggedAt;

    @ApiModelProperty(notes = "프로필 이미지 s3 URL", example = "static.koreatech.in/example.png")
    private String profileImageUrl;

    @ApiModelProperty(notes = "이메일 인증 토큰", example = "string")
    private String authToken;

    @ApiModelProperty(notes = "이메일 인증 토큰 만료 시간", example = "1680585195000")
    private Date authExpiredAt;

    @ApiModelProperty(notes = "비밀번호 초기화 토큰", example = "string")
    private String resetToken;

    @ApiModelProperty(notes = "비밀번호 초기화 토큰 만료 시간", example = "1680585195000")
    private Date resetExpiredAt;

    @ApiModelProperty(notes = "탈퇴 여부", example = "true")
    private Boolean isDeleted;

    @ApiModelProperty(notes = "회원가입 일자(생성 일자)", example = "1680585195000")
    private Date createdAt;

    @ApiModelProperty(notes = "업데이트 일자", example = "1680585195000")
    private Date updatedAt;


    //-------------------- Owner --------------------

    @ApiModelProperty(notes = "사업자 등록 번호", example = "012-34-56789", required = true)
    private String companyRegistrationNumber;

/*  Owner 객체에는 없는 필드. Owner에도 추가해야 할 것 같음.
    @ApiModelProperty(notes = "사업자 등록 인증서 이미지 URL", example = "static.koreatech.in/example.png")
    private String companyRegistrationCertificateImageUrl;
*/

    @ApiModelProperty(notes = "가게 목록")
    @Valid
    private List<Shop> shops;

    @ApiModelProperty(notes = "첨부파일 목록")
    @Valid
    private List<Attachment> attachments;

    @Getter
    @Builder
    @ApiModel("Admin_Shop_in_OwnerResponse")
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Shop {
        @ApiModelProperty(notes = "고유 id", example = "2", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "가장 맛있는 족발")
        private String name;

        @ApiModelProperty(notes = "가게 이름을 소문자로 변경하고 띄어쓰기 제거", example = "가장맛있는족발")
        private String internalName;

        @ApiModelProperty(notes = "가게 이름 앞자리 1글자의 초성", example = "가")
        private String chosung;

        @ApiModelProperty(notes = "전화 번호", example = "010-0000-0000")
        private String phone;

        @ApiModelProperty(notes = "주소", example = "string")
        private String address;

        @ApiModelProperty(notes = "세부 사항", example = "string")
        private String description;

        @ApiModelProperty(notes = "배달 가능 여부", example = "true")
        private Boolean delivery;

        @ApiModelProperty(notes = "배달 금액", example = "3000")
        private Integer deliveryPrice;

        @ApiModelProperty(notes = "카드 가능 여부", example = "true")
        private Boolean payCard;

        @ApiModelProperty(notes = "계좌이체 가능 여부", example = "true")
        private Boolean payBank;

        @ApiModelProperty(notes = "삭제 여부", example = "true")
        private Boolean isDeleted;

        @ApiModelProperty(notes = "생성 일자", example = "1680585195000")
        private Date createdAt;

        @ApiModelProperty(notes = "업데이트 일자", example = "1680585195000")
        private Date updatedAt;

        @ApiModelProperty(notes = "이벤트 진행 여부", example = "true")
        private Boolean isEvent;

        @ApiModelProperty(notes = "이벤트 상세내용 등 부가내용", example = "string")
        private String remarks;

        @ApiModelProperty(notes = "조회수", example = "300")
        private Integer hit;
    }

    @Getter
    @Builder
    @ApiModel("Admin_Attachment_in_OwnerResponse")
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Attachment {
        @ApiModelProperty(notes = "고유 id", example = "2", required = true)
        private Integer id;

        @ApiModelProperty(notes = "업로드된 파일 url", example = "static.koreatech.in/example.png")
        private final String fileUrl;

        @ApiModelProperty(notes = "삭제되었는지 여부", example = "False")
        private Boolean isDeleted;

        @ApiModelProperty(notes = "업데이트 일자", example = "1680585195000")
        private Date updateAt;
    }
}
