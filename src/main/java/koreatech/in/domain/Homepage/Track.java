package koreatech.in.domain.Homepage;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class Track {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "트랙명은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "트랙명", example = "BackEnd")
    private String name;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "인원수는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "인원수", example = "10")
    private Integer headcount;
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

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Integer getHeadcount() { return headcount; }

    public void setHeadcount(Integer headcount) { this.headcount = headcount; }

    public Boolean getIs_deleted() { return is_deleted; }

    public void setIs_deleted(Boolean is_deleted) { this.is_deleted = is_deleted; }

    public Date getCreated_at() { return created_at; }

    public void setCreated_at(Date created_at) { this.created_at = created_at; }

    public Date getUpdated_at() { return updated_at; }

    public void setUpdated_at(Date updated_at) { this.updated_at = updated_at; }

    public void update(Track track) {
        if(track.name != null) {
            this.name = track.name;
        }
        if(track.headcount != null) {
            this.headcount = track.headcount;
        }
        if(track.is_deleted != null) {
            this.is_deleted = track.is_deleted;
        }
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", headcount=" + headcount +
                ", is_deleted=" + is_deleted +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
