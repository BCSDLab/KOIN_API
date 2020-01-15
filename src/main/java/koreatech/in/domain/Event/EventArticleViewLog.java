package koreatech.in.domain.Event;

import java.util.Date;

public class EventArticleViewLog {
    private Integer id;
    private Integer event_articles_id;
    private Integer user_id;
    private Date expired_at;
    private String ip;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEvent_articles_id() {
        return event_articles_id;
    }

    public void setEvent_articles_id(Integer event_articles_id) {
        this.event_articles_id = event_articles_id;
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

    public void update(EventArticleViewLog eventArticleViewLog) {
        if(eventArticleViewLog.event_articles_id != null) {
            this.event_articles_id = eventArticleViewLog.event_articles_id;
        }
        if(eventArticleViewLog.user_id != null) {
            this.user_id = eventArticleViewLog.user_id;
        }
        if(eventArticleViewLog.expired_at != null) {
            this.expired_at = eventArticleViewLog.expired_at;
        }
        if(eventArticleViewLog.ip != null) {
            this.ip = eventArticleViewLog.ip;
        }
    }
}
