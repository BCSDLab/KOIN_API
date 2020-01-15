package koreatech.in.interceptor;

import koreatech.in.annotation.Auth;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.domain.Authority;
import koreatech.in.domain.ErrorMessage;
import koreatech.in.domain.User.User;
import koreatech.in.exception.ForbiddenException;
import koreatech.in.exception.UnauthorizeException;
import koreatech.in.repository.AuthorityMapper;
import koreatech.in.repository.UserMapper;
import koreatech.in.service.JwtValidator;
import koreatech.in.util.JwtTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JwtValidator jwtValidator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) return true;
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Auth auth = handlerMethod.getMethod().getDeclaringClass().getAnnotation(Auth.class);
        if (auth == null || auth.role() == Auth.Role.NONE) return true; // Auth 어노테이션이 없거나 아무 권한이 필요 없다면

        AuthExcept authExcept = handlerMethod.getMethodAnnotation(AuthExcept.class);
        if (authExcept != null) return true; // AuthExcept 어노테이션이 있다면

        User user = jwtValidator.validate(request.getHeader("Authorization"));
        if (user == null)
            throw new UnauthorizeException(new ErrorMessage("Unauthenticated.", 0));

        if (auth.role() == Auth.Role.ADMIN) {
            // 권한이 등록되지 않은 상태이거나
            if (user.getAuthority() == null || auth.authority() == Auth.Authority.NONE)
                throw new ForbiddenException(new ErrorMessage("permission denied", 0));

            // 필요한 권한이 없다면
            switch (auth.authority()) {
                case USER:
                    if (!user.getAuthority().getGrant_user())
                        throw new ForbiddenException(new ErrorMessage("permission denied", 0));
                    break;

                case CALLVAN:
                    if (!user.getAuthority().getGrant_callvan())
                        throw new ForbiddenException(new ErrorMessage("permission denied", 0));
                    break;

                case LAND:
                    if (!user.getAuthority().getGrant_land())
                        throw new ForbiddenException(new ErrorMessage("permission denied", 0));
                    break;

                case COMMUNITY:
                    if (!user.getAuthority().getGrant_community())
                        throw new ForbiddenException(new ErrorMessage("permission denied", 0));
                    break;

                case SHOP:
                    if (!user.getAuthority().getGrant_shop())
                        throw new ForbiddenException(new ErrorMessage("permission denied", 0));
                    break;

                case VERSION:
                    if (!user.getAuthority().getGrant_version())
                        throw new ForbiddenException(new ErrorMessage("permission denied", 0));
                    break;

                case MARKET:
                    if (!user.getAuthority().getGrant_market())
                        throw new ForbiddenException(new ErrorMessage("permission denied", 0));
                    break;

                case CIRCLE:
                    if (!user.getAuthority().getGrant_circle())
                        throw new ForbiddenException(new ErrorMessage("permission denied", 0));
                    break;

                case LOST:
                    if (!user.getAuthority().getGrant_lost())
                        throw new ForbiddenException(new ErrorMessage("permission denied", 0));
                    break;

                case SURVEY:
                    if (!user.getAuthority().getGrant_survey())
                        throw new ForbiddenException(new ErrorMessage("permission denied", 0));
                    break;

                case BCSDLAB:
                    if (!user.getAuthority().getGrant_bcsdlab())
                        throw new ForbiddenException(new ErrorMessage("permission denied", 0));
                    break;
            }
        }
        return true;
    }
}
