package koreatech.in.domain.Criteria;

import io.swagger.annotations.ApiParam;
import lombok.Getter;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Getter
public class Criteria {
    public static final int MAX_LIMIT = 50;
    public static final int MIN_LIMIT = 1;
    @ApiParam(value = "페이지 \n" +
            "- null일 경우 기본값: 1 \n" +
            "- 1 미만의 값으로 요청할 경우 1로 요청됨")
    private Integer page = 1;
    @ApiParam(value = "페이지당 조회할 최대 개수 \n" +
            "- null일 경우 기본값: 10 \n" +
            "- 50보다 초과된 값으로 요청할 경우 50으로 요청됨")
    private Integer limit = 10;

    public void setLimit(Integer limit) {
        if (limit > MAX_LIMIT) {
            this.limit = MAX_LIMIT;
        } else if (limit < MIN_LIMIT) {
            this.limit = MIN_LIMIT;
        } else {
            this.limit = limit;
        }
    }

    public void setPage(Integer page) {
        this.page = page < 1 ? 1 : page;
    }

    public Integer getCursor() {
        return (page - 1) * limit;
    }

    public Integer extractTotalPage(Integer totalCount) {
        return totalCount.equals(0) ? 1 : (int) Math.ceil((double) totalCount / this.limit);
    }
}
