package koreatech.in.dto.shop.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ShopsConditionRequest {
    @Positive(message = "page는 1 이상 2147483647 이하입니다.")
    @NotNull(message = "page는 필수입니다.")
    private Integer page;

    @Positive(message = "limit는 1 이상 2147483647 이하입니다.")
    @NotNull(message = "limit는 필수입니다.")
    private Integer limit;

    private List<Filter> filter = new ArrayList<>();

    private Integer category_id;

    private Sort sort;

    private String search_name;

    @Getter
    private enum Filter {
        IS_DELETED(1),
        DELIVERY(2),
        PAY_CARD(3),
        PAY_BANK(4);

        Filter(int value) {
            this.value = value;
        }

        private final int value;
    }

    @Getter
    private enum Sort {
        NAME(1),
        CREATED_AT(2);

        Sort(int value) {
            this.value = value;
        }

        private final int value;
    }

    public Integer extractTotalPage(Integer totalCount) {
        return totalCount.equals(0) ? 1 : (int) Math.ceil(((double)totalCount) / this.limit);
    }

    public Integer extractBegin() {
        return this.limit * this.page - this.limit;
    }

    // 검색을 위해서 공백은 전부 제거해놓는 메소드
    public ShopsConditionRequest removeBlankOfSearchName() {
        if (this.search_name != null) {
            this.search_name = this.search_name.replace(" ", "").toLowerCase();
        }
        return this;
    }
}
