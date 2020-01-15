package koreatech.in.domain.Shop;

import java.util.Date;

public class ShopViewLog {
    private Integer id;
    private Integer shop_id;
    private Integer user_id;
    private Date expired_at;
    private String ip;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getShop_id() { return shop_id; }

    public void setShop_id(Integer shop_id) { this.shop_id = shop_id; }

    public Integer getUser_id() { return user_id; }

    public void setUser_id(Integer user_id) { this.user_id = user_id; }

    public Date getExpired_at() { return expired_at; }

    public void setExpired_at(Date expired_at) { this.expired_at = expired_at; }

    public String getIp() { return ip; }

    public void setIp(String ip) { this.ip = ip; }

    public void update(ShopViewLog shopViewLog) {
        if(shopViewLog.shop_id != null) {
            this.shop_id = shopViewLog.shop_id;
        }
        if(shopViewLog.user_id != null) {
            this.user_id = shopViewLog.user_id;
        }
        if(shopViewLog.expired_at != null) {
            this.expired_at = shopViewLog.expired_at;
        }
        if(shopViewLog.ip != null) {
            this.ip = shopViewLog.ip;
        }
    }
}
