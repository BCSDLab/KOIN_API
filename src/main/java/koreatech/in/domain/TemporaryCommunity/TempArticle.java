package koreatech.in.domain.TemporaryCommunity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import org.jsoup.Jsoup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class TempArticle {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @Size(max = 255, message = "제목은 255자 이내여야 합니다.")
    @NotNull(groups = ValidationGroups.Create.class, message = "게시글 제목은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "제목", example = "제목입니다.")
    private String title;
    @ApiModelProperty(notes = "내용", example = "내용입니다.")
    private String content;
    @Size(max = 50, message = "닉네임은 50자 이내여야 합니다.")
    @NotNull(groups = ValidationGroups.Create.class, message = "닉네임은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "작성자의 닉네임", example = "jahona")
    private String nickname;
    @NotNull(message = "익명게시글 비밀번호는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "익명게시글 비밀번호", example = "1234")
    private String password;
    @ApiModelProperty(notes = "조회수", example = "1")
    private Integer hit;
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

    public String getContentSummary() {
        if (this.content == null) return "";
        String summary = Jsoup.parse(this.content).text();
        summary = summary.replace("&nbsp", "").trim();
        summary = (summary.length() > 100) ? summary.substring(0, 100) : summary;
        return summary;
    }

    public Boolean hasGrantDelete(String password) {
        return password.equals(this.password);
    }

    public Boolean hasGrantUpdate(String password) {
        return hasGrantDelete(password);
    }

    public void update(TempArticle article) {
        //TODO: 현재 방식 update 문제점, 멤버변수를 특정값으로 초기화했을경우 update시 무조건 초기값 대입
        if(article.title != null) {
            this.title = article.title;
        }
        if(article.content != null) {
            this.content = article.content;
        }
        if(article.nickname != null) {
            this.nickname = article.nickname;
        }
        if(article.password != null) {
            this.password = article.password;
        }
        if(article.comment_count != null) {
            this.comment_count = article.comment_count;
        }
        if(article.is_deleted != null) {
            this.is_deleted = article.is_deleted;
        }
    }
}
