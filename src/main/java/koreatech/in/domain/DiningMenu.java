package koreatech.in.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class DiningMenu {

    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;

    @ApiModelProperty(notes = "일자, default로 today의 년-월-일 값 설정", example = "2018-03-21")
    private String date;

    @ApiModelProperty(notes = "아침, 점심, 저녁", example = "breakfast")
    private String type;

    @ApiModelProperty(notes = "학식 받는 곳", example = "A코스")
    private String place;

    @ApiModelProperty(notes = "카드 금액", example = "2700")
    private Integer price_card;

    @ApiModelProperty(notes = "현금 금액", example = "3000")
    private Integer price_cash;

    @ApiModelProperty(notes = "칼로리", example = "755")
    private Integer kcal;

    @ApiModelProperty(notes = "메뉴", example = "차조밥")
    private String menu;

    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;

    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

//    public static List<String> getPlaces() {
//        return places;
//    }
//
//    public static void setPlaces(List<String> places) {
//        DiningMenu.places = places;
//    }

//
//    private static List<String> places = new ArrayList<String>() {{
//        add("한식");
//        add("일품식");
//        add("특식");
//        add("양식");
//        add("능수관");
//        add("수박여");
//        add("2캠퍼스");
//        add("2캠퍼스-2");
//    }};
//
//    public static Boolean isValidatedPlaces(String place) {
//        return places.contains(place);
//    }
}
