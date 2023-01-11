package koreatech.in.domain.Dining;

import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Getter
@ToString
public class DiningMenu {

    private Integer id;

    private String date;

    private String type;

    private String place;

    private Integer price_card;

    private Integer price_cash;

    private Integer kcal;

    private String menu;

    private Date created_at;

    private Date updated_at;
}
