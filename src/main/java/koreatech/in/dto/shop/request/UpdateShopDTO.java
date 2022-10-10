package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class UpdateShopDTO {
    @ApiModelProperty(notes = "상점 고유 id", hidden = true)
    private Integer id;

    @Size(min = 1, max = 15, message = "상점명은 1자 이상 15자 이하입니다.")
    @NotNull(message = "상점명은 필수입니다.")
    @ApiModelProperty(notes = "상점명", example = "써니 숯불 도시락")
    private String name;

    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @NotNull(message = "전화번호는 필수입니다.")
    @ApiModelProperty(notes = "전화번호")
    private String phone;

    @NotNull(message = "요일별 장사 시간 & 휴무 여부는 필수입니다.")
    @ApiModelProperty(notes = "요일별 장사 시간과 휴무 여부")
    private List<Map<String, Object>> open;

    @Size(min = 1, max = 50, message = "주소는 1자 이상 50자 이하입니다.")
    @NotNull(message = "주소는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "주소", example = "한국 어딘가")
    private String address;

    @Max(value = Integer.MAX_VALUE, message = "입력할 수 없는 가격입니다.")
    @Min(value = 0, message = "입력할 수 없는 가격입니다.")
    @ApiModelProperty(notes = "배달 금액", example = "3000")
    private Integer delivery_price = 0;

    @Size(min = 1, max = 50, message = "기타정보는 1자 이상 50자 이하입니다.")
    @ApiModelProperty(notes = "기타정보")
    private String description;

    @NotNull(message = "배달 가능 여부는 필수입니다.")
    @ApiModelProperty(notes = "배달 가능 여부", example = "false")
    private Boolean delivery;

    @NotNull(message = "카드 가능 여부는 필수입니다.")
    @ApiModelProperty(notes = "카드 가능 여부", example = "true")
    private Boolean pay_card;

    @NotNull(message = "계좌 이체 가능 여부는 필수입니다.")
    @ApiModelProperty(notes = "계좌 이체 가능 여부", example = "true")
    private Boolean pay_bank;

    @Size(min = 1, message = "상점 카테고리 고유 id는 최소 1개 선택되어야 합니다.")
    @NotNull(message = "상점 카테고리 고유 id 리스트는 필수입니다.")
    @ApiModelProperty(notes = "상점 카테고리 고유 id 리스트", example = "[1, 2]")
    private List<Integer> category_ids;

    @ApiModelProperty(notes = "상점 이미지 리스트", hidden = true)
    private List<MultipartFile> images;

    public UpdateShopDTO init(Integer id, List<MultipartFile> images) {
        this.id = id;
        this.images = images;
        return this;
    }
}
