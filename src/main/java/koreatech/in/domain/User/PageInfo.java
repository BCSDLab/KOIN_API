package koreatech.in.domain.User;

import koreatech.in.exception.BaseException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static koreatech.in.exception.ExceptionInformation.PAGE_NOT_FOUND;

@Getter
public class PageInfo {
    Integer totalPage;
    Integer totalCount;
    Integer currentCount;
    Integer currentPage;
    Integer limit;

    @Builder
    private PageInfo(Integer totalCount, Integer currentCount, Integer currentPage, Integer limit) {
        this.totalCount = totalCount;
        this.currentCount = currentCount;
        this.currentPage = currentPage;
        this.limit = limit;
        this.totalPage = extractTotalPage();
        validatePageInfo();
    }

    private Integer extractTotalPage() {
        return totalCount.equals(0) ? 1 : (int) Math.ceil((double) totalCount / this.limit);
    }

    private void validatePageInfo() {
        if (currentPage > totalPage) {
            throw new BaseException(PAGE_NOT_FOUND);
        }
    }
}
