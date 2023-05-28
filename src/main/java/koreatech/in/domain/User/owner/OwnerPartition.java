package koreatech.in.domain.User.owner;

import koreatech.in.domain.Shop.Shop;

import java.util.List;

public class OwnerPartition extends Owner {
    @Override
    public Integer getUser_id() {
        return super.getUser_id();
    }

    @Override
    public void setUser_id(Integer user_id) {
        super.setUser_id(user_id);
    }

    @Override
    public Boolean getGrant_shop() {
        return super.getGrant_shop();
    }

    @Override
    public Boolean getGrant_event() {
        return super.getGrant_event();
    }

    @Override
    public List<Shop> getShops() {
        return super.getShops();
    }

    @Override
    public void setShops(List<Shop> shops) {
        super.setShops(shops);
    }

    @Override
    public void setGrant_shop(Boolean grant_shop) {
        super.setGrant_shop(grant_shop);
    }

    @Override
    public void setGrant_event(Boolean grant_event) {
        super.setGrant_event(grant_event);
    }

    @Override
    public boolean hasRegistrationInformation() {
        return false;
    }
}
