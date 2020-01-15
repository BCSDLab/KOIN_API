package koreatech.in.domain.Homepage;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class TechStack {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @ApiModelProperty(notes = "이미지 링크", example = "http://url.com")
    private String image_url;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "기술 스택명은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "기술 스택명", example = "Spring")
    private String name;
    @ApiModelProperty(notes = "기술 스택 설명", example = "스프링은 웹 프레임워크이다")
    private String description;
    @ApiModelProperty(hidden = true)
    private Integer track_id;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getImage_url() { return image_url; }

    public void setImage_url(String image_url) { this.image_url = image_url; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Integer getTrack_id() { return track_id; }

    public void setTrack_id(Integer track_id) { this.track_id = track_id; }

    public Boolean getIs_deleted() { return is_deleted; }

    public void setIs_deleted(Boolean is_deleted) { this.is_deleted = is_deleted; }

    public Date getCreated_at() { return created_at; }

    public void setCreated_at(Date created_at) { this.created_at = created_at; }

    public Date getUpdated_at() { return updated_at; }

    public void setUpdated_at(Date updated_at) { this.updated_at = updated_at; }

    public void update(TechStack techStack) {
        if(techStack.image_url != null) {
            this.image_url = techStack.image_url;
        }
        if(techStack.name != null) {
            this.name = techStack.name;
        }
        if(techStack.description != null) {
            this.description = techStack.description;
        }
        if(techStack.track_id != null) {
            this.track_id = techStack.track_id;
        }
        if(techStack.is_deleted != null) {
            this.is_deleted = techStack.is_deleted;
        }
    }

    @Override
    public String toString() {
        return "TechStack{" +
                "id=" + id +
                ", image_url='" + image_url + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", track_id=" + track_id +
                ", is_deleted=" + is_deleted +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
