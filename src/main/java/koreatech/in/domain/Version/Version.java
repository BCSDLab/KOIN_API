package koreatech.in.domain.Version;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class Version {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = { ValidationGroups.CreateAdmin.class, ValidationGroups.UpdateAdmin.class }, message = "버전은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "버전", example = "1.1.0")
    private String version;
    @NotNull(groups = { ValidationGroups.CreateAdmin.class }, message = "타입은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "타입", example = "android")
    private String type;
    @ApiModelProperty(notes = "생성 일자", example = "2018-04-18 09:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;
    @ApiModelProperty(notes = "수정 일자", example = "2018-04-18 09:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public void update(Version version) {
        if(version.version != null) {
            this.version = version.version;
        }
        if(version.type != null) {
            this.type = version.type;
        }
    }
}
