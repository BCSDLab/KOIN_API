package koreatech.in.dto.shop.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
@NoArgsConstructor
public class ResponseSuccessfulDTO {
    private final Boolean success = true;
}
