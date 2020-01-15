package koreatech.in.domain.Community;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class Board {
    //TODO: default 값 초기화하는 방법 추가하여 적용
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "게시판 태그는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "게시판 태그", example = "FA001")
    private String tag;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "게시판 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "게시판 이름", example = "자유게시판")
    private String name;
    @ApiModelProperty(notes = "익명 닉네임을 사용하는지 여부", example = "false")
    private Boolean is_anonymous;
    @ApiModelProperty(notes = "게시글 개수", example = "3")
    private Integer article_count;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(notes = "공지사항인지 확인", example = "false")
    private Boolean is_notice;
    @ApiModelProperty(hidden = true)
    private Integer parent_id;
    @ApiModelProperty(hidden = true)
    private Integer seq;
    @ApiModelProperty(hidden = true)
    private List<Board> children;
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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIs_anonymous() {
        return is_anonymous;
    }

    public void setIs_anonymous(Boolean is_anonymous) {
        this.is_anonymous = is_anonymous;
    }

    public Integer getArticle_count() {
        return article_count;
    }

    public void setArticle_count(Integer article_count) {
        this.article_count = article_count;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public Boolean getIs_notice() {
        return is_notice;
    }

    public void setIs_notice(Boolean is_notice) {
        this.is_notice = is_notice;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public List<Board> getChildren() {
        return children;
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

    public void setChildren(List<Board> children) {
        this.children = children;
    }

    public void update(Board board) {
        if(board.tag != null) {
            this.tag = board.tag;
        }
        if(board.name != null) {
            this.name = board.name;
        }
        if(board.is_anonymous != null) {
            this.is_anonymous = board.is_anonymous;
        }
        if(board.article_count != null) {
            this.article_count = board.article_count;
        }
        if(board.is_deleted != null) {
            this.is_deleted = board.is_deleted;
        }
        if(board.is_notice != null) {
            this.is_notice = board.is_notice;
        }
        if(board.parent_id != null) {
            this.parent_id = board.parent_id;
        }
        if(board.seq != null) {
            this.seq = board.seq;
        }
    }
}
