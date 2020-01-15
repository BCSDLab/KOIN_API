package koreatech.in.domain.Circle;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Circle {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "동아리 카테고리는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "예술분야(C001), 공연분야(C002), 운동분야(C003), 학술분야(C004), 종교분야(C005), 사회봉사(C006), 준동아리(C007)", example = "C001")
    private String category;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "동아리 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "동아리 이름", example = "BCSD LAB")
    private String name;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "동아리 한 줄 설명은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "한 줄 설명", example = "상용 서비스 개발을 목표로 하는 KAP 입니다.")
    private String line_description;
    @ApiModelProperty(notes = "이미지 링크, string으로 받아서 저장", example = "https://s3.ap-northeast-2.amazonaws.com/team-kap-koin-storage/assets/img/296.jpg")
    private String logo_url;
    @ApiModelProperty(notes = "세부사항", example = "세부사항입니다.")
    private String description;
    @ApiModelProperty(notes = "외부 링크 JSON 배열(type은 facebook, naver, cyworld) - 추후 추가 될 때마다 갱신", example = "[{type: 'facebook', link: 'url'}]")
    private String link_urls;
    @ApiModelProperty(notes = "이미지 링크, string으로 받아서 저장", example = "https://s3.ap-northeast-2.amazonaws.com/team-kap-koin-storage/assets/img/296.jpg")
    private String background_img_url;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(notes = "담당교수", example = "교수님")
    private String professor;
    @ApiModelProperty(notes = "동아리방 위치", example = "동아리방 412호")
    private String location;
    @ApiModelProperty(notes = "주요사업", example = "컴퓨터 고치기")
    private String major_business;
    @ApiModelProperty(notes = "메인 소개 홈페이지", example = "https://bcsdlab.com")
    private String introduce_url;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLine_description() {
        return line_description;
    }

    public void setLine_description(String line_description) {
        this.line_description = line_description;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink_urls() {
        return link_urls;
    }

    public void setLink_urls(String link_urls) {
        this.link_urls = link_urls;
    }

    public String getBackground_img_url() {
        return background_img_url;
    }

    public void setBackground_img_url(String background_img_url) {
        this.background_img_url = background_img_url;
    }

    public Boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(Boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMajor_business() {
        return major_business;
    }

    public void setMajor_business(String major_business) {
        this.major_business = major_business;
    }

    public String getIntroduce_url() {
        return introduce_url;
    }

    public void setIntroduce_url(String introduce_url) {
        this.introduce_url = introduce_url;
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

    public void update(Circle circle) {
        if(circle.category != null) {
            this.category = circle.category;
        }
        if(circle.name != null) {
            this.name = circle.name;
        }
        if(circle.line_description != null) {
            this.line_description = circle.line_description;
        }
        if(circle.logo_url != null) {
            this.logo_url = circle.logo_url;
        }
        if(circle.description != null) {
            this.description = circle.description;
        }
        if(circle.link_urls != null) {
            this.link_urls = circle.link_urls;
        }
        if(circle.background_img_url != null) {
            this.background_img_url = circle.background_img_url;
        }
        if(circle.is_deleted != null) {
            this.is_deleted = circle.is_deleted;
        }
        if(circle.professor != null) {
            this.professor = circle.professor;
        }
        if(circle.location != null) {
            this.location = circle.location;
        }
        if(circle.major_business != null) {
            this.major_business = circle.major_business;
        }
        if(circle.introduce_url != null) {
            this.introduce_url = circle.introduce_url;
        }
    }

    private static Map<String, String> categories = new HashMap<String, String>() {{
        put("C001", "예술분야");
        put("C002", "공연분야");
        put("C003", "운동분야");
        put("C004", "학술분야");
        put("C005", "종교분야");
        put("C006", "사회봉사");
        put("C007", "준동아리");
    }};

    public static Boolean isValidCategory(String category) {
        return categories.containsKey(category);
    }
}
