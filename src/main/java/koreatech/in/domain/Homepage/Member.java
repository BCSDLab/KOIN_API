package koreatech.in.domain.Homepage;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import koreatech.in.dto.member.admin.request.CreateMemberRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@NoArgsConstructor
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

    public Member(CreateMemberRequest request) {
        this.name = request.getName();
        this.student_number = request.getStudent_number();
        this.track = request.getTrack();
        this.position = request.getPosition();
        this.email = request.getEmail();
        this.image_url = request.getImage_url();
    }

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
