package koreatech.in.domain.Shop;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class ShopMenuDetail {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @ApiModelProperty(notes = "상점 메뉴의 고유 id", example = "10")
    private Integer shop_menu_id;
    @ApiModelProperty(notes = "옵션 이름", example = "대")
    private String option;
    @ApiModelProperty(notes = "가격", example = "23000")
    private Integer price;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(hidden = true)
    private Date created_at;
    @ApiModelProperty(hidden = true)
    private Date updated_at;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShop_menu_id() {
        return shop_menu_id;
    }

    public void setShop_menu_id(Integer shop_menu_id) {
        this.shop_menu_id = shop_menu_id;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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
}
