package koreatech.in.domain.Homepage;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

public class Activity {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "활동명은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "활동명", example = "종강 총회")
    private String title;
    @ApiModelProperty(notes = "활동 설명", example = "고기를 먹었다 맛있었다.")
    private String description;
    // REGEX for Match URL String Array. (ex: ["https://static.testregex.com/upload/testest.png", "https://static.regextest.asdf/upload/testregex.jpg"])
    @Pattern(regexp = "\\[([\\\"\\']https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&\\/\\/=]*)[\\\"\\'],?\\s*)*\\]", message = "이미지 링크 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "이미지 링크, string으로 받아서 저장 후 json 으로 반환", example = "['http://url.com', 'http://url.com']")
    private String image_urls;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "활동 일자는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "활동 일자", example = "2019-08-13")
    private String date;
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

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getImage_urls() { return image_urls; }

    public void setImage_urls(String image_urls) { this.image_urls = image_urls; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public Boolean getIs_deleted() { return is_deleted; }

    public void setIs_deleted(Boolean is_deleted) { this.is_deleted = is_deleted; }

    public Date getCreated_at() { return created_at; }

    public void setCreated_at(Date created_at) { this.created_at = created_at; }

    public Date getUpdated_at() { return updated_at; }

    public void setUpdated_at(Date updated_at) { this.updated_at = updated_at; }

    public void update(Activity activity) {
        if(activity.title != null) {
            this.title = activity.title;
        }
        if(activity.description != null) {
            this.description = activity.description;
        }
        if(activity.image_urls != null) {
            this.image_urls = activity.image_urls;
        }
        if(activity.date != null) {
            this.date = activity.date;
        }
        if(activity.is_deleted != null) {
            this.is_deleted = activity.is_deleted;
        }
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image_urls='" + image_urls + '\'' +
                ", date='" + date + '\'' +
                ", is_deleted=" + is_deleted +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }
}
