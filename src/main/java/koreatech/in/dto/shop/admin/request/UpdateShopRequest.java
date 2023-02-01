package koreatech.in.dto.shop.admin.request;

import io.swagger.annotations.ApiModelProperty;
import koreatech.in.exception.BaseException;
import koreatech.in.exception.ExceptionInformation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter @Setter
public class UpdateShopRequest {
    @Size(min = 1, max = 15, message = "name은 1자 이상 15자 이하입니다.")
    @NotNull(message = "name은 필수입니다.")
    @ApiModelProperty(notes = "상점명", example = "써니 숯불 도시락")
    private String name;

    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "phone 형식이 올바르지 않습니다.")
    @NotNull(message = "phone은 필수입니다.")
    @ApiModelProperty(notes = "전화번호", example = "041-123-4567")
    private String phone;

    @Valid
    @NotNull(message = "open은 필수입니다.")
    @ApiModelProperty(notes = "요일별 장사 시간과 휴무 여부")
    private List<Open> open = new ArrayList<>();

    @Size(min = 1, max = 50, message = "address는 1자 이상 50자 이하입니다.")
    @NotNull(message = "address는 필수입니다.")
    @ApiModelProperty(notes = "주소", example = "충청남도 천안시 동남구 병천면 충절로 1600")
    private String address;

    @PositiveOrZero(message = "delivery_price는 0 이상 2147483647 이하입니다.")
    @Max(value = Integer.MAX_VALUE, message = "delivery_price는 0 이상 2147483647 이하입니다.")
    @ApiModelProperty(notes = "배달 금액", example = "3000")
    private Integer delivery_price = 0;

    @Size(min = 1, max = 50, message = "description은 1자 이상 50자 이하입니다.")
    @ApiModelProperty(notes = "기타정보", example = "이번주 전 메뉴 20% 할인 이벤트합니다.")
    private String description;

    @NotNull(message = "delivery는 필수입니다.")
    @ApiModelProperty(notes = "배달 가능 여부", example = "false")
    private Boolean delivery;

    @NotNull(message = "pay_card는 필수입니다.")
    @ApiModelProperty(notes = "카드 가능 여부", example = "true")
    private Boolean pay_card;

    @NotNull(message = "pay_bank는 필수입니다.")
    @ApiModelProperty(notes = "계좌 이체 가능 여부", example = "true")
    private Boolean pay_bank;

    @Size(min = 1, message = "category_ids의 길이는 1 이상입니다.")
    @NotNull(message = "category_ids는 필수입니다.")
    @ApiModelProperty(notes = "상점 카테고리 고유 id 리스트", example = "[1, 4]")
    private List<Integer> category_ids = new ArrayList<>();

    @Size(max = 10, message = "image_urls의 size는 최대 10입니다.")
    @ApiModelProperty(notes = "이미지 URL 리스트")
    private List<String> image_urls = new ArrayList<>();

    @Getter @Setter
    public static class Open {
        @NotNull(message = "open의 day_of_week는 필수입니다.")
        @ApiModelProperty(notes = "요일", example = "MONDAY")
        private DayOfWeek day_of_week;

        @NotNull(message = "open의 closed는 필수입니다.")
        @ApiModelProperty(notes = "휴무 여부", example = "false")
        private Boolean closed;

        @Pattern(regexp = "^([01][0-9]|2[0-3]):([0-5][0-9])$", message = "open의 open_time은 시간 형식입니다.")
        @ApiModelProperty(notes = "여는 시간", example = "10:00")
        private String open_time;

        @Pattern(regexp = "^([01][0-9]|2[0-3]):([0-5][0-9])$", message = "open의 closed_time은 시간 형식입니다.")
        @ApiModelProperty(notes = "닫는 시간", example = "22:30")
        private String close_time;
    }

    public void checkDataConstraintViolation() {
        if (!isOpenValid()) {
            throw new BaseException("open에 올바르지 않은 값이 들어있습니다.", ExceptionInformation.REQUEST_DATA_INVALID);
        }
    }

    private boolean isOpenValid() {
        if (this.open.size() != 7) {
            return false;
        }

        Set<DayOfWeek> dayOfWeeksWithoutDuplication = new HashSet<>();

        for (Open open : this.open) {
            DayOfWeek dayOfWeek = open.getDay_of_week();
            Boolean closed = open.getClosed();
            String openTime = open.getOpen_time();
            String closeTime = open.getClose_time();

            if (!closed) {
                if (openTime == null || closeTime == null) {
                    return false;
                }
            }

            dayOfWeeksWithoutDuplication.add(dayOfWeek);
        }

        return dayOfWeeksWithoutDuplication.size() == 7;
    }
}
