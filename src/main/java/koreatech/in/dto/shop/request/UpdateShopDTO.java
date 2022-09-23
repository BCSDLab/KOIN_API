package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
public class UpdateShopDTO {
    @ApiModelProperty(notes = "상점 고유 id", hidden = true)
    private Integer id;

    @NotNull(message = "상점명은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "상점명", example = "써니 숯불 도시락")
    private String name;

    @ApiModelProperty(notes = "전화번호")
    private String phone;

    @NotNull(message = "요일별 여는시간 & 휴무 여부는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "요일별 여는 시간, 휴무 여부")
    private List<Map<String, Object>> open;

    @ApiModelProperty(notes = "주소")
    private String address;

    @ApiModelProperty(notes = "배달 금액")
    private Integer delivery_price = 0;

    @ApiModelProperty(notes = "기타 정보")
    private String description;

    @NotNull(message = "배달 여부는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "배달 여부")
    private Boolean delivery;

    @NotNull(message = "배달 여부는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "카드 가능 여부")
    private Boolean pay_card;

    @NotNull(message = "배달 여부는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "계좌 이체 가능 여부")
    private Boolean pay_bank;

    @ApiModelProperty(notes = "상점 카테고리 고유 id 리스트")
    private List<Integer> category_ids;
    private List<MultipartFile> images;

    public UpdateShopDTO init(Integer id, List<MultipartFile> images) {
        this.id = id;
        this.images = images;
        return this;
    }
}
