package koreatech.in.domain.BokDuck;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;

import javax.validation.constraints.*;
import java.util.Date;

public class Land {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @Size(max = 255, message = "건물 이름은 255자 이내여야 합니다.")
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "건물 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "건물 이름", example = "라이프")
    private String name;
    @ApiModelProperty(hidden = true)
    private String internal_name;
    @ApiModelProperty(notes = "방 크기", example = "3.5")
    private Double size;
    @ApiModelProperty(notes = "원룸 종류", example = "원룸")
    private String room_type;
    @ApiModelProperty(notes = "위도", example = "42.12345")
    private Double latitude;
    @ApiModelProperty(notes = "경도", example = "39.12354")
    private Double longitude;
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "전화번호", example = "010-0000-0000")
    private String phone;
    @ApiModelProperty(notes = "이미지 링크, string으로 받아서 저장 후 json 으로 반환", example = "['http://url.com', 'http://url.com']")
    private String image_urls;
    @ApiModelProperty(notes = "주소", example = "병천면~~")
    private String address;
    @ApiModelProperty(notes = "세부사항", example = "세부사항입니다.")
    private String description;
    @Max(value = Integer.MAX_VALUE, message = "입력할 수 없는 층수입니다.")
    @Min(value = 0, message = "입력할 수 없는 층수입니다.")
    @ApiModelProperty(notes = "층수", example = "4")
    private Integer floor;
    @Size(max = 255, message = "보증금은 255자 이내여야 합니다.")
    @ApiModelProperty(notes = "보증금", example = "35.5")
    private String deposit;
    @Size(max = 255, message = "월세는 255자 이내여야 합니다.")
    @ApiModelProperty(notes = "월세", example = "170(6개월)")
    private String monthly_fee;
    @Size(max = 255, message = "전세는 255자 이내여야 합니다.")
    @ApiModelProperty(notes = "전세", example = "43.2")
    private String charter_fee;
    @Size(max = 255, message = "관리비는 255자 이내여야 합니다.")
    @ApiModelProperty(notes = "관리비", example = "21(1인)/22(2인)/23(3인)")
    private String management_fee;
    @ApiModelProperty(notes = "냉장고 보유 여부", example = "1")
    private Boolean opt_refrigerator;
    @ApiModelProperty(notes = "옷장 보유 여부", example = "1")
    private Boolean opt_closet;
    @ApiModelProperty(notes = "tv 보유 여부", example = "1")
    private Boolean opt_tv;
    @ApiModelProperty(notes = "전자레인지 보유 여부", example = "1")
    private Boolean opt_microwave;
    @ApiModelProperty(notes = "가스레인지 보유 여부", example = "1")
    private Boolean opt_gas_range;
    @ApiModelProperty(notes = "인덕션 보유 여부", example = "1")
    private Boolean opt_induction;
    @ApiModelProperty(notes = "정수기 보유 여부", example = "1")
    private Boolean opt_water_purifier;
    @ApiModelProperty(notes = "에어컨 보유 여부", example = "1")
    private Boolean opt_air_conditioner;
    @ApiModelProperty(notes = "샤워기 보유 여부", example = "1")
    private Boolean opt_washer;
    @ApiModelProperty(notes = "침대 보유 여부", example = "1")
    private Boolean opt_bed;
    @ApiModelProperty(notes = "책상 보유 여부", example = "1")
    private Boolean opt_desk;
    @ApiModelProperty(notes = "신발장 보유 여부", example = "1")
    private Boolean opt_shoe_closet;
    @ApiModelProperty(notes = "전자 도어락 보유 여부", example = "1")
    private Boolean opt_electronic_door_locks;
    @ApiModelProperty(notes = "비데 보유 여부", example = "1")
    private Boolean opt_bidet;
    @ApiModelProperty(notes = "베란다 보유 여부", example = "1")
    private Boolean opt_veranda;
    @ApiModelProperty(notes = "엘레베이터 보유 여부", example = "1")
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

    public void update(Land land) {
        //TODO: 현재 방식 update 문제점, 멤버변수를 특정값으로 초기화했을경우 update시 무조건 초기값 대입
        if(land.name != null) {
            this.name = land.name;
        }
        if(land.internal_name != null) {
            this.internal_name = land.internal_name;
        }
        if(land.size != null) {
            this.size = land.size;
        }
        if(land.room_type != null) {
            this.room_type = land.room_type;
        }
        if(land.latitude != null) {
            this.latitude = land.latitude;
        }
        if(land.longitude != null) {
            this.longitude = land.longitude;
        }
        if(land.phone != null) {
            this.phone = land.phone;
        }
        if(land.phone != null) {
            this.phone = land.phone;
        }
        if(land.image_urls != null) {
            this.image_urls = land.image_urls;
        }
        if(land.address != null) {
            this.address = land.address;
        }
        if(land.description != null) {
            this.description = land.description;
        }
        if(land.floor != null) {
            this.floor = land.floor;
        }
        if(land.deposit != null) {
            this.deposit = land.deposit;
        }
        if(land.monthly_fee != null) {
            this.monthly_fee = land.monthly_fee;
        }
        if(land.charter_fee != null) {
            this.charter_fee = land.charter_fee;
        }
        if(land.management_fee != null) {
            this.management_fee = land.management_fee;
        }
        if(land.opt_refrigerator != null) {
            this.opt_refrigerator = land.opt_refrigerator;
        }
        if(land.opt_closet != null) {
            this.opt_closet = land.opt_closet;
        }
        if(land.opt_tv != null) {
            this.opt_tv = land.opt_tv;
        }
        if(land.opt_microwave != null) {
            this.opt_microwave = land.opt_microwave;
        }
        if(land.opt_gas_range != null) {
            this.opt_gas_range = land.opt_gas_range;
        }
        if(land.opt_induction != null) {
            this.opt_induction = land.opt_induction;
        }
        if(land.opt_water_purifier != null) {
            this.opt_water_purifier = land.opt_water_purifier;
        }
        if(land.opt_air_conditioner != null) {
            this.opt_air_conditioner = land.opt_air_conditioner;
        }
        if(land.opt_washer != null) {
            this.opt_washer = land.opt_washer;
        }
        if(land.opt_bed != null) {
            this.opt_bed = land.opt_bed;
        }
        if(land.opt_desk != null) {
            this.opt_desk = land.opt_desk;
        }
        if(land.opt_shoe_closet != null) {
            this.opt_shoe_closet = land.opt_shoe_closet;
        }
        if(land.opt_electronic_door_locks != null) {
            this.opt_electronic_door_locks = land.opt_electronic_door_locks;
        }
        if(land.opt_bidet != null) {
            this.opt_bidet = land.opt_bidet;
        }
        if(land.opt_veranda != null) {
            this.opt_veranda = land.opt_veranda;
        }
        if(land.opt_elevator != null) {
            this.opt_elevator = land.opt_elevator;
        }
        if(land.is_deleted != null) {
            this.is_deleted = land.is_deleted;
        }
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
}
