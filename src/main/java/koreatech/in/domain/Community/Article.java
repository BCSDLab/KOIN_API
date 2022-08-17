package koreatech.in.domain.Community;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.domain.user.User;
import org.jsoup.Jsoup;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class Article {
    //TODO: default 값으로 초기화
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = { ValidationGroups.Create.class, ValidationGroups.CreateAdmin.class, ValidationGroups.Update.class, ValidationGroups.UpdateAdmin.class }, message = "board_id는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "게시판의 고유 id", example = "10")
    private Integer board_id;
    @Size(max = 255, message = "제목은 255자 이내여야 합니다.")
    @NotNull(groups = { ValidationGroups.Create.class, ValidationGroups.CreateAdmin.class }, message = "게시글 제목은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "제목", example = "제목입니다.")
    private String title;
    @NotNull(groups = { ValidationGroups.Create.class, ValidationGroups.CreateAdmin.class }, message = "게시글 내용은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "내용", example = "내용입니다.")
    private String content;
    @ApiModelProperty(notes = "작성자의 고유 id", example = "3")
    private Integer user_id;
    @ApiModelProperty(notes = "작성자의 닉네임", example = "jahona")
    private String nickname;
    @ApiModelProperty(notes = "조회수", example = "1")
    private Integer hit;
    @ApiModelProperty(hidden = true)
    private String ip;
    @ApiModelProperty(notes = "해결 여부(문의게시판에 사용)", example = "false")
    private Boolean is_solved=false;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(notes = "전체 댓글수", example = "1")
    private Integer comment_count;
    @ApiModelProperty(hidden = true)
    private String meta;
    @ApiModelProperty(hidden = true)
    private Boolean is_notice;
    @ApiModelProperty(hidden = true)
    private Integer notice_article_id;
    @ApiModelProperty(notes = "출력시 첫번째 글에 대한 요약글", example = "안녕하세요")
    private String summary;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

    private static final String CACHE_KEY_HOT_ARTICLE_INFO = "%s.Koreatech@HotArticle";

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBoard_id() {
        return board_id;
    }

    public void setBoard_id(Integer board_id) {
        this.board_id = board_id;
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

    public Boolean getIs_solved() {
        return is_solved;
    }

    public void setIs_solved(Boolean is_solved) {
        this.is_solved = is_solved;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Integer getComment_count() {
        return comment_count;
    }

    public void setComment_count(Integer comment_count) {
        this.comment_count = comment_count;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public Boolean getIs_notice() {
        return is_notice;
    }

    public void setIs_notice(Boolean is_notice) {
        this.is_notice = is_notice;
    }

    public Integer getNotice_article_id() {
        return notice_article_id;
    }

    public void setNotice_article_id(Integer notice_article_id) {
        this.notice_article_id = notice_article_id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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
        this.summary = (summary.length() > 100) ? summary.substring(0, 100) : summary;
        return summary;
    }

    public Boolean hasGrantDelete(User user) {
        if (user == null) return false;
        if ((user.getAuthority() != null) && user.getAuthority().getGrant_community()) return true;
        return (this.getUser_id().equals(user.getId()));
    }

    public Boolean hasGrantUpdate(User user) { return this.hasGrantDelete(user); }

    public void update(Article article) {
        //TODO: 현재 방식 update 문제점, 멤버변수를 특정값으로 초기화했을경우 update시 무조건 초기값 대입
        if(article.board_id != null) {
            this.board_id = article.board_id;
        }
        if(article.title != null) {
            this.title = article.title;
        }
        if(article.content != null) {
            this.content = article.content;
        }
        if(article.user_id != null) {
            this.user_id = article.user_id;
        }
        if(article.nickname != null) {
            this.nickname = article.nickname;
        }
        if(article.ip != null) {
            this.ip = article.ip;
        }
        if(article.is_solved != null) {
            this.is_solved = article.is_solved;
        }
        if(article.is_deleted != null) {
            this.is_deleted = article.is_deleted;
        }
        if(article.comment_count != null) {
            this.comment_count = article.comment_count;
        }
        if(article.meta != null) {
            this.meta = article.meta;
        }
        if(article.is_notice != null) {
            this.is_notice = article.is_notice;
        }
        if(article.notice_article_id != null) {
            this.notice_article_id = article.notice_article_id;
        }
    }

    public static String getHotArticlesCacheKey()
    {
        return String.format(CACHE_KEY_HOT_ARTICLE_INFO, "");
    }
}
