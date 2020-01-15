package koreatech.in.domain.Search;

import com.fasterxml.jackson.annotation.JsonFormat;
import koreatech.in.domain.Community.Article;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.Event.EventArticle;
import koreatech.in.domain.LostAndFound.LostItem;
import koreatech.in.domain.MarketPlace.Item;
import koreatech.in.domain.TemporaryCommunity.TempArticle;
import koreatech.in.exception.NotFoundException;
import koreatech.in.repository.*;
import koreatech.in.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class SearchArticlesMinified {
    private Integer id;
    private Integer table_id;
    private String service_name;
    private String title;
    private Integer user_id;
    private String nickname;
    private Integer hit;
    private Integer comment_count;
    private String permalink;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

    @Value("${project.domain}")
    private String env;
    @Autowired
    private CommunityMapper communityMapper; //= (CommunityMapper) BeanUtil.getBean("communityMapper");
    @Autowired
    private TemporaryCommunityMapper temporaryCommunityMapper; //= (TemporaryCommunityMapper) BeanUtil.getBean("temporaryCommunityMapper");
    @Autowired
    private LostAndFoundMapper lostAndFoundMapper; //= (LostAndFoundMapper) BeanUtil.getBean("lostAndFoundMapper");
    @Autowired
    private MarketPlaceMapper marketPlaceMapper; //= (MarketPlaceMapper) BeanUtil.getBean("marketPlaceMapper");
    @Autowired
    private EventMapper eventMapper;

    public SearchArticlesMinified getAdapter(SearchArticles searchArticles) {
        this.id = searchArticles.getArticle_id();
        this.table_id = searchArticles.getTable_id();
        this.service_name = SearchEnum.ServiceType.values()[searchArticles.getTable_id()].getTypeText();
        this.title = searchArticles.getTitle();
        this.user_id = searchArticles.getUser_id();
        this.nickname = searchArticles.getNickname();
        switch (this.table_id) {
            case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 8:
                Article article = communityMapper.getArticle(searchArticles.getArticle_id());
                this.hit = article.getHit();
                this.comment_count = article.getComment_count();
                this.permalink = String.format("%s/board/%s/%d", env, SearchEnum.articleBoardName.get(article.getBoard_id()), article.getId());
                break;
            case 7:
                TempArticle tempArticle = temporaryCommunityMapper.getArticle(searchArticles.getArticle_id());
                this.hit = tempArticle.getHit();
                this.comment_count = tempArticle.getComment_count();
                this.permalink = String.format("%s/board/anonymous/%d", env, tempArticle.getId());
                break;
            case 9:
                LostItem lostItem = lostAndFoundMapper.getLostItem(searchArticles.getArticle_id());
                this.hit = lostItem.getHit();
                this.comment_count = lostItem.getComment_count();
                this.permalink = String.format("%s/lost/detail/%d", env, lostItem.getId());
                break;
            case 10:
                Item item = marketPlaceMapper.getItem(searchArticles.getArticle_id());
                this.hit = item.getHit();
                this.permalink = String.format("%s/market/%s/%d", env, item.getType() == 0 ? "sell" : "buy", item.getId());
                break;
            case 11:
                EventArticle eventArticle = eventMapper.getEventArticle(searchArticles.getArticle_id());
                this.hit = eventArticle.getHit();
                this.comment_count = eventArticle.getComment_count();
                this.permalink = String.format("%s/events/%d", env, eventArticle.getId());
                break;
            default:
                throw new NotFoundException(new ErrorMessage("존재하지 않는 게시판입니다.", 0));
        }
        this.created_at = searchArticles.getCreated_at();
        this.updated_at = searchArticles.getUpdated_at();

        return this;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTable_id() {
        return table_id;
    }

    public void setTable_id(Integer table_id) {
        this.table_id = table_id;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getHit() {
        return hit;
    }

    public void setHit(Integer hit) {
        this.hit = hit;
    }

    public Integer getComment_count() {
        return comment_count;
    }

    public void setComment_count(Integer comment_count) {
        this.comment_count = comment_count;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return "SearchArticlesMinified{" +
                "id='" + id + '\'' +
                ", table_id='" + table_id + '\'' +
                ", service_name='" + service_name + '\'' +
                ", title='" + title + '\'' +
                ", user_id='" + user_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", hit='" + hit + '\'' +
                ", comment_count='" + comment_count + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
