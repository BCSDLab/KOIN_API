package koreatech.in.dto.shop.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter @Setter
public class ShopsConditionDTO {
    @Positive(message = "page는 1 이상 2147483647 이하입니다.")
    @NotNull(message = "page는 필수입니다.")
    private Integer page;

    @Positive(message = "limit는 1 이상 2147483647 이하입니다.")
    @NotNull(message = "limit는 필수입니다.")
    private Integer limit;

    private Boolean is_deleted;
    private Boolean delivery;
    private Boolean pay_card;
    private Boolean pay_bank;

    private Integer category_id;

    private Sort sort;

    private String search_name;

    @ApiModelProperty(hidden = true)
    private List<Filter> filter = new LinkedList<>();

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

    public ShopsConditionDTO removeBlankOfSearchName() {
        if (this.search_name != null) {
            this.search_name = this.search_name.replace(" ", "").toLowerCase();
        }
        return this;
    }

    public void setFilter() {
        if (Objects.equals(this.is_deleted, true)) {
            filter.add(Filter.IS_DELETED);
        }
        if (Objects.equals(this.delivery, true)) {
            filter.add(Filter.DELIVERY);
        }
        if (Objects.equals(this.pay_card, true)) {
            filter.add(Filter.PAY_CARD);
        }
        if (Objects.equals(this.pay_bank, true)) {
            filter.add(Filter.PAY_BANK);
        }
    }
}
