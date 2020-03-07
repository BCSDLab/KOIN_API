package koreatech.in.domain.TemporaryCommunity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class TempComment {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @ApiModelProperty(notes = "게시글 고유 id", example = "10")
    private Integer article_id;
    @NotNull(groups = ValidationGroups.Create.class, message = "댓글 내용은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "내용", example = "내용입니다.")
    private String content;
    @Size(max = 50, message = "닉네임은 50자 이내여야 합니다.")
    @NotNull(groups = ValidationGroups.Create.class, message = "닉네임은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "작성자의 닉네임", example = "jahona")
    private String nickname;
    @NotNull(message = "익명댓글 비밀번호는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "익명댓글 비밀번호", example = "1234")
    private String password;
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

    public Integer getArticle_id() {
        return article_id;
    }

    public void setArticle_id(Integer article_id) {
        this.article_id = article_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Boolean hasGrantDelete(String password) {
        return password.equals(this.getPassword());
    }

    public  Boolean hasGrantUpdate(String string) { return this.hasGrantDelete(string); }

    public void update(TempComment comment) {
        //TODO: 현재 방식 update 문제점, 멤버변수를 특정값으로 초기화했을경우 update시 무조건 초기값 대입
        if(comment.article_id != null) {
            this.article_id = comment.article_id;
        }
        if(comment.content != null) {
            this.content = comment.content;
        }
        if(comment.nickname != null) {
            this.nickname = comment.nickname;
        }
        if(comment.password != null) {
            this.password = comment.password;
        }
        if(comment.is_deleted != null) {
            this.is_deleted = comment.is_deleted;
        }
    }
}
