package koreatech.in.controller.v2.dto.shop.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/*
    id: 상점 메뉴
    name: 메뉴 이름
    is_single: 단일 메뉴 여부
    single_price: 단일메뉴일때의 가격
    option_prices: 옵션메뉴일때의 가격 리스트
    existent_prices: 상점에 존재하는 카테고리
    selected_prices: 메뉴에서 선택된 카테고리
    description: 메뉴 구성 설명
    images: 이미지 리스트
 */

@Getter
public class UpdateShopMenuDTO {
    private Integer id;
    private String name;
    private Boolean is_single;
    private Integer single_price;
    private List<Map<String, Integer>> option_prices;
    private List<String> existent_prices;
    private List<String> selected_prices;
    private String description;
    private List<MultipartFile> images;

    public void init(Integer id, List<MultipartFile> images) {
        this.id = id;
        this.images = images;
    }
}
