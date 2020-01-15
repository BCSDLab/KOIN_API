package koreatech.in.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiningMenu {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @ApiModelProperty(notes = "일자, default로 today의 년-월-일 값 설정", example = "2018-03-21")
    private String date;
    @ApiModelProperty(notes = "아침, 점심, 저녁", example = "breakfast")
    private String type;
    @ApiModelProperty(notes = "종류", example = "양식")
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getPrice_card() {
        return price_card;
    }

    public void setPrice_card(Integer price_card) {
        this.price_card = price_card;
    }

    public Integer getPrice_cash() {
        return price_cash;
    }

    public void setPrice_cash(Integer price_cash) {
        this.price_cash = price_cash;
    }

    public Integer getKcal() {
        return kcal;
    }

    public void setKcal(Integer kcal) {
        this.kcal = kcal;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    public static List<String> getPlaces() {
        return places;
    }

    public static void setPlaces(List<String> places) {
        DiningMenu.places = places;
    }

    @Override
    public String toString() {
        return "DiningMenu{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", type='" + type + '\'' +
                ", place='" + place + '\'' +
                ", price_card='" + price_card + '\'' +
                ", price_cash='" + price_cash + '\'' +
                ", kcal='" + kcal + '\'' +
                ", menu='" + menu + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }

    private static List<String> places = new ArrayList<String>() {{
        add("한식");
        add("일품식");
        add("특식");
        add("양식");
        add("능수관");
        add("수박여");
        add("2캠퍼스");
        add("2캠퍼스-2");
    }};

    public static Boolean isValidatedPlaces(String place) {
        return places.contains(place);
    }
}
