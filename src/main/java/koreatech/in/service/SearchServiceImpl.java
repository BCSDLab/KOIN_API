package koreatech.in.service;

import koreatech.in.domain.Criteria.SearchCriteria;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Search.SearchArticles;
import koreatech.in.domain.Search.SearchArticlesMinified;
import koreatech.in.domain.Shop.Menu;
import koreatech.in.domain.Shop.Shop;
import koreatech.in.exception.PreconditionFailedException;
import koreatech.in.repository.SearchMapper;
import koreatech.in.util.BeanUtil;
import koreatech.in.util.JsonConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static koreatech.in.domain.DomainToMap.domainToMap;

@Service("searchService")
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchMapper searchMapper;

    public Map<String, Object> searchShop(SearchCriteria searchCriteria) throws Exception {
        if (!StringUtils.hasText(searchCriteria.getQuery()))
            throw new PreconditionFailedException(new ErrorMessage("공백으로는 검색할 수 없습니다.", 0));

        Map<String, Object> map = new HashMap<>();
        double totalCount, countByLimit, totalPage;

        switch (searchCriteria.getSearchType()) {
            case 0: default: // 상점명 검색
                totalCount = searchMapper.totalShopCountByName(searchCriteria.getQuery());
                countByLimit = totalCount / searchCriteria.getLimit();
                totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / searchCriteria.getLimit());
                if (totalPage < 0)
                    throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

                List<Shop> shops = searchMapper.findShopByName(searchCriteria.getQuery(), searchCriteria.getCursor(), searchCriteria.getLimit());
                List<Map<String, Object>> shopsMapList = new ArrayList<>();

                /*
                      상점 DB 구조 개편으로 인해 컴파일 에러가 발생하는 부분이 있어 주석 처리
                      TODO: 추후 검색 기능이 다시 생긴다면 리팩토링 필요
                 */
                /*for (Shop shop : shops) {
                    shop.setPermalink(shop.getInternal_name());
                    Map<String, Object> shopMap = domainToMap(shop);
                    if (shopMap.get("image_urls") != null && JsonConstructor.isJsonArrayWithOnlyString(shop.getImage_urls())) {
                        shopMap.replace("image_urls", JsonConstructor.parseJsonArrayWithOnlyString(shop.getImage_urls()));
                    }
                    shopsMapList.add(shopMap);
                }*/

                map.put("shops", shopsMapList);
                map.put("totalPage", totalPage);
                break;
            case 1: // 메뉴명 검색
                totalCount = searchMapper.totalMenuCountByName(searchCriteria.getQuery());
                countByLimit = totalCount / searchCriteria.getLimit();
                totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / searchCriteria.getLimit());
                if (totalPage < 0)
                    throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

                List<Menu> menus = searchMapper.findMenuByName(searchCriteria.getQuery(), searchCriteria.getCursor(), searchCriteria.getLimit());
                List<Map<String, Object>> menusMapList = new ArrayList<>();
                for (Menu menu : menus) {
                    Map<String, Object> menuMap = domainToMap(menu);
                    if (menuMap.get("price_type") != null && JsonConstructor.isJsonArrayWithOnlyObject(menu.getPrice_type())) {
                        menuMap.replace("price_type", JsonConstructor.parseJsonArrayWithObject(menu.getPrice_type()));
                    }
                    menusMapList.add(menuMap);
                }
                map.put("menus", menusMapList);
                map.put("totalPage", totalPage);
                break;
        }
        return map;
    }

    public Map<String, Object> searchCommunity(SearchCriteria searchCriteria) throws Exception {
        if (!StringUtils.hasText(searchCriteria.getQuery()))
            throw new PreconditionFailedException(new ErrorMessage("공백으로는 검색할 수 없습니다.", 0));

        Map<String, Object> map = new HashMap<>();
        double totalCount, countByLimit, totalPage;
        List<SearchArticles> searchArticlesList;
//        List<SearchComments> searchCommentsList;
        List<SearchArticlesMinified> searchArticlesMapList = new ArrayList<>();
//        List<SearchCommentsMinified> searchCommentsMapList = new ArrayList<>();

        switch (searchCriteria.getSearchType()) {
            case 0: default: // 게시글 제목 검색
                totalCount = searchMapper.totalArticlesCountByTitle(searchCriteria.getQuery());
                countByLimit = totalCount / searchCriteria.getLimit();
                totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / searchCriteria.getLimit());
                if (totalPage < 0)
                    throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

                searchArticlesList = searchMapper.findArticlesByTitle(searchCriteria.getQuery(), searchCriteria.getCursor(), searchCriteria.getLimit());
                for (SearchArticles searchArticles : searchArticlesList)
                    searchArticlesMapList.add(((SearchArticlesMinified) BeanUtil.getBean("searchArticlesMinified")).getAdapter(searchArticles));

                map.put("articles", searchArticlesMapList);
                map.put("totalPage", totalPage);
                break;
            case 1: // 게시글 제목 및 내용 검색
                totalCount = searchMapper.totalArticlesCountByTitleAndContent(searchCriteria.getQuery());
                countByLimit = totalCount / searchCriteria.getLimit();
                totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / searchCriteria.getLimit());
                if (totalPage < 0)
                    throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

                searchArticlesList = searchMapper.findArticlesByTitleAndContent(searchCriteria.getQuery(), searchCriteria.getCursor(), searchCriteria.getLimit());
                for (SearchArticles searchArticles : searchArticlesList)
                    searchArticlesMapList.add(((SearchArticlesMinified) BeanUtil.getBean("searchArticlesMinified")).getAdapter(searchArticles));

                map.put("articles", searchArticlesMapList);
                map.put("totalPage", totalPage);
                break;
            case 2: // 게시글 닉네임 검색
                totalCount = searchMapper.totalArticlesCountByNickname(searchCriteria.getQuery());
                countByLimit = totalCount / searchCriteria.getLimit();
                totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / searchCriteria.getLimit());
                if (totalPage < 0)
                    throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));

                searchArticlesList = searchMapper.findArticlesByNickname(searchCriteria.getQuery(), searchCriteria.getCursor(), searchCriteria.getLimit());
                for (SearchArticles searchArticles : searchArticlesList)
                    searchArticlesMapList.add(((SearchArticlesMinified) BeanUtil.getBean("searchArticlesMinified")).getAdapter(searchArticles));

                map.put("articles", searchArticlesMapList);
                map.put("totalPage", totalPage);
                break;
//            case 3: // 댓글 내용 검색
//                totalCount = searchMapper.totalCommentsCountByContent(searchCriteria.getQuery());
//                countByLimit = totalCount / searchCriteria.getLimit();
//                totalPage = countByLimit == Double.POSITIVE_INFINITY || countByLimit == Double.NEGATIVE_INFINITY ? 0 : (int) Math.ceil(totalCount / searchCriteria.getLimit());
//                if (totalPage < 0)
//                    throw new PreconditionFailedException(new ErrorMessage("invalid page number", 2));
//
//                searchCommentsList = searchMapper.findCommentsByContent(searchCriteria.getQuery(), searchCriteria.getCursor(), searchCriteria.getLimit());
//                for (SearchComments searchComments : searchCommentsList)
//                    searchCommentsMapList.add(new SearchCommentsMinified(searchComments));
//
//                map.put("articles", searchArticlesMapList);
//                map.put("totalPage", totalPage);
//                break;
        }
        return map;
    }
}
