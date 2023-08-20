package koreatech.in.mapstruct.admin.user;

import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.User.PageInfo;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerIncludingShop;
import koreatech.in.domain.User.owner.OwnerShop;
import koreatech.in.domain.User.student.Student;
import koreatech.in.dto.admin.user.owner.request.OwnerUpdateRequest;
import koreatech.in.dto.admin.user.owner.response.OwnerUpdateResponse;
import koreatech.in.dto.admin.user.owner.response.OwnersResponse;
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

    Owner toOwner(OwnerUpdateRequest owner);

    OwnerUpdateResponse toOwnerUpdateResponse(Owner owner);

    @Mappings({
        @Mapping(source = "owner.company_registration_number", target = "companyRegistrationNumber"),
        @Mapping(source = "attachmentsId", target = "attachmentsId"),
        @Mapping(source = "shopsId", target = "shopsId")
    })
    OwnerResponse toOwnerResponse(Owner owner, List<Integer> shopsId, List<Integer> attachmentsId);

    @Mappings({
        @Mapping(source = "id",target = "id"),
        @Mapping(source = "email",target = "email"),
        @Mapping(source = "name",target = "name"),
        @Mapping(source = "phone_number",target = "phone_number"),
        @Mapping(source = "shop_id",target = "shop_id"),
        @Mapping(source = "shop_name",target = "shop_name"),
        @Mapping(source = "created_at",target = "created_at")
    })
    NewOwnersResponse.NewOwner toNewOwnerResponse$NewOwner(OwnerIncludingShop owner);

    List<NewOwnersResponse.NewOwner> toNewOwnerResponse$NewOwners(List<OwnerIncludingShop> owners);

    @Mappings({
            @Mapping(source = "pageInfo.totalPage", target = "total_page"),
            @Mapping(source = "pageInfo.totalCount", target = "total_count"),
            @Mapping(source = "pageInfo.currentPage", target = "current_page"),
            @Mapping(source = "pageInfo.currentCount", target = "current_count"),
            @Mapping(source = "newOwners", target = "owners")
    })
    NewOwnersResponse toNewOwnersResponse(PageInfo pageInfo, List<NewOwnersResponse.NewOwner> newOwners);
    
    OwnersResponse.Owner toOwnersResponse$Owner(Owner ownerByCondition);

    List<OwnersResponse.Owner> toOwnersResponse$Owners(List<Owner> ownersByCondition);

    @Mappings({
            @Mapping(source = "pageInfo.totalPage", target = "total_page"),
            @Mapping(source = "pageInfo.totalCount", target = "total_count"),
            @Mapping(source = "pageInfo.currentPage", target = "current_page"),
            @Mapping(source = "pageInfo.currentCount", target = "current_count"),
            @Mapping(source = "owners", target = "owners")
    })
    OwnersResponse toOwnersResponse(PageInfo pageInfo, List<OwnersResponse.Owner> owners);
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
