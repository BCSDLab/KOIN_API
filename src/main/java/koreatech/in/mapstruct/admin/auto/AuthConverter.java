package koreatech.in.mapstruct.admin.auto;

import koreatech.in.domain.Auth.LoginResult;
import koreatech.in.domain.Auth.RefreshToken;
import koreatech.in.dto.admin.auth.TokenRefreshRequest;
import koreatech.in.dto.admin.auth.TokenRefreshResponse;
import koreatech.in.dto.admin.user.response.LoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthConverter {
    AuthConverter INSTANCE = Mappers.getMapper(AuthConverter.class);

    LoginResponse toLoginResponse(LoginResult loginResult);

    @Mapping(source = "refreshToken", target = "token")
    RefreshToken toToken(TokenRefreshRequest request);

    @Mapping(source = "newToken", target = "accessToken")
    TokenRefreshResponse toTokenRefreshResponse(String newToken);
}
