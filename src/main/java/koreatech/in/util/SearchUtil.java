package koreatech.in.util;

import koreatech.in.domain.Community.Article;
import koreatech.in.domain.Community.Comment;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Event.EventArticle;
import koreatech.in.domain.LostAndFound.LostItem;
import koreatech.in.domain.MarketPlace.Item;
import koreatech.in.domain.Search.SearchArticles;
import koreatech.in.domain.Search.SearchComments;
import koreatech.in.domain.Search.SearchEnum;
import koreatech.in.domain.TemporaryCommunity.TempArticle;
import koreatech.in.exception.NotFoundException;
import koreatech.in.repository.SearchMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

public class SearchUtil {
    @Autowired
    private SearchMapper searchMapper;

    private SearchArticles articleToSearchArticles(Article article) {
        Document document;
        String content = article.getContent();
        try {
            document = Jsoup.parse(content);
            Element element = document.body();
            content = element.text();
        }
        catch (IllegalArgumentException e) {
            // TODO: 추후 로깅하기
        }

        SearchArticles searchArticles = new SearchArticles();
        Integer tableId = SearchEnum.articleBoard.get(article.getBoard_id());
        if (tableId == null)
            throw new NotFoundException(new ErrorMessage("non-existent table id", 0));
        searchArticles.setTable_id(tableId);
        searchArticles.setArticle_id(article.getId());
        searchArticles.setContent(content);
        searchArticles.setNickname(article.getNickname());
        searchArticles.setTitle(article.getTitle());
        searchArticles.setUser_id(article.getUser_id());
        searchArticles.setIs_deleted(article.getIs_deleted() == null ? false : article.getIs_deleted());

        return searchArticles;
    }

    private SearchArticles articleToSearchArticles(TempArticle tempArticle) {
        Document document;
        String content = tempArticle.getContent();
        try {
            document = Jsoup.parse(content);
            Element element = document.body();
            content = element.text();
        }
        catch (IllegalArgumentException e) {
            // TODO: 추후 로깅하기
        }

        SearchArticles searchArticles = new SearchArticles();
        searchArticles.setTable_id(SearchEnum.ServiceType.ANONYMOUS.ordinal());
        searchArticles.setArticle_id(tempArticle.getId());
        searchArticles.setContent(content);
        searchArticles.setNickname(tempArticle.getNickname());
        searchArticles.setTitle(tempArticle.getTitle());
        searchArticles.setIs_deleted(tempArticle.getIs_deleted() == null ? false : tempArticle.getIs_deleted());

        return searchArticles;
    }

    private SearchArticles articleToSearchArticles(LostItem lostItem) {
        Document document;
        String content = lostItem.getContent();
        try {
            document = Jsoup.parse(content);
            Element element = document.body();
            content = element.text();
        }
        catch (IllegalArgumentException e) {
            // TODO: 추후 로깅하기
        }

        SearchArticles searchArticles = new SearchArticles();
        searchArticles.setTable_id(SearchEnum.ServiceType.LOST.ordinal());
        searchArticles.setArticle_id(lostItem.getId());
        searchArticles.setContent(content);
        searchArticles.setNickname(lostItem.getNickname());
        searchArticles.setTitle(lostItem.getTitle());
        searchArticles.setUser_id(lostItem.getUser_id());
        searchArticles.setIs_deleted(lostItem.getIs_deleted() == null ? false : lostItem.getIs_deleted());

        return searchArticles;
    }

    private SearchArticles articleToSearchArticles(Item item) {
        Document document;
        String content = item.getContent();
        try {
            document = Jsoup.parse(content);
            Element element = document.body();
            content = element.text();
        }
        catch (IllegalArgumentException e) {
            // TODO: 추후 로깅하기
        }

        SearchArticles searchArticles = new SearchArticles();
        searchArticles.setTable_id(SearchEnum.ServiceType.MARKET.ordinal());
        searchArticles.setArticle_id(item.getId());
        searchArticles.setContent(content);
        searchArticles.setNickname(item.getNickname());
        searchArticles.setTitle(item.getTitle());
        searchArticles.setUser_id(item.getUser_id());
        searchArticles.setIs_deleted(item.getIs_deleted() == null ? false : item.getIs_deleted());

        return searchArticles;
    }

    private SearchArticles articleToSearchArticles(EventArticle eventArticle) {
        Document document;
        String content = eventArticle.getContent();
        try {
            document = Jsoup.parse(content);
            Element element = document.body();
            content = element.text();
        }
        catch (IllegalArgumentException e) {
            // TODO: 추후 로깅하기
        }

        SearchArticles searchArticles = new SearchArticles();
        searchArticles.setTable_id(SearchEnum.ServiceType.EVENT.ordinal());
        searchArticles.setArticle_id(eventArticle.getId());
        searchArticles.setContent(content);
        searchArticles.setNickname(eventArticle.getNickname());
        searchArticles.setTitle(eventArticle.getTitle());
        searchArticles.setUser_id(eventArticle.getUser_id());
        searchArticles.setIs_deleted(eventArticle.getIs_deleted() == null ? false : eventArticle.getIs_deleted());

        return searchArticles;
    }

    public void createArticle(Article article) {
        searchMapper.createSearchArticles(articleToSearchArticles(article));
    }

    public void createArticle(TempArticle tempArticle) {
        searchMapper.createSearchArticles(articleToSearchArticles(tempArticle));
    }

    public void createArticle(LostItem lostItem) {
        searchMapper.createSearchArticles(articleToSearchArticles(lostItem));
    }

    public void createArticle(Item item) {
        searchMapper.createSearchArticles(articleToSearchArticles(item));
    }

    public void createArticle(EventArticle eventArticle) {
        searchMapper.createSearchArticles(articleToSearchArticles(eventArticle));
    }

    public void updateArticle(Article article) {
        SearchArticles searchArticles = searchMapper.findArticlesByTableIdAndArticleId(SearchEnum.articleBoard.get(article.getBoard_id()), article.getId());
        if (searchArticles == null) return;
        searchArticles.update(articleToSearchArticles(article));
        searchMapper.updateSearchArticles(searchArticles);
    }

    public void updateArticle(TempArticle tempArticle) {
        SearchArticles searchArticles = searchMapper.findArticlesByTableIdAndArticleId(SearchEnum.ServiceType.ANONYMOUS.ordinal(), tempArticle.getId());
        if (searchArticles == null) return;
        searchArticles.update(articleToSearchArticles(tempArticle));
        searchMapper.updateSearchArticles(searchArticles);
    }

    public void updateArticle(LostItem lostItem) {
        SearchArticles searchArticles = searchMapper.findArticlesByTableIdAndArticleId(SearchEnum.ServiceType.LOST.ordinal(), lostItem.getId());
        if (searchArticles == null) return;
        searchArticles.update(articleToSearchArticles(lostItem));
        searchMapper.updateSearchArticles(searchArticles);
    }

    public void updateArticle(Item item) {
        SearchArticles searchArticles = searchMapper.findArticlesByTableIdAndArticleId(SearchEnum.ServiceType.MARKET.ordinal(), item.getId());
        if (searchArticles == null) return;
        searchArticles.update(articleToSearchArticles(item));
        searchMapper.updateSearchArticles(searchArticles);
    }

    public void updateArticle(EventArticle eventArticle) {
        SearchArticles searchArticles = searchMapper.findArticlesByTableIdAndArticleId(SearchEnum.ServiceType.EVENT.ordinal(), eventArticle.getId());
        if (searchArticles == null) return;
        searchArticles.update(articleToSearchArticles(eventArticle));
        searchMapper.updateSearchArticles(searchArticles);
    }
}
