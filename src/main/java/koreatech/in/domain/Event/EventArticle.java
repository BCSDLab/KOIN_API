package koreatech.in.domain.Event;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.user.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

public class EventArticle {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = { ValidationGroups.Create.class, ValidationGroups.CreateAdmin.class }, message = "홍보 대상 가게는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "주변상점의 고유 id", example = "10")
    private Integer shop_id;
    @NotNull(groups = { ValidationGroups.Create.class, ValidationGroups.CreateAdmin.class }, message = "게시글 제목은 비워둘 수 없습니다.")
    @Size(max = 20, message = "제목은 20자 이내여야 합니다.")
    @ApiModelProperty(notes = "제목", example = "제목입니다.")
    private String title;
    @NotNull(groups = { ValidationGroups.Create.class, ValidationGroups.CreateAdmin.class }, message = "홍보 문구는 비워둘 수 없습니다.")
    @Size(max = 50, message = "홍보 문구는 50자 이내여야 합니다.")
    @ApiModelProperty(notes = "홍보 문구", example = "나윤재의 통닭집 전메뉴 10% 세일 이벤트합니다.")
    private String event_title;
    @NotNull(groups = { ValidationGroups.Create.class, ValidationGroups.CreateAdmin.class }, message = "게시글 내용은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "내용", example = "내용입니다.")
    private String content;
    @ApiModelProperty(notes = "작성자의 고유 id", example = "3")
    private Integer user_id;
    @ApiModelProperty(notes = "작성자의 닉네임", example = "jahona")
    private String nickname;
    @ApiModelProperty(notes = "대표이미지, 썸네일 이미지", example = "http://url.com")
    private String thumbnail;
    @ApiModelProperty(notes = "조회수", example = "1")
    private Integer hit;
    @ApiModelProperty(hidden = true)
    private String ip;
    @NotNull(groups = { ValidationGroups.Create.class, ValidationGroups.CreateAdmin.class }, message = "행사 시작일은 비워둘 수 없습니다.")
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}", message = "날짜 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "행사 시작일", example = "2019-10-12")
    private String start_date;
    @NotNull(groups = { ValidationGroups.Create.class, ValidationGroups.CreateAdmin.class }, message = "행사 마감일은 비워둘 수 없습니다.")
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}", message = "날짜 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "행사 마감일", example = "2019-10-12")
    private String end_date;
    @ApiModelProperty(notes = "전체 댓글수", example = "1")
    private Integer comment_count;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShop_id() {
        return shop_id;
    }

    public void setShop_id(Integer shop_id) {
        this.shop_id = shop_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Integer getHit() {
        return hit;
    }

    public void setHit(Integer hit) {
        this.hit = hit;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
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

    public Integer getComment_count() {
        return comment_count;
    }

    public void setComment_count(Integer comment_count) {
        this.comment_count = comment_count;
    }

    public Boolean hasGrantDelete(User user) {
        if (user == null) return false;
        if ((user.getAuthority() != null) && user.getAuthority().getGrant_event()) return true;
        return (this.getUser_id().equals(user.getId()));
    }

    public Boolean hasGrantUpdate(User user) { return this.hasGrantDelete(user); }

    public void update(EventArticle eventArticle) {
        if(eventArticle.shop_id != null) {
            this.shop_id = eventArticle.shop_id;
        }
        if(eventArticle.title != null) {
            this.title = eventArticle.title;
        }
        if(eventArticle.event_title != null) {
            this.event_title = eventArticle.event_title;
        }
        if(eventArticle.content != null) {
            this.content = eventArticle.content;
        }
        if(eventArticle.user_id != null) {
            this.user_id = eventArticle.user_id;
        }
        if(eventArticle.nickname != null) {
            this.nickname = eventArticle.nickname;
        }
        if(eventArticle.thumbnail != null) {
            this.thumbnail = eventArticle.thumbnail;
        }
        if(eventArticle.hit != null) {
            this.hit = eventArticle.hit;
        }
        if(eventArticle.ip != null) {
            this.ip = eventArticle.ip;
        }
        if(eventArticle.start_date != null) {
            this.start_date = eventArticle.start_date;
        }
        if(eventArticle.end_date != null) {
            this.end_date = eventArticle.end_date;
        }
        if(eventArticle.comment_count != null) {
            this.comment_count = eventArticle.comment_count;
        }
        if(eventArticle.is_deleted != null) {
            this.is_deleted = eventArticle.is_deleted;
        }
    }

    @Override
    public String toString() {
        return "EventArticle{" +
                ", shop_id=" + shop_id +
                ", title='" + title + '\'' +
                ", event_title='" + event_title + '\'' +
                ", content='" + content + '\'' +
                ", user_id='" + user_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                '}';
    }
}
