package koreatech.in.mapstruct.normal.auto;

import koreatech.in.domain.Auth.LoginResult;
import koreatech.in.dto.normal.user.response.LoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthConverter {
    AuthConverter INSTANCE = Mappers.getMapper(AuthConverter.class);

    LoginResponse toLoginResponse(LoginResult loginResult);

}
