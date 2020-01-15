package koreatech.in.domain.Search;

import com.fasterxml.jackson.annotation.JsonFormat;
import koreatech.in.domain.Community.Article;
import koreatech.in.domain.LostAndFound.LostItem;
import koreatech.in.domain.MarketPlace.Item;
import koreatech.in.domain.TemporaryCommunity.TempArticle;
import koreatech.in.repository.CommunityMapper;
import koreatech.in.repository.LostAndFoundMapper;
import koreatech.in.repository.MarketPlaceMapper;
import koreatech.in.repository.TemporaryCommunityMapper;
import koreatech.in.util.BeanUtil;

import java.util.Date;

public class SearchCommentsMinified {
    private Integer id;
    private Integer table_id;
    private String service_name;
    private String title;
    private Integer user_id;
    private String nickname;
    private Integer hit;
    private Integer comment_count;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

    public SearchCommentsMinified(SearchComments searchComments) {
        this.id = searchComments.getComment_id();
        this.table_id = searchComments.getTable_id();
        this.service_name = SearchEnum.ServiceType.values()[searchComments.getTable_id()].getTypeText();
        switch(table_id) {
            case 0: case 1: case 2: case 4:
                CommunityMapper communityMapper = (CommunityMapper) BeanUtil.getBean("communityMapper");
                Article article = communityMapper.getArticle(searchComments.getArticle_id());
                this.title = article.getTitle();
                this.hit = article.getHit();
                this.comment_count = article.getComment_count();
                break;
            case 3:
                TemporaryCommunityMapper temporaryCommunityMapper = (TemporaryCommunityMapper) BeanUtil.getBean("temporaryCommunityMapper");
                TempArticle tempArticle = temporaryCommunityMapper.getArticle(searchComments.getArticle_id());
                this.title = tempArticle.getTitle();
                this.hit = tempArticle.getHit();
                this.comment_count = tempArticle.getComment_count();
                break;
            case 5:
                LostAndFoundMapper lostAndFoundMapper = (LostAndFoundMapper) BeanUtil.getBean("lostAndFoundMapper");
                LostItem lostItem = lostAndFoundMapper.getLostItem(searchComments.getArticle_id());
                this.title = lostItem.getTitle();
                this.hit = lostItem.getHit();
                this.comment_count = lostItem.getComment_count();
                break;
            case 6:
                MarketPlaceMapper marketPlaceMapper = (MarketPlaceMapper) BeanUtil.getBean("marketPlaceMapper");
                Item item = marketPlaceMapper.getItem(searchComments.getArticle_id());
                this.title = item.getTitle();
                this.hit = item.getHit();
                break;
        }
        this.user_id = searchComments.getUser_id();
        this.nickname = searchComments.getNickname();
        this.created_at = searchComments.getCreated_at();
        this.updated_at = searchComments.getUpdated_at();
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
        return "SearchCommentsMinified{" +
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
