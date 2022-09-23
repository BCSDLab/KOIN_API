package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.*;
import java.util.List;

@Getter
public class CreateShopDTO {
    @NotNull(message = "사장님 id는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "사장님 id", example = "1")
    private Integer owner_id;

    @NotNull(message = "상점명은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "상점명", example = "써니 숯불 도시락")
    private String name;

    @NotNull
    @ApiModelProperty(notes = "상점 카테고리 고유 id 리스트", example = "[1, 2]")
    private List<Integer> category_ids;

    /*@Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @NotNull(message = "전화번호는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "전화번호", example = "")
    private String phone;

    // TODO: 최대 길이 기획나오면 적용하기
    @NotNull(message = "주소는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "주소", example = "한국 어딘가")
    private String address;

    @NotNull(message = "배달 가능 여부는 필수입니다.")
    @ApiModelProperty(notes = "배달 가능 여부", example = "false")
    private Boolean delivery;

    @Max(value = Integer.MAX_VALUE, message = "입력할 수 없는 가격입니다.")
    @Min(value = 0, message = "입력할 수 없는 가격입니다.")
    @ApiModelProperty(notes = "배달 금액", example = "20000")
    private Integer delivery_price = 0;

    @NotNull(message = "카드 가능 여부는 필수입니다.")
    @ApiModelProperty(notes = "카드 가능 여부", example = "true")
    private Boolean pay_card;

    @NotNull(message = "계좌 이체 가능 여부는 필수입니다.")
    @ApiModelProperty(notes = "계좌 이체 가능 여부", example = "true")
    private Boolean pay_bank;

    // TODO: 최대 길이 기획 나오면 적용하기
    @ApiModelProperty(notes = "기타정보", example = "기타정보입니다.")
    private String description;

    @NotNull(message = "상점은 최소 하나의 카테고리에 포함되어 있어야 합니다.")
    @ApiModelProperty(notes = "상점 카테고리 고유 id 리스트", example = "[1, 2]")
    private List<Integer> category_ids;

    @NotNull(message = "요일별 장사 시간과 휴무 여부는 필수입니다.")
    @ApiModelProperty(notes = "요일별 장사 시간과 휴무 여부")
    private List<Map<String, Object>> open;

    @ApiModelProperty(notes = "상점 이미지 리스트", hidden = true)
    private List<MultipartFile> images;
    
    public CreateShopDTO init(List<MultipartFile> images) {
        this.images = images;
        return this;
    }*/
}
