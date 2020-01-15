package koreatech.in.domain.Homepage;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class Member {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "이름", example = "최박김")
    private String name;
    @ApiModelProperty(notes = "학번", example = "2019136001")
    private String student_number;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "소속 트랙은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "소속 트랙", example = "BackEnd")
    private String track;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "직급은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "직급", example = "Mentor")
    private String position;
    @ApiModelProperty(notes = "이메일", example = "babaisu@koreatech.ac.kr")
    private String email;
    @ApiModelProperty(notes = "이미지 링크", example = "http://url.com")
    private String image_url;
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

    public String getStudent_number() { return student_number; }

    public void setStudent_number(String student_number) { this.student_number = student_number; }

    public String getTrack() { return track; }

    public void setTrack(String track) { this.track = track; }

    public String getPosition() { return position; }

    public void setPosition(String position) { this.position = position; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getImage_url() { return image_url; }

    public void setImage_url(String image_url) { this.image_url = image_url; }

    public Boolean getIs_deleted() { return is_deleted; }

    public void setIs_deleted(Boolean is_deleted) { this.is_deleted = is_deleted; }

    public Date getCreated_at() { return created_at; }

    public void setCreated_at(Date created_at) { this.created_at = created_at; }

    public Date getUpdated_at() { return updated_at; }

    public void setUpdated_at(Date updated_at) { this.updated_at = updated_at; }

    public void update(Member member) {
        if(member.name != null) {
            this.name = member.name;
        }
        if(member.email != null) {
            this.email = member.email;
        }
        if(member.student_number != null) {
            this.student_number = member.student_number;
        }
        if(member.track != null) {
            this.track = member.track;
        }
        if(member.position != null) {
            this.position = member.position;
        }
        if(member.image_url != null) {
            this.image_url = member.image_url;
        }
        if(member.is_deleted != null) {
            this.is_deleted = member.is_deleted;
        }
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", student_number='" + student_number + '\'' +
                ", track='" + track + '\'' +
                ", position='" + position + '\'' +
                ", email='" + email + '\'' +
                ", image_url='" + image_url + '\'' +
                ", is_deleted=" + is_deleted +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
