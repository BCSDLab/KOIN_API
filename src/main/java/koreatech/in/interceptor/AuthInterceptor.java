package koreatech.in.interceptor;

import static koreatech.in.exception.ExceptionInformation.BAD_ACCESS;
import static koreatech.in.exception.ExceptionInformation.FORBIDDEN;

import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import koreatech.in.annotation.ApiOff;
import koreatech.in.annotation.Auth;
import koreatech.in.annotation.AuthExcept;
import koreatech.in.annotation.AuthTemporary;
import koreatech.in.domain.User.User;
import koreatech.in.domain.User.owner.Owner;
import koreatech.in.exception.BaseException;
import koreatech.in.service.JwtValidator;
import koreatech.in.util.HttpHeaderValue;
import koreatech.in.util.HttpHeaderValueAttacher;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JwtValidator jwtValidator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Method actualMethod = handlerMethod.getMethod();
        Class<?> actualClass = handlerMethod.getMethod().getDeclaringClass();

        /*
            처리 순서

            1. controller 또는 메소드에 @ApiOff가 선언되어 있다면 403 응답
            2. controller에 @Auth가 선언되어 있지 않거나, @Auth의 Role이 NONE이라면 접근 허용
            3. 메소드에 @AuthExcept가 선언되어 있다면 접근 허용

            ------- 4번부터는 권한이 필요한 경우임. -------

            4. 토큰의 변경 or 변조 or 만료시 401 응답
            5. 유저 정보가 조회되지 않으면 401 응답
            6. controller에 선언된 @Auth의 Role 값에 따른 처리
                a. ADMIN 일때
                    - @Auth의 Authority에 해당하는 권한을 유저가 가지고 있지 않다면 403 응답
                    - 그 외에는 접근 허용
                b. USER 일때
                    - 메소드에 @Auth가 선언되어 있을때, @Auth의 Role과 해당 유저의 신원이 같지 않다면 403 응답
                    - 그 외에는 접근 허용
                c. STUDENT 일때
                    - 유저의 신원이 학생이 아니라면 403 응답
                    - 그 외에는 접근 허용
                c. OWNER 일때
                    - 유저의 신원이 사장님이 아니라면 403 응답
                    - 유저의 신원이 사장님일때, @Auth의 Authority에 대한 권한을 해당 사장님이 가지고 있지 않다면 403 응답
                    - 그 외에는 접근 허용
         */

        if (actualClass.isAnnotationPresent(ApiOff.class) || actualMethod.isAnnotationPresent(ApiOff.class)) {
            throw new BaseException(FORBIDDEN);
        }


        Auth auth = actualClass.getAnnotation(Auth.class);
        if (auth == null || auth.role() == Auth.Role.NONE) {
            return true;
        }

        if (actualMethod.isAnnotationPresent(AuthExcept.class)) {
            return true;
        }

        // 아래에서부턴 권한이 필요하다.

        String authorizationHeader = request.getHeader("Authorization");


        if (actualMethod.isAnnotationPresent(AuthTemporary.class)) {
            jwtValidator.validateTemporaryAccessTokenInHeader(authorizationHeader);
        }

        User user = jwtValidator.validate(authorizationHeader);

        if (user == null) {
            throw new BaseException(BAD_ACCESS);
        }

        // 어드민용 controller
        if (auth.role() == Auth.Role.ADMIN) {
            // 어드민 유저가 아니라면
            if (!user.hasAuthority()) {
                throw new BaseException(FORBIDDEN);
            }

            // 어드민용 로그아웃 API라면 authority 상관 없이 접근 가능해야 한다.
            if (request.getRequestURI().equals("/admin/user/logout")) {
                return true;
            }

            // 필요한 권한이 없다면
            switch (auth.authority()) {
                case USER:
                    if (!user.getAuthority().getGrant_user()) {
                        throw new BaseException(FORBIDDEN);
                    }
                    break;

                case CALLVAN:
                    if (!user.getAuthority().getGrant_callvan()) {
                        throw new BaseException(FORBIDDEN);
                    }
                    break;

                case LAND:
                    if (!user.getAuthority().getGrant_land()) {
                        throw new BaseException(FORBIDDEN);
                    }
                    break;

                case COMMUNITY:
                    if (!user.getAuthority().getGrant_community()) {
                        throw new BaseException(FORBIDDEN);
                    }
                    break;

                case SHOP:
                    if (!user.getAuthority().getGrant_shop()) {
                        throw new BaseException(FORBIDDEN);
                    }
                    break;

                case VERSION:
                    if (!user.getAuthority().getGrant_version()) {
                        throw new BaseException(FORBIDDEN);
                    }
                    break;

                case MARKET:
                    if (!user.getAuthority().getGrant_market()) {
                        throw new BaseException(FORBIDDEN);
                    }
                    break;

                case CIRCLE:
                    if (!user.getAuthority().getGrant_circle()) {
                        throw new BaseException(FORBIDDEN);
                    }
                    break;

                case LOST:
                    if (!user.getAuthority().getGrant_lost()) {
                        throw new BaseException(FORBIDDEN);
                    }
                    break;

                case SURVEY:
                    if (!user.getAuthority().getGrant_survey()) {
                        throw new BaseException(FORBIDDEN);
                    }
                    break;

                case BCSDLAB:
                    if (!user.getAuthority().getGrant_bcsdlab()) {
                        throw new BaseException(FORBIDDEN);
                    }
                    break;

                case EVENT:
                    if (!user.getAuthority().getGrant_event()) {
                        throw new BaseException(FORBIDDEN);
                    }
                    break;
            }
        }
        // 일반 유저용(학생, 사장님을 전부 포함) controller일 경우
        else if (auth.role() == Auth.Role.USER) {
            Auth methodAuth = actualMethod.getAnnotation(Auth.class);

            if (methodAuth != null) {
                if ((methodAuth.role() == Auth.Role.STUDENT) && !user.isStudent()) {
                    throw new BaseException(FORBIDDEN);
                }
                if ((methodAuth.role() == Auth.Role.OWNER) && !user.isOwner()) {
                    throw new BaseException(FORBIDDEN);
                }
            }
        }
        // 학생용 controller일 경우
        else if (auth.role() == Auth.Role.STUDENT) {
            if (!user.isStudent()) {
                throw new BaseException(FORBIDDEN);
            }
        }
        // 사장님용 controller일 경우
        else if (auth.role() == Auth.Role.OWNER) {
            if (!user.isOwner()) {
                throw new BaseException(FORBIDDEN);
            }

            Owner owner = (Owner) user;

            if ((auth.authority() == Auth.Authority.SHOP) && !owner.hasGrantedShop()) {
                throw new BaseException(FORBIDDEN);
            }
            if ((auth.authority() == Auth.Authority.EVENT) && !owner.hasGrantedEvent()) {
                throw new BaseException(FORBIDDEN);
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Method actualMethod = handlerMethod.getMethod();

        // view 응답이라면 캐싱 방지 헤더 설정
        if (!actualMethod.isAnnotationPresent(ResponseBody.class) && !actualMethod.getReturnType().equals(ResponseEntity.class)) {
            addHeaderForPreventingCache(response);
        }
    }

    private void addHeaderForPreventingCache(HttpServletResponse response) {
        // 캐싱 방지 (HTTP 1.1)
        response.addHeader(HttpHeaders.CACHE_CONTROL, HttpHeaderValueAttacher.start()
                .attach(HttpHeaderValue.NO_CACHE)
                .attach(HttpHeaderValue.NO_STORE)
                .attach(HttpHeaderValue.MUST_REVALIDATE)
                .end()
        );

        // 캐싱 방지 (HTTP 1.0)
        response.addHeader(HttpHeaders.PRAGMA, HttpHeaderValue.NO_CACHE);

        // 응답의 리소스 만료
        response.addHeader(HttpHeaders.EXPIRES, HttpHeaderValue.ZERO);
    }
}
