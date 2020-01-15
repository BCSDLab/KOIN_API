package koreatech.in.domain.Shop;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class Menu {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @ApiModelProperty(notes = "상점의 고유 id", example = "10")
    private Integer shop_id;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "메뉴 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "메뉴", example = "치즈불닭")
    private String name;
    @NotNull(groups = { ValidationGroups.CreateAdmin.class, ValidationGroups.UpdateAdmin.class }, message = "가격은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "사이즈에 따른 가격", example = "소 : 2.5")
    private String price_type;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice_type() {
        return price_type;
    }

    public void setPrice_type(String price_type) {
        this.price_type = price_type;
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

    public void update(Menu menu) {
        //TODO: 현재 방식 update 문제점, 멤버변수를 특정값으로 초기화했을경우 update시 무조건 초기값 대입
        if(menu.shop_id != null) {
            this.shop_id = menu.shop_id;
        }
        if(menu.name != null) {
            this.name = menu.name;
        }
        if(menu.price_type != null) {
            this.price_type = menu.price_type;
        }
        if(menu.is_deleted != null) {
            this.is_deleted = menu.is_deleted;
        }
    }
}