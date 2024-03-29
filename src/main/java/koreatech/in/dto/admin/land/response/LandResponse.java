package koreatech.in.dto.admin.land.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter @Builder
public class LandResponse {
    @ApiModelProperty(notes = "고유 id", example = "1", required = true)
    private Integer id;

    @ApiModelProperty(notes = "이름", example = "금실타운", required = true)
    private String name;

    @ApiModelProperty(notes = "삭제(soft delete) 여부", example = "false")
    private Boolean is_deleted;

    @ApiModelProperty(notes = "종류", example = "원룸")
    private String room_type;

    @ApiModelProperty(notes = "관리비", example = "21(1인 기준)")
    private String management_fee;

    @ApiModelProperty(notes = "크기", example = "9.0")
    private Double size;

    @ApiModelProperty(notes = "월세", example = "200만원 (6개월)")
    private String monthly_fee;

    @ApiModelProperty(notes = "전세", example = "3500")
    private String charter_fee;

    @ApiModelProperty(notes = "위도", example = "36.766205")
    private Double latitude;

    @ApiModelProperty(notes = "경도", example = "127.284638")
    private Double longitude;

    @ApiModelProperty(notes = "보증금", example = "30")
    private String deposit;

    @ApiModelProperty(notes = "층수", example = "4")
    private Integer floor;

    @ApiModelProperty(notes = "전화번호", example = "041-111-1111")
    private String phone;

    @ApiModelProperty(notes = "주소", example = "충청남도 천안시 동남구 병천면")
    private String address;

    @ApiModelProperty(notes = "설명", example = "1년 계약시 20만원 할인")
    private String description;

    @ApiModelProperty(notes = "냉장고 보유 여부", example = "true", required = true)
    private Boolean opt_refrigerator;

    @ApiModelProperty(notes = "옷장 보유 여부", example = "true", required = true)
    private Boolean opt_closet;

    @ApiModelProperty(notes = "tv 보유 여부", example = "true", required = true)
    private Boolean opt_tv;

    @ApiModelProperty(notes = "전자레인지 보유 여부", example = "true", required = true)
    private Boolean opt_microwave;

    @ApiModelProperty(notes = "가스레인지 보유 여부", example = "false", required = true)
    private Boolean opt_gas_range;

    @ApiModelProperty(notes = "인덕션 보유 여부", example = "true", required = true)
    private Boolean opt_induction;

    @ApiModelProperty(notes = "정수기 보유 여부", example = "true", required = true)
    private Boolean opt_water_purifier;

    @ApiModelProperty(notes = "에어컨 보유 여부", example = "true", required = true)
    private Boolean opt_air_conditioner;

    @ApiModelProperty(notes = "샤워기 보유 여부", example = "true", required = true)
    private Boolean opt_washer;

    @ApiModelProperty(notes = "침대 보유 여부", example = "false", required = true)
    private Boolean opt_bed;

    @ApiModelProperty(notes = "책상 보유 여부", example = "true", required = true)
    private Boolean opt_desk;

    @ApiModelProperty(notes = "신발장 보유 여부", example = "true", required = true)
    private Boolean opt_shoe_closet;

    @ApiModelProperty(notes = "전자 도어락 보유 여부", example = "true", required = true)
    private Boolean opt_electronic_door_locks;

    @ApiModelProperty(notes = "비데 보유 여부", example = "false", required = true)
    private Boolean opt_bidet;

    @ApiModelProperty(notes = "베란다 보유 여부", example = "false", required = true)
    private Boolean opt_veranda;

    @ApiModelProperty(notes = "엘레베이터 보유 여부", example = "true", required = true)
    private Boolean opt_elevator;

    @ApiModelProperty(notes = "이미지 url 리스트")
    private List<String> image_urls = new ArrayList<>();
}
