package koreatech.in.domain.BokDuck;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.Gson;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.dto.admin.land.request.UpdateLandRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Land {
    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(hidden = true)
    private String name;

    @ApiModelProperty(hidden = true)
    private String internal_name;

    @ApiModelProperty(hidden = true)
    private Double size;

    @ApiModelProperty(hidden = true)
    private String room_type;

    @ApiModelProperty(hidden = true)
    private Double latitude;

    @ApiModelProperty(hidden = true)
    private Double longitude;

    @ApiModelProperty(hidden = true)
    private String phone;

    @ApiModelProperty(hidden = true)
    private String image_urls;

    @ApiModelProperty(hidden = true)
    private String address;

    @ApiModelProperty(hidden = true)
    private String description;

    @ApiModelProperty(hidden = true)
    private Integer floor;

    @ApiModelProperty(hidden = true)
    private String deposit;

    @ApiModelProperty(hidden = true)
    private String monthly_fee;

    @ApiModelProperty(hidden = true)
    private String charter_fee;

    @ApiModelProperty(hidden = true)
    private String management_fee;

    @ApiModelProperty(hidden = true)
    private Boolean opt_refrigerator;

    @ApiModelProperty(hidden = true)
    private Boolean opt_closet;

    @ApiModelProperty(hidden = true)
    private Boolean opt_tv;

    @ApiModelProperty(hidden = true)
    private Boolean opt_microwave;

    @ApiModelProperty(hidden = true)
    private Boolean opt_gas_range;

    @ApiModelProperty(hidden = true)
    private Boolean opt_induction;

    @ApiModelProperty(hidden = true)
    private Boolean opt_water_purifier;

    @ApiModelProperty(hidden = true)
    private Boolean opt_air_conditioner;

    @ApiModelProperty(hidden = true)
    private Boolean opt_washer;

    @ApiModelProperty(hidden = true)
    private Boolean opt_bed;

    @ApiModelProperty(hidden = true)
    private Boolean opt_desk;

    @ApiModelProperty(hidden = true)
    private Boolean opt_shoe_closet;

    @ApiModelProperty(hidden = true)
    private Boolean opt_electronic_door_locks;

    @ApiModelProperty(hidden = true)
    private Boolean opt_bidet;

    @ApiModelProperty(hidden = true)
    private Boolean opt_veranda;

    @ApiModelProperty(hidden = true)
    private Boolean opt_elevator;

    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;

    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;

    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;

    public void update(UpdateLandRequest request) {
        this.name = request.getName();
        this.internal_name = request.getName().replace(" ","").toLowerCase();
        this.size = request.getSize();
        this.room_type = request.getRoom_type();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.phone = request.getPhone();
        this.image_urls = new Gson().toJson(request.getImage_urls());
        this.address = request.getAddress();
        this.description = request.getDescription();
        this.floor = request.getFloor();
        this.deposit = request.getDeposit();
        this.monthly_fee = request.getMonthly_fee();
        this.charter_fee = request.getCharter_fee();
        this.management_fee = request.getManagement_fee();
        this.opt_refrigerator = request.getOpt_refrigerator();
        this.opt_closet = request.getOpt_closet();
        this.opt_tv = request.getOpt_tv();
        this.opt_microwave = request.getOpt_microwave();
        this.opt_gas_range = request.getOpt_gas_range();
        this.opt_induction = request.getOpt_induction();
        this.opt_water_purifier = request.getOpt_water_purifier();
        this.opt_air_conditioner = request.getOpt_air_conditioner();
        this.opt_washer = request.getOpt_washer();
        this.opt_bed = request.getOpt_bed();
        this.opt_desk = request.getOpt_desk();
        this.opt_shoe_closet = request.getOpt_shoe_closet();
        this.opt_electronic_door_locks = request.getOpt_electronic_door_locks();
        this.opt_bidet = request.getOpt_bidet();
        this.opt_veranda = request.getOpt_veranda();
        this.opt_elevator = request.getOpt_elevator();
    }
    public void init() {
        // create시 입력되지 않은 옵션이 있으면 false로 초기화
        if(this.opt_refrigerator == null) {
            this.opt_refrigerator = false;
        }
        if(this.opt_closet == null) {
            this.opt_closet = false;
        }
        if(this.opt_tv == null) {
            this.opt_tv = false;
        }
        if(this.opt_microwave == null) {
            this.opt_microwave = false;
        }
        if(this.opt_gas_range == null) {
            this.opt_gas_range = false;
        }
        if(this.opt_induction == null) {
            this.opt_induction = false;
        }
        if(this.opt_water_purifier == null) {
            this.opt_water_purifier = false;
        }
        if(this.opt_air_conditioner == null) {
            this.opt_air_conditioner = false;
        }
        if(this.opt_washer == null) {
            this.opt_washer = false;
        }
        if(this.opt_bed == null) {
            this.opt_bed = false;
        }
        if(this.opt_desk == null) {
            this.opt_desk = false;
        }
        if(this.opt_shoe_closet == null) {
            this.opt_shoe_closet = false;
        }
        if(this.opt_electronic_door_locks == null) {
            this.opt_electronic_door_locks = false;
        }
        if(this.opt_bidet == null) {
            this.opt_bidet = false;
        }
        if(this.opt_veranda == null) {
            this.opt_veranda = false;
        }
        if(this.opt_elevator == null) {
            this.opt_elevator = false;
        }
        if(this.is_deleted == null) {
            this.is_deleted = false;
        }
    }

    public boolean hasSameId(Integer id) {
        if (this.id == null || id == null) {
            return false;
        }

        return this.id.equals(id);
    }

    public boolean isSoftDeleted() {
        if (this.is_deleted == null) {
            return false;
        }

        return this.is_deleted.equals(true);
    }
}
