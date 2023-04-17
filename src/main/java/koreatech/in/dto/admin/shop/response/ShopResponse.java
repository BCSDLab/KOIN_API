package koreatech.in.dto.admin.shop.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;

@Getter @Builder
@ApiModel("AdminShopResponse")
public class ShopResponse {
    @ApiModelProperty(notes = "고유 id", example = "1", required = true)
    private Integer id;

    @ApiModelProperty(notes = "이름", example = "홉스", required = true)
    private String name;

    @ApiModelProperty(notes = "전화번호", example = "041-000-0000")
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

    @ApiModelProperty(notes = "소속되어있는 상점 카테고리 리스트", required = true)
    private List<ShopCategory> shop_categories;

    @ApiModelProperty(notes = "상점의 메뉴 카테고리 리스트", required = true)
    private List<MenuCategory> menu_categories;

    @ApiModelProperty(notes = "삭제(soft delete) 여부", example = "false", required = true)
    private Boolean is_deleted;

    @ApiModelProperty(value = "상점 일반 정보 업데이트 일자", example = "2023-01-01 12:01:02", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

    @Getter @Builder
    @ApiModel("Open_1")
    public static class Open {
        @ApiModelProperty(notes = "요일", example = "MONDAY", required = true)
        private DayOfWeek day_of_week;

        @ApiModelProperty(notes = "휴무 여부 \n" +
                                  "- true: 휴무 X \n" +
                                  "- false: 휴무 O", example = "false", required = true)
        private Boolean closed;

        @ApiModelProperty(notes = "해당 요일의 오픈 시간", example = "16:00", required = true)
        private String open_time;

        @ApiModelProperty(notes = "해당 요일의 마감 시간", example = "02:00", required = true)
        private String close_time;

        @ApiModelProperty(notes = "상점 오픈 정보 업데이트 일자", example = "2023-01-01 12:01:02", required = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date updated_at;
    }

    @Getter @Builder
    @ApiModel("ShopCategory_1")
    public static class ShopCategory {
        @ApiModelProperty(notes = "고유 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "치킨", required = true)
        private String name;

        @ApiModelProperty(notes = "상점 카테고리 업데이트 일자", example = "2023-01-01 12:01:02", required = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date updated_at;
    }

    @Getter @Builder
    @ApiModel("MenuCategory_1")
    public static class MenuCategory {
        @ApiModelProperty(notes = "고유 id", example = "1", required = true)
        private Integer id;

        @ApiModelProperty(notes = "이름", example = "대표 메뉴", required = true)
        private String name;

        @ApiModelProperty(notes = "상점 메뉴 카테고리 업데이트 일자", example = "2023-01-01 12:01:02", required = true)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date updated_at;
    }
}
