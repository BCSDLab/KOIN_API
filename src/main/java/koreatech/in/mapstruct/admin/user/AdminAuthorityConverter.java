package koreatech.in.mapstruct.admin.user;

import koreatech.in.domain.Authority;
import koreatech.in.dto.admin.user.request.CreateAuthorityRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminAuthorityConverter {
    AdminAuthorityConverter INSTANCE = Mappers.getMapper(AdminAuthorityConverter.class);

    Authority toAuthority(CreateAuthorityRequest request);
}
