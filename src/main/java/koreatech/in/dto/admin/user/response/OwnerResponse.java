package koreatech.in.dto.admin.user.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import java.util.List;

@Getter
@SuperBuilder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel("Admin_OwnerResponse")
public class OwnerResponse extends UserResponse{
    @ApiModelProperty(value = "이름 " + "(50자 이내)", example = "정보혁", required = true)
    private String name;

    @ApiModelProperty(notes = "사업자 등록 번호", example = "012-34-56789", required = true)
    private String companyRegistrationNumber;

    @ApiModelProperty(notes = "첨부파일 id 목록")
    @Valid
    private List<Integer> attachmentsId;

    @ApiModelProperty(notes = "가게 id 목록")
    @Valid
    private List<Integer> shopsId;
}
