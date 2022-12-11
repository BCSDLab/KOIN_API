package koreatech.in.dto.land.admin.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class UpdateLandRequest {
    @NotNull(message = "name은 필수입니다.")
    @Size(max = 255, message = "name의 최대 길이는 80자 입니다.")
    @ApiModelProperty(notes = "이름 \n" +
            "- NOT NULL \n" +
            "- 최대 255자", example = "금실타운", required = true)
    private String name;

    @ApiModelProperty(notes = "크기", example = "9.0")
    private Double size;

    @Size(max = 20, message = "room_type의 최대 길이는 20자 입니다.")
    @ApiModelProperty(notes = "종류 \n" +
            "- 최대 20자", example = "원룸")
    private String room_type;

    @ApiModelProperty(notes = "위도", example = "36.766205")
    private Double latitude;

    @ApiModelProperty(notes = "경도", example = "127.284638")
    private Double longitude;

    @Size(max = 20, message = "phone의 최대 길이는 20자 입니다.")
    @ApiModelProperty(notes = "전화번호 \n" +
            "- 최대 20자", example = "041-111-1111")
    private String phone;

    @ApiModelProperty(notes = "이미지 url 리스트")
    private List<String> image_urls = new LinkedList<>();

    @Size(max = 65535, message = "address의 최대 길이는 65535자 입니다.")
    @ApiModelProperty(notes = "주소 \n" +
            "- 최대 65535자", example = "충청남도 천안시 동남구 병천면")
    private String address;

    @Size(max = 65535, message = "description의 최대 길이는 65535자 입니다.")
    @ApiModelProperty(notes = "설명 \n" +
            "- 최대 65535자", example = "1년 계약시 20만원 할인")
    private String description;

    @ApiModelProperty(notes = "층수", example = "4")
    private Integer floor;

    @Size(max = 255, message = "deposit의 최대 길이는 255자 입니다.")
    @ApiModelProperty(notes = "보증금 \n" +
            "- 최대 255자", example = "30")
    private String deposit;

    @Size(max = 255, message = "monthly_fee의 최대 길이는 255자 입니다.")
    @ApiModelProperty(notes = "월세 \n" +
            "- 최대 255자", example = "200만원 (6개월)")
    private String monthly_fee;

    @Size(max = 20, message = "charter_fee의 최대 길이는 20자 입니다.")
    @ApiModelProperty(notes = "전세 \n" +
            "- 최대 20자", example = "3500")
    private String charter_fee;

    @Size(max = 255, message = "management_fee의 최대 길이는 255자 입니다.")
    @ApiModelProperty(notes = "관리비 \n" +
            "- 최대 255자", example = "21(1인 기준)")
    private String management_fee;

    @ApiModelProperty(notes = "냉장고 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "true")
    private Boolean opt_refrigerator = false;

    @ApiModelProperty(notes = "옷장 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "true")
    private Boolean opt_closet = false;

    @ApiModelProperty(notes = "tv 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "true")
    private Boolean opt_tv = false;

    @ApiModelProperty(notes = "전자레인지 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "true")
    private Boolean opt_microwave = false;

    @ApiModelProperty(notes = "가스레인지 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "false")
    private Boolean opt_gas_range = false;

    @ApiModelProperty(notes = "인덕션 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "true")
    private Boolean opt_induction = false;

    @ApiModelProperty(notes = "정수기 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "true")
    private Boolean opt_water_purifier = false;

    @ApiModelProperty(notes = "에어컨 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "true")
    private Boolean opt_air_conditioner = false;

    @ApiModelProperty(notes = "샤워기 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "true")
    private Boolean opt_washer = false;

    @ApiModelProperty(notes = "침대 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "false")
    private Boolean opt_bed = false;

    @ApiModelProperty(notes = "책상 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "true")
    private Boolean opt_desk = false;

    @ApiModelProperty(notes = "신발장 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "true")
    private Boolean opt_shoe_closet = false;

    @ApiModelProperty(notes = "전자 도어락 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "true")
    private Boolean opt_electronic_door_locks = false;

    @ApiModelProperty(notes = "비데 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "false")
    private Boolean opt_bidet = false;

    @ApiModelProperty(notes = "베란다 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "false")
    private Boolean opt_veranda = false;

    @ApiModelProperty(notes = "엘레베이터 보유 여부 \n" +
            "- NULL일경우 false로 요청됨", example = "true")
    private Boolean opt_elevator = false;
}
