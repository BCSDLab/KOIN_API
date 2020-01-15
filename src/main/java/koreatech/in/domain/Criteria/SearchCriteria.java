package koreatech.in.domain.Criteria;

import io.swagger.annotations.ApiParam;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.exception.PreconditionFailedException;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
public class SearchCriteria extends Criteria {
    @ApiParam(required = true)
    private Integer searchType = 0;
    @ApiParam(required = true)
    private String query;

    public Integer getSearchType() {
        return searchType;
    }

    public void setSearchType(Integer searchType) {
        this.searchType = searchType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query.trim();
    }

    @Override
    public String toString() {
        return "Criteria{" +
                "searchType=" + searchType +
                ", query=" + query +
                '}';
    }
}
