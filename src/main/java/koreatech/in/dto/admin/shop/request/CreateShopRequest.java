package koreatech.in.dto.admin.shop.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.exception.BaseException;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.DayOfWeek;
import java.util.*;

import static koreatech.in.exception.ExceptionInformation.*;

@Getter @Setter
@ApiModel("AdminCreateShopRequest")
public class CreateShopRequest {
    @Size(min = 1, max = 15, message = "가게명의 길이는 1자 이상 15자 이하입니다.")
    @NotNull(message = "가게명은 필수입니다.")
    @ApiModelProperty(notes = "가게명 \n" +
                              "- not null \n" +
                              "- 1자 이상 15자 이하", example = "써니 숯불 도시락", required = true)
    private String name;

    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}$", message = "전화번호의 형식이 올바르지 않습니다.")
    @NotNull(message = "전화번호는 필수입니다.")
    @ApiModelProperty(notes = "전화번호 \n" +
                              "- not null \n" +
                              "- 정규식 `^[0-9]{3}-[0-9]{3,4}-[0-9]{4}$`을 만족해야함", example = "041-123-4567", required = true)
    private String phone;

    @Valid
    @NotNull(message = "운영 시간 정보는 필수입니다.")
    @ApiModelProperty(notes = "요일별 운영 시간과 휴무 여부 \n" +
                              "- not null \n" +
                              "- 리스트의 길이는 7(1주일 요일 개수) 이어야 함 \n" +
                              "- day_of_week은 각각 `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY`, `SUNDAY` 이어야 함", required = true)
    private List<Open> open = new ArrayList<>();

    @Size(min = 1, max = 100, message = "주소의 길이는 1자 이상 100자 이하입니다.")
    @NotNull(message = "주소는 필수입니다.")
    @ApiModelProperty(notes = "주소 \n" +
                              "- not null \n" +
                              "- 1자 이상 100자 이하", example = "충청남도 천안시 동남구 병천면 충절로 1600", required = true)
    private String address;

    @PositiveOrZero(message = "배달 금액은 0원 이상 2147483647원 이하여야합니다.")
    @ApiModelProperty(notes = "배달 금액 \n" +
                              "- 0 이상 2147483647 이하 \n" +
                              "- null일 경우 0으로 저장됨", example = "1000")
    private Integer delivery_price = 0;

    @Size(min = 1, max = 50, message = "기타정보의 길이는 1자 이상 50자 이하입니다.")
    @ApiModelProperty(notes = "기타정보 \n" +
                              "- 1자 이상 50자 이하", example = "이번주 전 메뉴 10% 할인 이벤트합니다.")
    private String description;

    @NotNull(message = "배달 가능 여부는 필수입니다.")
    @ApiModelProperty(notes = "배달 가능 여부 \n" +
                              "- not null", example = "false", required = true)
    private Boolean delivery;

    @NotNull(message = "카드 가능 여부는 필수입니다.")
    @ApiModelProperty(notes = "카드 가능 여부 \n" +
                              "- not null", example = "true", required = true)
    private Boolean pay_card;

    @NotNull(message = "계좌 이체 가능 여부는 필수입니다.")
    @ApiModelProperty(notes = "계좌 이체 가능 여부 \n" +
                              "- not null", example = "true", required = true)
    private Boolean pay_bank;

    @NotEmpty(message = "소속시킬 상점 카테고리는 최소 1개 선택하여야합니다.")
    @ApiModelProperty(notes = "상점 카테고리 고유 id 리스트 \n" +
                              "- not null \n" +
                              "- 최소 1개", example = "[1, 4]", required = true)
    private List<Integer> category_ids = new ArrayList<>();

    @Size(max = 10, message = "상점 이미지 개수 제한은 최대 10개입니다.")
    @ApiModelProperty(notes = "이미지 URL 리스트 \n" +
                              "- 최대 10개")
    private List<String> image_urls = new ArrayList<>();

    @Getter @Setter
    @ApiModel("Open_2")
    public static class Open {
        @NotNull(message = "운영 시간의 요일 정보는 필수입니다.")
        @ApiModelProperty(notes = "요일 \n" +
                                  "- not null \n" +
                                  "- `MONDAY`, `TUESDAY`, `WEDNESDAY`, `THURSDAY`, `FRIDAY`, `SATURDAY`, `SUNDAY` 중 택1 (중복되면 안됨)", example = "MONDAY", required = true)
        private DayOfWeek day_of_week;

        @NotNull(message = "운영 시간의 휴무 여부는 필수입니다.")
        @ApiModelProperty(notes = "휴무 여부 \n" +
                                  "- not null", example = "false", required = true)
        private Boolean closed;

        @Pattern(regexp = "^([01][0-9]|2[0-3]):([0-5][0-9])$", message = "운영 시간의 여는 시간 정보 형식이 올바르지 않습니다.")
        @ApiModelProperty(notes = "여는 시간 \n" +
                                  "- closed가 false일 때 \n" +
                                  "  - not null \n" +
                                  "  - 정규식 `^([01][0-9]|2[0-3]):([0-5][0-9])$`을 만족해야 함 \n" +
                                  "- closed가 true일 때 \n" +
                                  "  - 어떤 값이 요청되던 null로 저장됨", example = "10:00")
        private String open_time;

        @Pattern(regexp = "^([01][0-9]|2[0-3]):([0-5][0-9])$", message = "운영 시간의 닫는 시간 정보 형식이 올바르지 않습니다.")
        @ApiModelProperty(notes = "닫는 시간 \n" +
                                  "- closed가 false일때 \n" +
                                  "  - not null \n" +
                                  "  - 정규식 `^([01][0-9]|2[0-3]):([0-5][0-9])$`을 만족해야 함 \n" +
                                  "- closed가 true일 때 \n" +
                                  "  - 어떤 값이 요청되던 null로 저장됨", example = "22:30")
        private String close_time;
    }

    public void checkDataConstraintViolation() {
        checkOpenConstraintViolation();
    }

    private void checkOpenConstraintViolation() {
        if (this.open.size() != 7) {
            throw new BaseException(LENGTH_OF_OPENS_MUST_BE_7);
        }

        Set<DayOfWeek> dayOfWeeksWithoutDuplication = new HashSet<>();

        for (Open open : this.open) {
            DayOfWeek dayOfWeek = open.getDay_of_week();
            Boolean closed = open.getClosed();
            String openTime = open.getOpen_time();
            String closeTime = open.getClose_time();

            if (!closed) {
                if (openTime == null || closeTime == null) {
                    throw new BaseException(TIME_INFORMATION_IS_REQUIRED_UNLESS_CLOSED);
                }
            }

            dayOfWeeksWithoutDuplication.add(dayOfWeek);
        }

        if (dayOfWeeksWithoutDuplication.size() != 7) {
            throw new BaseException(DUPLICATE_DAY_OF_WEEK_INFORMATION_EXISTS);
        }
    }
}
