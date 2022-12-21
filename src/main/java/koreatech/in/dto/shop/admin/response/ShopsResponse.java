package koreatech.in.dto.shop.admin.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class ShopsResponse {
    private Integer total_count;
    private Integer current_count;
    private Integer total_page;
    private Integer current_page;
    private List<Shop> shops;

    @Getter
    public static final class Shop {
        private Integer id;
        private String name;
        private String phone;
        private List<String> category_names;
        private Boolean is_deleted;
    }
}
