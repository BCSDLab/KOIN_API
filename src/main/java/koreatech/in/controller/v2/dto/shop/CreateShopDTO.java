package koreatech.in.controller.v2.dto.shop;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.Shop.Shop;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateShopDTO {

    @Size(max = 50, message = "가게 이름은 50자 이내여야 합니다.")
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "가게 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "가게 이름", example = "써니 숯불 도시락")
    private String name;

    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "가게 카테고리는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "기타(S000), 콜벤(S001), 정식(S002), 족발(S003), 중국집(S004), 치킨(S005), 피자(S006), 탕수육(S007), 일반(S008), 미용실(S009)", example = "S001")
    private String category;

    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "전화번호", example = "010-0000-0000")
    private String phone;

    @Pattern(regexp = "^[0-9]{2}:[0-9]{2}", message = "시간 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "오픈 시간", example = "11:00")
    private String openTime;

    @Pattern(regexp = "^[0-9]{2}:[0-9]{2}", message = "시간 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "마감 시간", example = "23:00")
    private String closeTime;

    @Pattern(regexp = "^[0-9]{2}:[0-9]{2}", message = "시간 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "주말 오픈 시간", example = "11:00")
    private String weekendOpenTime;

    @Pattern(regexp = "^[0-9]{2}:[0-9]{2}", message = "시간 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "주말 마감 시간", example = "23:00")
    private String weekendCloseTime;

    @ApiModelProperty(notes = "이미지 링크, string으로 받아서 저장 후 json 으로 반환, key와 value는 큰따옴표로 감싸야 함", example = "[ A : B ]")
    private String imageUrls;

    @ApiModelProperty(notes = "주소", example = "한국 어딘가")
    private String address;

    @ApiModelProperty(notes = "세부사항", example = "세부사항입니다.")
    private String description;

    @ApiModelProperty(notes = "배달 가능 여부", example = "false")
    private Boolean delivery;

    @Max(value = Integer.MAX_VALUE, message = "입력할 수 없는 가격입니다.")
    @Min(value = 0, message = "입력할 수 없는 가격입니다.")
    @ApiModelProperty(notes = "배달 금액", example = "20000")
    private Integer deliveryPrice = 0;

    @ApiModelProperty(notes = "카드 가능 여부", example = "false")
    private Boolean payCard;

    @ApiModelProperty(notes = "계좌이체 가능 여부", example = "false")
    private Boolean payBank;

    @ApiModelProperty(notes = "이벤트 진행 여부", example = "false")
    private Boolean isEvent;

    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;

    @ApiModelProperty(notes = "이벤트 상세내용 등 부가내용", example = "비고.")
    private String remarks;


    public Shop toEntity(){
        return Shop.builder()
                .name(name)
                .address(address)
                .category(category)
                .open_time(openTime)
                .close_time(closeTime)
                .weekend_open_time(weekendOpenTime)
                .weekend_close_time(weekendCloseTime)
                .delivery(delivery)
                .delivery_price(deliveryPrice)
                .description(description)
                .image_urls(imageUrls)
                .is_event(isEvent)
                .pay_bank(payBank)
                .pay_card(payCard)
                .phone(phone)
                .remarks(remarks)
                .is_deleted(is_deleted)
                .build();
    }
}
