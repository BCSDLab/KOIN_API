package koreatech.in.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
public class SuccessCreateResponse extends SuccessResponse {
    private Integer id; // 생성된 레코드 id
}
