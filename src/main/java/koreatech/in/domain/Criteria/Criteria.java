package koreatech.in.domain.Criteria;

import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
public class Criteria {
    @ApiParam(value = "페이지 \n" +
            "- NOT NULL \n" +
            "- 기본값: 1 \n" +
            "- 1 미만의 값으로 요청할 경우 1로 요청됨",
            required = true)
    private Integer page = 1;
    @ApiParam(value = "페이지당 조회할 최대 개수 \n" +
            "- NOT NULL \n" +
            "- 기본값: 10 \n" +
            "- 50보다 초과된 값으로 요청할 경우 50으로 요청됨",
            required = true)
    private Integer limit = 10;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit > 50 ? 50 : limit;
    }

    public Integer getPage() {
        return page;
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

    @Override
    public String toString() {
        return "Criteria{" +
                "page=" + page +
                ", limit=" + limit +
                '}';
    }
}
