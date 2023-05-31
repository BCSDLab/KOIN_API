package koreatech.in.mapstruct.admin.user;

import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.user.owner.request.OwnerUpdateRequest;
import koreatech.in.dto.admin.user.owner.response.OwnerUpdateResponse;
import koreatech.in.dto.admin.user.response.NewOwnersResponse;
import koreatech.in.dto.admin.user.response.OwnerResponse;
import koreatech.in.dto.admin.user.student.request.StudentUpdateRequest;
import koreatech.in.dto.admin.user.student.response.StudentUpdateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface OwnerConverter {
    OwnerConverter INSTANCE = Mappers.getMapper(OwnerConverter.class);

    NewOwnersResponse.NewOwner toNewOwnersResponse$NewOwner(Owner owner);

    Owner toOwner(OwnerUpdateRequest owner);

    OwnerUpdateResponse toOwnerUpdateResponse(Owner owner);

    @Mappings({
        @Mapping(source = "owner.company_registration_number", target = "companyRegistrationNumber"),
        @Mapping(source = "attachmentsId", target = "attachmentsId"),
        @Mapping(source = "shopsId", target = "shopsId")
    })
    OwnerResponse toOwnerResponse(Owner owner, List<Integer> shopsId, List<Integer> attachmentsId);
/*
    @Named("convertAttachments")
    default List<Integer> convertAttachments(List<Integer> attachmentsId) {
        return attachments.stream()
            .map(OwnerAttachment::getId)
            .collect(Collectors.toList());
    }

    @Named("convertShops")
    default List<Integer> convertShops(List<Integer> shopsId) {
        return shops.stream()
            .map(Shop::getId)
            .collect(Collectors.toList());
    }*/
}
