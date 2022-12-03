package koreatech.in.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
@NoArgsConstructor
public class SuccessResponse {
    private final Boolean success = true;
}
