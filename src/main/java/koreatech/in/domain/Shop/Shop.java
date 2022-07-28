package koreatech.in.domain.Shop;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import koreatech.in.annotation.ValidationGroups;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.*;

@NoArgsConstructor
public class Shop {
    @ApiModelProperty(notes = "고유 id", example = "10")
    private Integer id;
    @Size(max = 50, message = "가게 이름은 50자 이내여야 합니다.")
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "가게 이름은 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "가게 이름", example = "써니 숯불 도시락")
    private String name;
    @ApiModelProperty(hidden = true)
    private String internal_name;
    @ApiModelProperty(notes = "가게 이름 앞자리 1글자의 초성", example = "ㅆ")
    private String chosung;
    @NotNull(groups = ValidationGroups.CreateAdmin.class, message = "가게 카테고리는 비워둘 수 없습니다.")
    @ApiModelProperty(notes = "기타(S000), 콜벤(S001), 정식(S002), 족발(S003), 중국집(S004), 치킨(S005), 피자(S006), 탕수육(S007), 일반(S008), 미용실(S009)", example = "S001")
    private String category;
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}", message = "전화번호 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "전화번호", example = "010-0000-0000")
    private String phone;
    @Pattern(regexp = "^[0-9]{2}:[0-9]{2}", message = "시간 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "오픈 시간", example = "11:00")
    private String open_time;
    @Pattern(regexp = "^[0-9]{2}:[0-9]{2}", message = "시간 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "마감 시간", example = "23:00")
    private String close_time;
    @Pattern(regexp = "^[0-9]{2}:[0-9]{2}", message = "시간 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "마감 시간", example = "23:00")
    private String weekend_open_time;
    @Pattern(regexp = "^[0-9]{2}:[0-9]{2}", message = "시간 형식이 올바르지 않습니다.")
    @ApiModelProperty(notes = "마감 시간", example = "23:00")
    private String weekend_close_time;

    @ApiModelProperty(notes = "이미지 링크, string으로 받아서 저장 후 json 으로 반환, key와 value는 큰따옴표로 감싸야 함", example = "[ A : B ]")
    private String image_urls;
    @ApiModelProperty(notes = "주소", example = "한국 어딘가")
    private String address;
    @ApiModelProperty(notes = "세부사항", example = "세부사항입니다.")
    private String description;
    @ApiModelProperty(notes = "배달 가능 여부", example = "false")
    private Boolean delivery;
    @Max(value = Integer.MAX_VALUE, message = "입력할 수 없는 가격입니다.")
    @Min(value = 0, message = "입력할 수 없는 가격입니다.")
    @ApiModelProperty(notes = "배달 금액", example = "20000")
    private Integer delivery_price = 0;
    @ApiModelProperty(notes = "카드 가능 여부", example = "false")
    private Boolean pay_card;
    @ApiModelProperty(notes = "계좌이체 가능 여부", example = "false")
    private Boolean pay_bank;
    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date created_at;
    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updated_at;
    @ApiModelProperty(notes = "이벤트 진행 여부", example = "false")
    private Boolean is_event;
    @ApiModelProperty(notes = "이벤트 상세내용 등 부가내용", example = "비고.")
    private String remarks;
    @ApiModelProperty(notes = "조회수", example = "10")
    private Integer hit;
    @ApiModelProperty(notes = "고유링크", example = "http://url.com")
    private String permalink;

    private static List<String> validCategory = new ArrayList<String>() {{
        add("S000");
        add("S001");
        add("S002");
        add("S003");
        add("S004");
        add("S005");
        add("S006");
        add("S007");
        add("S008");
        add("S009");
        add("S010");
    }};

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

    public String getChosung() {
        return chosung;
    }

    public void setChosung(String chosung) {
        this.chosung = chosung;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpen_time() {
        return open_time;
    }

    public void setOpen_time(String open_time) {
        this.open_time = open_time;
    }

    public String getClose_time() {
        return close_time;
    }

    public void setClose_time(String close_time) {
        this.close_time = close_time;
    }

    public String getWeekend_open_time() {
        return weekend_open_time;
    }

    public void setWeekend_open_time(String weekend_open_time) {
        this.weekend_open_time = weekend_open_time;
    }

    public String getWeekend_close_time() {
        return weekend_close_time;
    }

    public void setWeekend_close_time(String weekend_close_time) {
        this.weekend_close_time = weekend_close_time;
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

    public Boolean getDelivery() {
        return delivery;
    }

    public void setDelivery(Boolean delivery) {
        this.delivery = delivery;
    }

    public Integer getDelivery_price() {
        return delivery_price;
    }

    public void setDelivery_price(Integer delivery_price) {
        this.delivery_price = delivery_price;
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

    public Boolean getIs_event() {
        return is_event;
    }

    public void setIs_event(Boolean is_event) {
        this.is_event = is_event;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getHit() { return hit; }

    public void setHit(Integer hit) { this.hit = hit; }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public Boolean contain(String category) {
        return validCategory.contains(category);
    }

    public void update(Shop shop) {
        //TODO: 현재 방식 update 문제점, 멤버변수를 특정값으로 초기화했을경우 update시 무조건 초기값 대입
        if(shop.name != null) {
            this.name = shop.name;
        }
        if(shop.internal_name != null) {
            this.internal_name = shop.internal_name;
        }
        if(shop.chosung != null) {
            this.chosung = shop.chosung;
        }
        if(shop.category != null) {
            this.category = shop.category;
        }
        if(shop.phone != null) {
            this.phone = shop.phone;
        }
        if(shop.open_time != null) {
            this.open_time = shop.open_time;
        }
        if(shop.close_time != null) {
            this.close_time = shop.close_time;
        }
        if(shop.image_urls != null) {
            this.image_urls = shop.image_urls;
        }
        if(shop.address != null) {
            this.address = shop.address;
        }
        if(shop.description != null) {
            this.description = shop.description;
        }
        if(shop.delivery != null) {
            this.delivery = shop.delivery;
        }
        if(shop.delivery_price != null) {
            this.delivery_price = shop.delivery_price;
        }
        if(shop.pay_card != null) {
            this.pay_card = shop.pay_card;
        }
        if(shop.pay_bank != null) {
            this.pay_bank = shop.pay_bank;
        }
        if(shop.is_deleted != null) {
            this.is_deleted = shop.is_deleted;
        }
        if(shop.is_event != null) {
            this.is_event = shop.is_event;
        }
        if(shop.remarks != null) {
            this.remarks = shop.remarks;
        }
        if(shop.permalink != null) {
            this.permalink = shop.permalink;
        }
    }

    @Builder
    public Shop(String name, String category, String phone, String open_time, String close_time, String weekend_open_time, String weekend_close_time, String image_urls, String address, String description, Boolean delivery, Integer delivery_price, Boolean pay_card, Boolean pay_bank, Boolean is_event, String remarks, Boolean is_deleted) {
        this.name = name;
        this.category = category;
        this.phone = phone;
        this.open_time = open_time;
        this.close_time = close_time;
        this.weekend_open_time = weekend_open_time;
        this.weekend_close_time = weekend_close_time;
        this.image_urls = image_urls;
        this.address = address;
        this.description = description;
        this.delivery = delivery;
        this.delivery_price = delivery_price;
        this.pay_card = pay_card;
        this.pay_bank = pay_bank;
        this.is_event = is_event;
        this.remarks = remarks;
        this.is_deleted = is_deleted;
    }
}
