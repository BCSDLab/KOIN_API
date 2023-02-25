package koreatech.in.mapstruct;

import java.util.List;
import java.util.stream.Collectors;
import koreatech.in.domain.Shop.Shop;
import koreatech.in.domain.User.Domain;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.LocalParts;
import koreatech.in.domain.User.owner.Attachment;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerInCertification;
import koreatech.in.domain.User.owner.OwnerShopAttachment;
import koreatech.in.domain.User.owner.OwnerShopAttachments;
import koreatech.in.dto.global.AttachmentUrlRequest;
import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyEmailRequest;
import koreatech.in.dto.normal.user.owner.response.OwnerResponse;
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
    EmailAddress toEmailAddress(VerifyEmailRequest verifyEmailRequest);

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

            @Mapping(source = "attachmentUrls", target = "attachments", qualifiedByName = "convertAttachments"),
            @Mapping(source = "companyNumber", target = "company_registration_number"),
    })
    Owner toOwner(OwnerRegisterRequest ownerRegisterRequest);

    @Named("convertAttachments")
    default List<Attachment> convertAttachments(List<AttachmentUrlRequest> companyCertificateAttachmentUrls) {
        return companyCertificateAttachmentUrls.stream().map(
                        attachmentUrlRequest -> Attachment.from(attachmentUrlRequest.getFileUrl())
                )
                .collect(Collectors.toList());
    }

    @Mappings({
            @Mapping(source = ".", target = "attachments", qualifiedByName = "convertAttachment")
    })
    OwnerShopAttachments toOwnerShopAttachments(Owner owner);

    @Named("convertAttachment")
    default List<OwnerShopAttachment> convertAttachment(Owner owner) {
        return owner.getAttachments().stream()
                .map(attachment -> OwnerShopAttachment.of(owner.getId(), attachment.getFileUrl()))
                .collect(Collectors.toList());
    }

    @Mappings({
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "email", target = "email"),

            @Mapping(source = "company_registration_number", target = "companyNumber"),
            @Mapping(source = "attachments", target = "attachments", qualifiedByName = "convertAttachments"),

            @Mapping(source = "shops", target = "shops", qualifiedByName = "convertShops"),
    })
    OwnerResponse toOwnerResponse(Owner owner);

    @Named("convertAttachments")
    default List<OwnerResponse.Attachment> convertAttachmentsForResponse(List<Attachment> attachments) {
        return attachments.stream().map(attachment ->
                OwnerResponse.Attachment
                        .builder()
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
}
