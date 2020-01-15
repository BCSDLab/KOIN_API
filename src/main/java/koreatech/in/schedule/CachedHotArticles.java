package koreatech.in.schedule;

import koreatech.in.domain.Community.Article;
import koreatech.in.domain.Community.ArticleResponseType;
import koreatech.in.domain.DomainToMap;
import koreatech.in.repository.CommunityMapper;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("hotArticleSchedule")
public class CachedHotArticles {

    @Resource(name="communityMapper")
    private CommunityMapper communityMapper;

    private static ValueOperations<String, List<Map<String, Object>>> valueOps;
    @Resource(name = "redisTemplate")
    public void setValueOps(ValueOperations<String, List<Map<String, Object>>> _valueOps) {
        valueOps = _valueOps;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void handle() throws Exception {
        List<Map<String, Object>> hotArticles = new ArrayList<Map<String, Object>>();
        try {
            System.out.println("updating hot articles...");
            for (Article article: communityMapper.getHotArticles()) {
                Map<String, Object> col = DomainToMap.domainToMapWithExcept(article , ArticleResponseType.getHotArticleArray());
                hotArticles.add(col);
            }
            valueOps.set(Article.getHotArticlesCacheKey(), hotArticles);
        } catch (NullPointerException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
