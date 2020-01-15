package koreatech.in.domain.Callvan;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

public class Company {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "콜벤 회사 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "콜벤 회사 이름", example = "병천콜벤")
    private String name;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "콜벤 회사 전화번호는 비워둘 수 없습니다.")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "콜벤 회사 전화번호", example = "051-555-3333")
    private String phone;
    @ApiModelProperty(notes = "카드 여부, 0이면 미사용, 1이면 사용", example = "false")
    private Boolean pay_card;
    @ApiModelProperty(notes = "계좌이체 여부, 0이면 미사용, 1이면 사용", example = "false")
    private Boolean pay_bank;
    @ApiModelProperty(notes = "전화 횟수", example = "1")
    private Integer hit;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getPay_card() {
        return pay_card;
    }

    public void setPay_card(Boolean pay_card) {
        this.pay_card = pay_card;
    }

    public Boolean getPay_bank() {
        return pay_bank;
    }

    public void setPay_bank(Boolean pay_bank) {
        this.pay_bank = pay_bank;
    }

    public Integer getHit() {
        return hit;
    }

    public void setHit(Integer hit) {
        this.hit = hit;
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

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", pay_card=" + pay_card +
                ", pay_bank=" + pay_bank +
                ", hit=" + hit +
                ", is_deleted=" + is_deleted +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                '}';
    }

    public void update(Company company) {
        if(company.name != null) {
            this.name = company.name;
        }
        if(company.phone != null) {
            this.phone = company.phone;
        }
        if(company.pay_card != null) {
            this.pay_card = company.pay_card;
        }
        if(company.pay_bank != null) {
            this.pay_bank = company.pay_bank;
        }
        if(company.hit != null) {
            this.hit = company.hit;
        }
        if(company.is_deleted != null) {
            this.is_deleted = company.is_deleted;
        }
    }
}
