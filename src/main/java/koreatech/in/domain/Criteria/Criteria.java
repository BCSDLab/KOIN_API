package koreatech.in.domain.Criteria;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.PreconditionFailedException;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
public class Criteria {
    @ApiParam(required = false, defaultValue = "1")
    private Integer page = 1;
    @ApiParam(required = false, defaultValue = "10")
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

    public Integer calcTotalPage(Integer totalCount){
        double countByLimit = (double)totalCount / this.limit;
        int totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / this.limit);

        if (totalPage < 0)
            throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

        return totalPage;
    }

    @Override
    public String toString() {
        return "Criteria{" +
                "page=" + page +
                ", limit=" + limit +
                '}';
    }
}
