package koreatech.in.service;

import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.PageResource.PageResource;
import koreatech.in.exception.NotFoundException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service(value = "pageResourceService")
public class PageResourceServiceImpl implements PageResourceService {

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    @Resource(name = "redisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public PageResource getCardNews() throws Exception {
        String cardNewsImage = valueOps.get("card_news_image");
        String cardLinkUrl = valueOps.get("card_link_url");

        // 데이터 존재여부 확인
        if(cardNewsImage == null && cardLinkUrl == null) {
            throw new NotFoundException(new ErrorMessage("CardNews URLs not found.", 0));
        }

        PageResource pageResource = new PageResource();
        pageResource.setCard_news_image(cardNewsImage);
        pageResource.setCard_link_url(cardLinkUrl);

        return pageResource;
    }

    @Override
    public PageResource updateCardNewsForAdmin(PageResource pageResource) throws Exception {
        PageResource pageResource_old = new PageResource();
        String cardNewsImage = valueOps.get("card_news_image");
        String cardLinkUrl = valueOps.get("card_link_url");

        // 기존 데이터 처리
        if(cardNewsImage != null) {
            pageResource_old.setCard_news_image(cardNewsImage);
            redisTemplate.delete("card_news_image");
        }
        if(cardLinkUrl != null) {
            pageResource_old.setCard_link_url(cardLinkUrl);
            redisTemplate.delete("card_link_url");
        }

        // 새 데이터로 갱신
        pageResource_old.update(pageResource);
        if(pageResource_old.getCard_news_image() != null) {
            valueOps.set("card_news_image", pageResource_old.getCard_news_image());
        }
        if(pageResource_old.getCard_link_url() != null) {
            valueOps.set("card_link_url", pageResource_old.getCard_link_url());
        }

        return pageResource_old;
    }
}
