package koreatech.in.service;

import koreatech.in.domain.PageResource.PageResource;

public interface PageResourceService {

    PageResource getCardNews() throws Exception;

    PageResource updateCardNewsForAdmin(PageResource pageResource) throws Exception;
}
