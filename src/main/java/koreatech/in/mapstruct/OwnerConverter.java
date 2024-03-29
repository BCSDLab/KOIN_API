package koreatech.in.mapstruct;

import java.util.List;
import java.util.stream.Collectors;
import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.Shop.ShopMenuCategory;
import koreatech.in.domain.User.Domain;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.LocalParts;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerAttachment;
import koreatech.in.domain.User.owner.OwnerAttachments;
import koreatech.in.domain.User.owner.OwnerInCertification;
import koreatech.in.domain.User.owner.OwnerPartition;
import koreatech.in.domain.User.owner.OwnerShop;
import koreatech.in.dto.global.AttachmentUrlRequest;
import koreatech.in.dto.normal.shop.request.UpdateMenuCategoryRequest;
import koreatech.in.dto.normal.user.owner.request.OwnerChangePasswordRequest;
import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import koreatech.in.dto.normal.user.owner.request.OwnerUpdateRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyEmailRequest;
import koreatech.in.dto.normal.user.owner.response.OwnerResponse;
import koreatech.in.dto.normal.user.owner.response.VerifyCodeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OwnerConverter {

    OwnerConverter INSTANCE = Mappers.getMapper(OwnerConverter.class);

    @Mappings({
            @Mapping(source = "address", target = "localParts", qualifiedByName = "convertLocalParts"),
            @Mapping(source = "address", target = "domain", qualifiedByName = "convertDomain")
    })
    EmailAddress toEmailAddress(String address);

    default EmailAddress toEmailAddress(OwnerChangePasswordRequest ownerChangePasswordRequest) {
        return toEmailAddress(ownerChangePasswordRequest.getAddress());
    }

    default EmailAddress toEmailAddress(VerifyEmailRequest verifyEmailRequest) {
        return toEmailAddress(verifyEmailRequest.getAddress());
    }

    @Named("convertLocalParts")
    default LocalParts convertLocalParts(String address) {
        return LocalParts.from(address);
    }

    @Named("convertDomain")
    default Domain convertDomain(String address) {
        return Domain.from(address);
    }

    @Mappings({
            @Mapping(source = "address", target = "email", qualifiedByName = "convertEmail"),
            @Mapping(source = "certificationCode", target = "certificationCode")
    })
    OwnerInCertification toOwnerInCertification(VerifyCodeRequest verifyCodeRequest);

    @Named("convertEmail")
    default String convertEmail(String address) {
        return EmailAddress.from(address).getEmailAddress();
    }

    @Mappings({
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "phoneNumber", target = "phone_number"),
            @Mapping(source = "attachmentUrls", target = "attachments", qualifiedByName = "convertAttachments"),
            @Mapping(source = "companyNumber", target = "company_registration_number"),
    })
    Owner toOwner(OwnerRegisterRequest ownerRegisterRequest);

    @Mappings({
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "phoneNumber", target = "phone_number"),
    })
    OwnerPartition toOwnerPartition(OwnerRegisterRequest ownerRegisterRequest);

    @Named("convertAttachments")
    default List<OwnerAttachment> convertAttachments(List<AttachmentUrlRequest> attachmentUrls) {
        return attachmentUrls.stream().map(
                        attachmentUrlRequest -> OwnerAttachment.builder().fileUrl(attachmentUrlRequest.getFileUrl()).build()
                )
                .collect(Collectors.toList());
    }

    @Mappings({
            @Mapping(source = ".", target = "attachments", qualifiedByName = "convertAttachments")
    })
    OwnerAttachments toOwnerAttachments(Owner owner);

    @Named("convertAttachments")
    default List<OwnerAttachment> convertAttachments(Owner owner) {
        return owner.getAttachments().stream()
                .map(attachment -> OwnerAttachment.builder().ownerId(owner.getId()).fileUrl(attachment.getFileUrl()).build())
                .collect(Collectors.toList());
    }

    @Mappings({
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "email", target = "email"),

            @Mapping(source = "company_registration_number", target = "companyNumber"),
            @Mapping(source = "attachments", target = "attachments", qualifiedByName = "convertAttachmentsForResponse"),

            @Mapping(source = "shops", target = "shops", qualifiedByName = "convertShops"),
    })
    OwnerResponse toOwnerResponse(Owner owner);

    @Named("convertAttachmentsForResponse")
    default List<OwnerResponse.Attachment> convertAttachmentsForResponse(List<OwnerAttachment> attachments) {
        return attachments.stream().map(attachment ->
                OwnerResponse.Attachment
                        .builder()
                        .id(attachment.getId())
                        .fileUrl(attachment.getFileUrl())
                        .fileName(attachment.fileName())
                        .build())
                .collect(Collectors.toList());
    }

    @Named("convertShops")
    default List<OwnerResponse.Shop> convertShops(List<Shop> shops) {
        return shops.stream().map(shop -> OwnerResponse.Shop.builder()
                .id(shop.getId())
                .name(shop.getName())
                .build()).collect(Collectors.toList());
    }

    @Mappings({
            @Mapping(source = "attachmentUrls", target = "attachments", qualifiedByName = "convertAttachments"),
    })
    Owner toOwner(OwnerUpdateRequest ownerUpdateRequest);

    @Mappings({
            @Mapping(source = "ownerRegisterRequest.shopId", target = "shop_id"),
            @Mapping(source = "ownerRegisterRequest.shopName", target = "shop_name")
    })
    OwnerShop toOwnerShop(OwnerRegisterRequest ownerRegisterRequest);

    @Mapping(source = "temporaryAccessToken", target = "accessToken")
    VerifyCodeResponse toVerifyCodeResponse(String temporaryAccessToken);

    default Owner toNewOwner(OwnerRegisterRequest ownerRegisterRequest) {
        if (ownerRegisterRequest.getCompanyNumber() == null) {
            return toOwnerPartition(ownerRegisterRequest);
        }
        return toOwner(ownerRegisterRequest);
    }

    @Mapping(source = "shopId", target="shop_id")
    ShopMenuCategory toMenuCategory(Integer shopId, UpdateMenuCategoryRequest request);

}
