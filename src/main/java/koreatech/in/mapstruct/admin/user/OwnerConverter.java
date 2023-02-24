package koreatech.in.mapstruct.admin.user;

import koreatech.in.domain.User.owner.Owner;
import koreatech.in.dto.admin.user.response.NewOwnersResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OwnerConverter {
    OwnerConverter INSTANCE = Mappers.getMapper(OwnerConverter.class);

    NewOwnersResponse.NewOwner toNewOwnersResponse$NewOwner(Owner owner);
}
