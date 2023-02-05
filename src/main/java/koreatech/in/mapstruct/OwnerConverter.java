package koreatech.in.mapstruct;

import java.util.List;
import java.util.stream.Collectors;
import koreatech.in.domain.User.owner.Domain;
import koreatech.in.domain.User.owner.EmailAddress;
import koreatech.in.domain.User.owner.LocalParts;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.domain.User.owner.OwnerInCertification;
import koreatech.in.dto.global.AttachmentUrlRequest;
import koreatech.in.dto.normal.user.owner.request.OwnerRegisterRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyCodeRequest;
import koreatech.in.dto.normal.user.owner.request.VerifyEmailRequest;
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
            @Mapping(source = "companyCertificateAttachmentUrls", target = "email", qualifiedByName = "convertAttachments"),
            @Mapping(source = "companyNumber", target = "company_registration_number"),
            @Mapping(source = "account", target = "account"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "name", target = "name")
    })
    Owner toOwner(OwnerRegisterRequest ownerRegisterRequest);

    @Named("convertAttachments")
    default List<String> convertAttachments(List<AttachmentUrlRequest> companyCertificateAttachmentUrls) {
        return companyCertificateAttachmentUrls.stream().map(AttachmentUrlRequest::getFileUrl).collect(Collectors.toList());
    }

}
