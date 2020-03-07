package koreatech.in.domain.MarketPlace;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.User.User;

import javax.validation.constraints.*;
import java.util.Date;

public class Item {
    //TODO: default 값 초기화하는 방법 추가하여 적용
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = { ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class }, message = "서비스 타입은 비워둘 수 없습니다.")
    @Min(value = 0, message = "서비스 타입은 0 또는 1이여야 합니다.")
    @Max(value = 1, message = "서비스 타입은 0 또는 1이여야 합니다.")
    @ApiModelProperty(notes = "서비스 타입(0: 팝니다 서비스, 1: 삽니다 서비스)", example = "0")
    private Integer type;
    @Size(max = 255, message = "제목은 255자 이내여야 합니다.")
    @NotNull(groups = { ValidationGroups.CreateAdmin.class, ValidationGroups.Create.class }, message = "게시글 제목은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "제목", example = "아이패드 팝니다")
    private String title;
    @ApiModelProperty(notes = "내용", example = "내용")
    private String content;
    @ApiModelProperty(notes = "작성자의 유저 고유 id", example = "1")
    private Integer user_id;
    @ApiModelProperty(notes = "작성자의 닉네임", example = "bbo")
    private String nickname;
    @Min(value = 0, message = "상태 정보는 0, 1, 2 중 하나여야 합니다.")
    @Max(value = 2, message = "상태 정보는 0, 1, 2 중 하나여야 합니다.")
    @ApiModelProperty(notes = "상태 정보(0: 판매중, 1: 판매완료, 2: 판매중지)", example = "0")
    private Integer state;
    @Max(value = Integer.MAX_VALUE, message = "입력할 수 없는 가격입니다.")
    @Min(value = 0, message = "입력할 수 없는 가격입니다.")
    @ApiModelProperty(notes = "가격", example = "21000")
    private Integer price = 0;
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "전화번호(포맷형식: 010-0000-0000, 010-000-0000", example = "010-0000-0000")
    private String phone;
    @ApiModelProperty(notes = "전화번호 공개 여부(0: 비공개, 1: 공개)", example = "0")
    private Boolean is_phone_open = false;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @Size(max = 510, message = "썸네일 이미지 링크는 510자 이내여야 합니다.")
    @ApiModelProperty(notes = "대표이미지, 썸네일 이미지", example = "http://url.com")
    private String thumbnail;
    @ApiModelProperty(notes = "조회수", example = "1")
    private Integer hit;
    @ApiModelProperty(hidden = true)
    private String ip;
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    public Boolean hasGrantUpdate(User user) {
        return this.hasGrantDelete(user);
    }

    public void update(Item item) {
        //TODO: 현재 방식 update 문제점, 멤버변수를 특정값으로 초기화했을경우 update시 무조건 초기값 대입
        if(item.title != null) {
            this.title = item.title;
        }
        if(item.content != null) {
            this.content = item.content;
        }
        if(item.user_id != null) {
            this.user_id = item.user_id;
        }
        if(item.nickname != null) {
            this.nickname = item.nickname;
        }
        if(item.state != null) {
            this.state = item.state;
        }
        if(item.price != null) {
            this.price = item.price;
        }
        if(item.phone != null) {
            this.phone = item.phone;
        }
        if(item.is_deleted != null) {
            this.is_deleted = item.is_deleted;
        }
        if(item.is_phone_open != null) {
            this.is_phone_open = item.is_phone_open;
        }
        if(item.thumbnail != null) {
            this.thumbnail = item.thumbnail;
        }
        if(item.ip != null) {
            this.ip = item.ip;
        }
    }
}