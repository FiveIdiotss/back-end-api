//package com.team.mementee.session;
//
//import com.team.mementee.exception.unauthorized.InvalidTokenException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.MethodParameter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class TokenArgumentResolver implements HandlerMethodArgumentResolver {
//
//    private final ServerSessionService serverSessionService;
//
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//        // 파라미터에 @Token 어노테이션이 있을 때만 동작하도록 설정
//        return parameter.hasParameterAnnotation(Token.class);
//    }
//
//    @Override
//    public String resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//        log.info("Resolving token");
//
//        // HttpServletRequest에서 토큰을 가져와 파라미터에 주입
//        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
//        HttpSession session = request.getSession();
//        if (session != null) return serverSessionService.get(session);
//
//        // 세션이 null인 경우 예외를 던짐
//        throw new InvalidTokenException();
//    }
//
//}
