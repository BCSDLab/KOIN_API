package koreatech.in.dto.land.admin.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter @Builder
public class LandResponse {
    private Integer id;
    private String name;
    private Boolean is_deleted;
    private String room_type;
    private String management_fee;
    private Double size;
    private String monthly_fee;
    private String charter_fee;
    private Double latitude;
    private Double longitude;
    private String deposit;
    private Integer floor;
    private String phone;
    private String address;
    private String description;
    private Boolean opt_refrigerator;
    private Boolean opt_closet;
    private Boolean opt_tv;
    private Boolean opt_microwave;
    private Boolean opt_gas_range;
    private Boolean opt_induction;
    private Boolean opt_water_purifier;
    private Boolean opt_air_conditioner;
    private Boolean opt_washer;
    private Boolean opt_bed;
    private Boolean opt_desk;
    private Boolean opt_shoe_closet;
    private Boolean opt_electronic_door_locks;
    private Boolean opt_bidet;
    private Boolean opt_veranda;
    private Boolean opt_elevator;
    private List<String> image_urls;
}
