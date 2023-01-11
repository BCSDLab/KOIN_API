package koreatech.in.domain.Criteria;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.PreconditionFailedException;
import lombok.Getter;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Getter
public class Criteria {
    @ApiParam(value = "페이지 \n" +
            "- null일 경우 기본값: 1 \n" +
            "- 1 미만의 값으로 요청할 경우 1로 요청됨")
    private Integer page = 1;
    @ApiParam(value = "페이지당 조회할 최대 개수 \n" +
            "- null일 경우 기본값: 10 \n" +
            "- 50보다 초과된 값으로 요청할 경우 50으로 요청됨")
    private Integer limit = 10;

    public void setLimit(Integer limit) {
        this.limit = limit > 50 ? 50 : limit;
    }

    public void setPage(Integer page) {
        this.page = page < 1 ? 1 : page;
    }

    public Integer getCursor() {
        return (page - 1) * limit;
    }

    public Integer calcTotalPage(Integer totalCount) {
        double countByLimit = (double) totalCount / this.limit;
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / this.limit);

        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        return totalPage;
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
