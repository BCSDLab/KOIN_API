package koreatech.in.domain.LostAndFound;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.User.User;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

public class LostItem {
    //TODO: default 값으로 초기화
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = { ValidationGroups.CreateAdmin.class, ValidationGroups.UpdateAdmin.class, ValidationGroups.Create.class, ValidationGroups.Update.class }, message = "서비스 타입은 비워둘 수 없습니다.")
    @Min(value = 0, message = "서비스 타입은 0 또는 1이여야 합니다.")
    @Max(value = 1, message = "서비스 타입은 0 또는 1이여야 합니다.")
    @ApiModelProperty(notes = "서비스 타입(0: 습득 서비스, 1: 분실 서비스)", example = "0")
    private Integer type;
    @NotNull(groups = { ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class }, message = "게시글 제목은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "제목", example = "아이패드 주웠습니다")
    private String title;
    @ApiModelProperty(notes = "주운 장소", example = "2공 앞 벤치")
    private String location;
    @ApiModelProperty(notes = "내용", example = "내용")
    private String content;
    @ApiModelProperty(notes = "작성자의 유저 고유 id", example = "1")
    private Integer user_id;
    @ApiModelProperty(notes = "작성자의 닉네임", example = "bbo")
    private String nickname;
    @Min(value = 0, message = "서비스 타입은 0 또는 1이여야 합니다.")
    @Max(value = 1, message = "서비스 타입은 0 또는 1이여야 합니다.")
    @ApiModelProperty(notes = "상태 정보(0: 찾는중, 1: 돌려받음)", example = "0")
    private Integer state;
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "전화번호(포맷형식: 010-0000-0000, 010-000-0000", example = "010-0000-0000")
    private String phone;
    @ApiModelProperty(notes = "전화번호 공개 여부(0: 비공개, 1: 공개)", example = "0")
    private Boolean is_phone_open=false;
    @ApiModelProperty(notes = "이미지 링크, string으로 받아서 저장 후 json 으로 반환", example = "['http://url.com', 'http://url.com']")
    private String image_urls;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(notes = "대표이미지, 썸네일 이미지", example = "http://url.com")
    private String thumbnail;
    @ApiModelProperty(notes = "조회수", example = "1")
    private Integer hit;
    @ApiModelProperty(hidden = true)
    private String ip;
    @ApiModelProperty(notes = "전체 댓글 수", example = "3")
    private Integer comment_count;
    @Pattern(regexp = "^[0-9]{4}-[0-9]{2}-[0-9]{2}", message = "날짜 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "분실 물품 날짜", example = "20191012")
    private String date;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIs_phone_open() {
        return is_phone_open;
    }

    public void setIs_phone_open(Boolean is_phone_open) {
        this.is_phone_open = is_phone_open;
    }

    public String getImage_urls() {
        return image_urls;
    }

    public void setImage_urls(String image_urls) {
        this.image_urls = image_urls;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
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

    public Integer getComment_count() {
        return comment_count;
    }

    public void setComment_count(Integer comment_count) {
        this.comment_count = comment_count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public Boolean hasGrantDelete(User user) {
        if (user == null) return false;
        if ((user.getAuthority() != null) && user.getAuthority().getGrant_community()) return true;
        return (this.getUser_id().equals(user.getId()));
    }

    public Boolean hasGrantUpdate(User user) { return this.hasGrantDelete(user); }

    public void update(LostItem lostItem) {
        //TODO: 현재 방식 update 문제점, 멤버변수를 특정값으로 초기화했을경우 update시 무조건 초기값 대입
        if(lostItem.type != null) {
            this.type = lostItem.type;
        }
        if(lostItem.title != null) {
            this.title = lostItem.title;
        }
        if(lostItem.location != null) {
            this.location = lostItem.location;
        }
        if(lostItem.content != null) {
            this.content = lostItem.content;
        }
        if(lostItem.nickname != null) {
            this.nickname = lostItem.nickname;
        }
        if(lostItem.state != null) {
            this.state = lostItem.state;
        }
        if(lostItem.phone != null) {
            this.phone = lostItem.phone;
        }
        if(lostItem.is_phone_open != null) {
            this.is_phone_open = lostItem.is_phone_open;
        }
        if(lostItem.image_urls != null) {
            this.image_urls = lostItem.image_urls;
        }
        if(lostItem.thumbnail != null) {
            this.thumbnail = lostItem.thumbnail;
        }
        if(lostItem.ip != null) {
            this.ip = lostItem.ip;
        }
        if(lostItem.date != null) {
            this.date = lostItem.date;
        }
    }
}
