package koreatech.in.domain.BokDuck;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.Gson;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.dto.land.admin.request.CreateLandRequest;
import koreatech.in.dto.land.admin.request.UpdateLandRequest;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
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

    public String getInternal_name() {
        return internal_name;
    }

    public void setInternal_name(String internal_name) {
        this.internal_name = internal_name;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage_urls() {
        return image_urls;
    }

    public void setImage_urls(String image_urls) {
        this.image_urls = image_urls;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getMonthly_fee() {
        return monthly_fee;
    }

    public void setMonthly_fee(String monthly_fee) {
        this.monthly_fee = monthly_fee;
    }

    public String getCharter_fee() {
        return charter_fee;
    }

    public void setCharter_fee(String charter_fee) {
        this.charter_fee = charter_fee;
    }

    public String getManagement_fee() {
        return management_fee;
    }

    public void setManagement_fee(String management_fee) {
        this.management_fee = management_fee;
    }

    public Boolean getOpt_refrigerator() {
        return opt_refrigerator;
    }

    public void setOpt_refrigerator(Boolean opt_refrigerator) {
        this.opt_refrigerator = opt_refrigerator;
    }

    public Boolean getOpt_closet() {
        return opt_closet;
    }

    public void setOpt_closet(Boolean opt_closet) {
        this.opt_closet = opt_closet;
    }

    public Boolean getOpt_tv() {
        return opt_tv;
    }

    public void setOpt_tv(Boolean opt_tv) {
        this.opt_tv = opt_tv;
    }

    public Boolean getOpt_microwave() {
        return opt_microwave;
    }

    public void setOpt_microwave(Boolean opt_microwave) {
        this.opt_microwave = opt_microwave;
    }

    public Boolean getOpt_gas_range() {
        return opt_gas_range;
    }

    public void setOpt_gas_range(Boolean opt_gas_range) {
        this.opt_gas_range = opt_gas_range;
    }

    public Boolean getOpt_induction() {
        return opt_induction;
    }

    public void setOpt_induction(Boolean opt_induction) {
        this.opt_induction = opt_induction;
    }

    public Boolean getOpt_water_purifier() {
        return opt_water_purifier;
    }

    public void setOpt_water_purifier(Boolean opt_water_purifier) {
        this.opt_water_purifier = opt_water_purifier;
    }

    public Boolean getOpt_air_conditioner() {
        return opt_air_conditioner;
    }

    public void setOpt_air_conditioner(Boolean opt_air_conditioner) {
        this.opt_air_conditioner = opt_air_conditioner;
    }

    public Boolean getOpt_washer() {
        return opt_washer;
    }

    public void setOpt_washer(Boolean opt_washer) {
        this.opt_washer = opt_washer;
    }

    public Boolean getOpt_bed() {
        return opt_bed;
    }

    public void setOpt_bed(Boolean opt_bed) {
        this.opt_bed = opt_bed;
    }

    public Boolean getOpt_desk() {
        return opt_desk;
    }

    public void setOpt_desk(Boolean opt_desk) {
        this.opt_desk = opt_desk;
    }

    public Boolean getOpt_shoe_closet() {
        return opt_shoe_closet;
    }

    public void setOpt_shoe_closet(Boolean opt_shoe_closet) {
        this.opt_shoe_closet = opt_shoe_closet;
    }

    public Boolean getOpt_electronic_door_locks() {
        return opt_electronic_door_locks;
    }

    public void setOpt_electronic_door_locks(Boolean opt_electronic_door_locks) {
        this.opt_electronic_door_locks = opt_electronic_door_locks;
    }

    public Boolean getOpt_bidet() {
        return opt_bidet;
    }

    public void setOpt_bidet(Boolean opt_bidet) {
        this.opt_bidet = opt_bidet;
    }

    public Boolean getOpt_veranda() {
        return opt_veranda;
    }

    public void setOpt_veranda(Boolean opt_veranda) {
        this.opt_veranda = opt_veranda;
    }

    public Boolean getOpt_elevator() {
        return opt_elevator;
    }

    public void setOpt_elevator(Boolean opt_elevator) {
        this.opt_elevator = opt_elevator;
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

    public Land(CreateLandRequest request) {
        this.name = request.getName();
        this.internal_name = request.getName().replace(" ","").toLowerCase();
        this.size = request.getSize();
        this.room_type = request.getRoom_type();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.phone = request.getPhone();
        if (!request.getImage_urls().isEmpty()) {
            this.image_urls = new Gson().toJson(request.getImage_urls());
        }
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

    public void update(UpdateLandRequest request) {
        this.name = request.getName();
        this.internal_name = request.getName().replace(" ","").toLowerCase();
        this.size = request.getSize();
        this.room_type = request.getRoom_type();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.phone = request.getPhone();
        if (!request.getImage_urls().isEmpty()) {
            this.image_urls = new Gson().toJson(request.getImage_urls());
        }
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
}
