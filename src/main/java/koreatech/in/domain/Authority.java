package koreatech.in.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class Authority {
    private Integer id;
    private Integer user_id;
    private Boolean grant_user;
    private Boolean grant_callvan;
    private Boolean grant_land;
    private Boolean grant_community;
    private Boolean grant_shop;
    private Boolean grant_version;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;
    private Boolean grant_market;
    private Boolean grant_circle;
    private Boolean grant_lost;
    private Boolean grant_survey;
    private Boolean grant_bcsdlab;
    private Boolean grant_event;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Boolean getGrant_user() {
        return grant_user;
    }

    public void setGrant_user(Boolean grant_user) {
        this.grant_user = grant_user;
    }

    public Boolean getGrant_callvan() {
        return grant_callvan;
    }

    public void setGrant_callvan(Boolean grant_callvan) {
        this.grant_callvan = grant_callvan;
    }

    public Boolean getGrant_land() {
        return grant_land;
    }

    public void setGrant_land(Boolean grant_land) {
        this.grant_land = grant_land;
    }

    public Boolean getGrant_community() {
        return grant_community;
    }

    public void setGrant_community(Boolean grant_community) {
        this.grant_community = grant_community;
    }

    public Boolean getGrant_shop() {
        return grant_shop;
    }

    public void setGrant_shop(Boolean grant_shop) {
        this.grant_shop = grant_shop;
    }

    public Boolean getGrant_version() {
        return grant_version;
    }

    public void setGrant_version(Boolean grant_version) {
        this.grant_version = grant_version;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
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

    public Boolean getGrant_market() {
        return grant_market;
    }

    public void setGrant_market(Boolean grant_market) {
        this.grant_market = grant_market;
    }

    public Boolean getGrant_circle() {
        return grant_circle;
    }

    public void setGrant_circle(Boolean grant_circle) {
        this.grant_circle = grant_circle;
    }

    public Boolean getGrant_lost() {
        return grant_lost;
    }

    public void setGrant_lost(Boolean grant_lost) {
        this.grant_lost = grant_lost;
    }

    public Boolean getGrant_survey() {
        return grant_survey;
    }

    public void setGrant_survey(Boolean grant_survey) {
        this.grant_survey = grant_survey;
    }

    public Boolean getGrant_bcsdlab() { return grant_bcsdlab; }

    public void setGrant_bcsdlab(Boolean grant_bcsdlab) { this.grant_bcsdlab = grant_bcsdlab; }

    public Boolean getGrant_event() {
        return grant_event;
    }

    public void setGrant_event(Boolean grant_event) {
        this.grant_event = grant_event;
    }

    @Override
    public String toString() {
        return "Authority{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", grant_user=" + grant_user +
                ", grant_callvan=" + grant_callvan +
                ", grant_land=" + grant_land +
                ", grant_community=" + grant_community +
                ", grant_shop=" + grant_shop +
                ", grant_version=" + grant_version +
                ", is_deleted=" + is_deleted +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", grant_market=" + grant_market +
                ", grant_circle=" + grant_circle +
                ", grant_lost=" + grant_lost +
                ", grant_survey=" + grant_survey +
                ", grant_bcsdlab=" + grant_bcsdlab +
                ", grant_event=" + grant_event +
                '}';
    }

    public void init() {
        if (this.grant_user == null) {
            this.grant_user = false;
        }
        if (this.grant_callvan == null) {
            this.grant_callvan = false;
        }
        if (this.grant_land == null) {
            this.grant_land = false;
        }
        if (this.grant_community == null) {
            this.grant_community = false;
        }
        if (this.grant_shop == null) {
            this.grant_shop = false;
        }
        if (this.grant_version == null) {
            this.grant_version = false;
        }
        if (this.grant_market == null) {
            this.grant_market = false;
        }
        if (this.grant_circle == null) {
            this.grant_circle = false;
        }
        if (this.grant_lost == null) {
            this.grant_lost = false;
        }
        if (this.grant_survey == null) {
            this.grant_survey = false;
        }
        if (this.grant_bcsdlab == null) {
            this.grant_bcsdlab = false;
        }
        if (this.grant_event == null) {
            this.grant_event = false;
        }
        if (this.is_deleted == null) {
            this.is_deleted = false;
        }
    }

    public void update(Authority authority) {
        if (authority.getGrant_user() != null) {
            this.grant_user = authority.getGrant_user();
        }
        if (authority.getGrant_community() != null) {
            this.grant_community = authority.getGrant_community();
        }
        if (authority.getGrant_version() != null) {
            this.grant_version = authority.getGrant_version();
        }
        if (authority.getGrant_land() != null) {
            this.grant_land = authority.getGrant_land();
        }
        if (authority.getGrant_callvan() != null) {
            this.grant_callvan = authority.getGrant_callvan();
        }
        if (authority.getGrant_circle() != null) {
            this.grant_circle = authority.getGrant_circle();
        }
        if (authority.getGrant_lost() != null) {
            this.grant_lost = authority.getGrant_lost();
        }
        if (authority.getGrant_market() != null) {
            this.grant_market = authority.getGrant_market();
        }
        if (authority.getGrant_shop() != null) {
            this.grant_shop = authority.getGrant_shop();
        }
        if (authority.getGrant_survey() != null) {
            this.grant_survey = authority.getGrant_survey();
        }
        if (authority.getGrant_bcsdlab() != null) {
            this.grant_bcsdlab = authority.getGrant_bcsdlab();
        }
        if (authority.getGrant_event() != null) {
            this.grant_event = authority.getGrant_event();
        }
    }
}
