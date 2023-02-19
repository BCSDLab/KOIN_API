package koreatech.in.mapstruct;

import koreatech.in.domain.User.AuthResult;
import koreatech.in.domain.User.AuthToken;
import koreatech.in.domain.User.Domain;
import koreatech.in.domain.User.EmailAddress;
import koreatech.in.domain.User.LocalParts;
import koreatech.in.dto.normal.user.request.AuthTokenRequest;
import koreatech.in.dto.normal.user.request.CheckExistsEmailRequest;
import koreatech.in.dto.normal.user.response.AuthResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);


    @Mappings({
            @Mapping(source = "address", target = "localParts", qualifiedByName = "convertLocalParts"),
            @Mapping(source = "address", target = "domain", qualifiedByName = "convertDomain")
    })
    EmailAddress toEmailAddress(CheckExistsEmailRequest checkExistsEmailRequest);

    @Named("convertLocalParts")
    default LocalParts convertLocalParts(String address) {
        return LocalParts.from(address);
    }

    @Named("convertDomain")
    default Domain convertDomain(String address) {
        return Domain.from(address);
    }

    @Mapping(source = "token", target = "token")
    AuthToken toAuthToken(AuthTokenRequest token);

    @Mappings({
            @Mapping(target = "isSuccess", expression = "java(authResult.isSuccess())"),
            @Mapping(source = "errorMessage", target = "errorMessage"),
    })
    AuthResponse toAuthResponse(AuthResult authResult);
}
