package koreatech.in.domain.Community;

import java.util.Date;

public class ArticleViewLog {
    private Integer id;
    private Integer article_id;
    private Integer user_id;
    private Date expired_at;
    private String ip;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getArticle_id() {
        return article_id;
    }

    public void setArticle_id(Integer article_id) {
        this.article_id = article_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Date getExpired_at() {
        return expired_at;
    }

    public void setExpired_at(Date expired_at) {
        this.expired_at = expired_at;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void update(ArticleViewLog articleViewLog) {
        if(articleViewLog.article_id != null) {
            this.article_id = articleViewLog.article_id;
        }
        if(articleViewLog.user_id != null) {
            this.user_id = articleViewLog.user_id;
        }
        if(articleViewLog.expired_at != null) {
            this.expired_at = articleViewLog.expired_at;
        }
        if(articleViewLog.ip != null) {
            this.ip = articleViewLog.ip;
        }
    }
}
