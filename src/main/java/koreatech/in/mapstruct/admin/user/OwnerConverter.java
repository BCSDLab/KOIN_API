package koreatech.in.mapstruct.admin.user;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import koreatech.in.domain.User.owner.Owner;
import koreatech.in.dto.admin.user.response.NewOwnersResponse;
import koreatech.in.dto.admin.user.response.OwnerResponse;

@Mapper
public interface OwnerConverter {
    OwnerConverter INSTANCE = Mappers.getMapper(OwnerConverter.class);

    NewOwnersResponse.NewOwner toNewOwnersResponse$NewOwner(Owner owner);

    @Mappings({
        @Mapping(source = "company_registration_number", target = "companyRegistrationNumber"),
        @Mapping(source = "attachments", target = "attachmentsId", qualifiedByName = "convertAttachments"),
        @Mapping(source = "shops", target = "shopsId", qualifiedByName = "convertShops")
    })
    OwnerResponse toOwnerResponse(Owner owner);

    @Named("convertAttachments")
    default List<Integer> convertAttachmentsId(List<koreatech.in.domain.User.owner.OwnerAttachment> attachmentsId) {
        return attachmentsId.stream()
            .map(attachment -> attachment.getId()).
            collect(Collectors.toList());
    }

    @Named("convertShops")
    default List<Integer> convertShops(List<koreatech.in.domain.Shop.Shop> shopsId) {
        return shopsId.stream()
            .map(shop -> shop.getId()).
            collect(Collectors.toList());
    }
}
