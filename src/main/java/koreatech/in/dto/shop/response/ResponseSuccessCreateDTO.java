package koreatech.in.dto.shop.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
public class ResponseSuccessCreateDTO extends ResponseSuccessfulDTO {
    private Integer id;
}
