package koreatech.in.dto.shop.response.inner;

import lombok.Getter;

import java.util.List;

@Getter
public class Shop {
    private Integer id;
    private String name;
    private String phone;
    private Boolean delivery;
    private Boolean pay_card;
    private Boolean pay_bank;
    private List<Open> open;
    private List<Integer> category_ids;
}
