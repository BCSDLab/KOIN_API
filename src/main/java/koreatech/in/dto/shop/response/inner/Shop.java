package koreatech.in.dto.shop.response.inner;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class Shop {
    private Integer id;
    private String name;
    private String phone;
    private Boolean delivery;
    private Boolean pay_card;
    private Boolean pay_bank;
    private List<Map<String, Object>> open;
    private List<ShopCategory> categories;
}
