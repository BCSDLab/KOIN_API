package koreatech.in.domain.LostAndFound;

import java.util.Date;

public class LostItemViewLog {
    private Integer id;
    private Integer lost_item_id;
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
        return lost_item_id;
    }

    public void setArticle_id(Integer article_id) {
        this.lost_item_id = article_id;
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

    public void update(LostItemViewLog lostItemViewLog) {
        if(lostItemViewLog.lost_item_id != null) {
            this.lost_item_id = lostItemViewLog.lost_item_id;
        }
        if(lostItemViewLog.user_id != null) {
            this.user_id = lostItemViewLog.user_id;
        }
        if(lostItemViewLog.expired_at != null) {
            this.expired_at = lostItemViewLog.expired_at;
        }
        if(lostItemViewLog.ip != null) {
            this.ip = lostItemViewLog.ip;
        }
    }
}
