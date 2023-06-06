package koreatech.in.mapstruct.normal.auto;

import koreatech.in.domain.Auth.LoginResult;
import koreatech.in.domain.Auth.RefreshResult;
import koreatech.in.domain.Auth.RefreshToken;
import koreatech.in.dto.normal.auth.TokenRefreshResponse;
import koreatech.in.dto.normal.auth.TokenRefreshRequest;
import koreatech.in.dto.normal.user.response.LoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthConverter {
    AuthConverter INSTANCE = Mappers.getMapper(AuthConverter.class);

    LoginResponse toLoginResponse(LoginResult loginResult);

    @Mapping(source = "refreshToken", target = "token")
    RefreshToken toToken(TokenRefreshRequest request);

    TokenRefreshResponse toTokenRefreshResponse(RefreshResult refreshResult);
}
