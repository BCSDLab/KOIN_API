package koreatech.in.domain.Dining;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@ToString
public class DiningMenuDTO {

    @ApiModelProperty(notes = "고유 id", example = "10")
    private final Integer id;

    @ApiModelProperty(notes = "일자, default로 today의 년-월-일 값 설정", example = "2018-03-21")
    private final String date;

    @ApiModelProperty(notes = "아침, 점심, 저녁", example = "DINNER")
    private final String type;

    @ApiModelProperty(notes = "학식 받는 곳", example = "A코스")
    private final String place;

    @ApiModelProperty(notes = "카드 금액", example = "3000")
    private final Integer price_card;

    @ApiModelProperty(notes = "현금 금액", example = "3000")
    private final Integer price_cash;

    @ApiModelProperty(notes = "칼로리", example = "755")
    private final Integer kcal;

    @ApiModelProperty(notes = "메뉴", example = "[차조밥, 고순조]")
    private final List<String> menu;

    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final Date created_at;

    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final Date updated_at;

    public DiningMenuDTO(Integer id, String date, String type, String place, Integer price_card, Integer price_cash, Integer kcal, List<String> menu, Date created_at, Date updated_at) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.place = place;
        this.price_card = price_card;
        this.price_cash = price_cash;
        this.kcal = kcal;
        this.menu = menu;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}