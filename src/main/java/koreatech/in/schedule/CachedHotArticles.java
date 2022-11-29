package koreatech.in.schedule;

import koreatech.in.domain.Community.Article;
import koreatech.in.domain.Community.ArticleResponseType;
import koreatech.in.domain.DomainToMap;
import koreatech.in.repository.CommunityMapper;
import koreatech.in.util.StringRedisUtilObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("hotArticleSchedule")
public class CachedHotArticles {

    @Resource(name = "communityMapper")
    private CommunityMapper communityMapper;

    @Autowired
    private StringRedisUtilObj stringRedisUtilObj;

    @Scheduled(cron = "0 */5 * * * *")
    public void handle() throws Exception {
        List<Map<String, Object>> hotArticles = new ArrayList<>();
        try {
//            System.out.println("updating hot articles...");
            for (Article article : communityMapper.getHotArticles()) {
                Map<String, Object> col = DomainToMap.domainToMapWithExcept(article, ArticleResponseType.getHotArticleArray());
                hotArticles.add(col);
            }
            stringRedisUtilObj.setDataAsString(Article.getHotArticlesCacheKey(), hotArticles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
