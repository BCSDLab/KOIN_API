package koreatech.in.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
@NoArgsConstructor
public class Authority {
    private Integer id;
    private Integer user_id;
    private Boolean grant_user;
    private Boolean grant_callvan;
    private Boolean grant_land;
    private Boolean grant_community;
    private Boolean grant_shop;
    private Boolean grant_version;
    private Boolean grant_market;
    private Boolean grant_circle;
    private Boolean grant_lost;
    private Boolean grant_survey;
    private Boolean grant_bcsdlab;
    private Boolean grant_event;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

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
