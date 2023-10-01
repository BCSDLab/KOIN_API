package koreatech.in.argumentresolver;

import koreatech.in.annotation.Auth;
import koreatech.in.annotation.Login;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
/*
* @Auth-AuthInterceptor을 통해 검증이 이루어진 API 라면,
* @Login User user과 같은 형식으로 사용자 정보를 받아올 수 있다.
* */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return hasLoginAnnotation(methodParameter) && isAuthVerified(methodParameter);
    }

    private boolean hasLoginAnnotation(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(Login.class);
    }

    private boolean isAuthVerified(MethodParameter methodParameter) {
        return methodParameter.getDeclaringClass().isAnnotationPresent(Auth.class) || methodParameter.getMethod()
                .isAnnotationPresent(Auth.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory)
            throws Exception {
        return nativeWebRequest.getAttribute("user", NativeWebRequest.SCOPE_REQUEST);
    }
}
