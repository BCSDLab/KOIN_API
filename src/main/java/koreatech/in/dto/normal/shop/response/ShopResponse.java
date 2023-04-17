package koreatech.in.dto.normal.shop.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;

@Getter @Builder
public class ShopResponse {
    @ApiModelProperty(notes = "고유 id", example = "1", required = true)
    private Integer id;

    @ApiModelProperty(notes = "이름", example = "수신반점", required = true)
    private String name;

    @ApiModelProperty(notes = "전화번호", example = "041-000-0000", required = true)
    private String phone;

    @ApiModelProperty(notes = "주소", example = "충청남도 천안시 동남구 병천면", required = true)
    private String address;

    @ApiModelProperty(notes = "설명")
    private String description;

    @ApiModelProperty(notes = "배달 가능 여부", example = "true", required = true)
    private Boolean delivery;

    @ApiModelProperty(notes = "배달비", example = "1000", required = true)
    private Integer delivery_price;

    @ApiModelProperty(notes = "카드 계산 가능 여부", example = "false", required = true)
    private Boolean pay_card;

    @ApiModelProperty(notes = "계좌 이체 가능 여부", example = "true", required = true)
    private Boolean pay_bank;

    @ApiModelProperty(notes = "요일별 휴무 여부 및 장사 시간", required = true)
    private List<Open> open;

    @ApiModelProperty(notes = "이미지 URL 리스트")
    private List<String> image_urls;

    @ApiModelProperty(notes = "소속된 상점 카테고리 리스트", required = true)
    private List<ShopCategory> shop_categories;

    @ApiModelProperty(notes = "상점에 있는 메뉴 카테고리 리스트", required = true)
    private List<MenuCategory> menu_categories;

    @ApiModelProperty(value = "업데이트 일자", example = "2023-01-01 12:01:02", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

    @Getter @Builder
    @ApiModel("Open_4")
    public static class Open {
        @ApiModelProperty(notes = "요일", example = "MONDAY", required = true)
        private DayOfWeek day_of_week;

        @ApiModelProperty(notes = "휴무 여부 \n" +
                                  "- true: 휴무 O \n" +
                                  "- false: 휴무 X", example = "false", required = true)
        private Boolean closed;

        @ApiModelProperty(notes = "오픈 시간", example = "16:00", required = true)
        private String open_time;

        @ApiModelProperty(notes = "마감 시간", example = "02:00", required = true)
        private String close_time;
    }

    @Getter @Builder
    @ApiModel("ShopCategory_2")
    public static class ShopCategory {
        @ApiModelProperty(notes = "고유 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "중국집", required = true)
        private String name;
    }

    @Getter @Builder
    @ApiModel("MenuCategory_2")
    public static class MenuCategory {
        @ApiModelProperty(notes = "고유 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "대표 메뉴", required = true)
        private String name;
    }
}
