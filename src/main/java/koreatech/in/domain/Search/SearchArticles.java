package koreatech.in.domain.Search;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class SearchArticles {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @ApiModelProperty(notes = "DB 테이블 고유 id", example = "1")
    private Integer table_id;
    @ApiModelProperty(notes = "게시글 고유 id", example = "128")
    private Integer article_id;
    @ApiModelProperty(notes = "게시글 제목", example = "제목입니다.")
    private String title;
    @ApiModelProperty(notes = "게시글 내용", example = "내용입니다.")
    private String content;
    @ApiModelProperty(notes = "유저 고유 id", example = "25")
    private Integer user_id;
    @ApiModelProperty(notes = "닉네임", example = "yunjaena")
    private String nickname;
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

    public Integer getTable_id() {
        return table_id;
    }

    public void setTable_id(Integer table_id) {
        this.table_id = table_id;
    }

    public Integer getArticle_id() {
        return article_id;
    }

    public void setArticle_id(Integer article_id) {
        this.article_id = article_id;
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

    public void update(SearchArticles searchArticles) {
        if(searchArticles.title != null) {
            this.title = searchArticles.title;
        }
        if(searchArticles.content != null) {
            this.content = searchArticles.content;
        }
        if(searchArticles.nickname != null) {
            this.nickname = searchArticles.nickname;
        }
        if(searchArticles.is_deleted != null) {
            this.is_deleted = searchArticles.is_deleted;
        }
    }

    @Override
    public String toString() {
        return "SearchArticles{" +
                "id='" + id + '\'' +
                ", table_id='" + table_id + '\'' +
                ", article_id='" + article_id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", user_id='" + user_id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", is_deleted='" + is_deleted + '\'' +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                '}';
    }
}
