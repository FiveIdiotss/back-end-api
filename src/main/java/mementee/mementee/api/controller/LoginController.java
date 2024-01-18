package mementee.mementee.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.service.socialservice.OAuthService;
import mementee.mementee.service.socialservice.SchoolEmailVerificationService;
import mementee.mementee.vo.SocialLoginType;
import mementee.mementee.vo.SocialMember;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/login")
public class LoginController {

    private final OAuthService oAuthService;
    private final HttpServletResponse response;
    private final SchoolEmailVerificationService schoolEmailVerificationService;

//    @GetMapping("/naver")
//    public void naver() throws IOException {
//        String url = naverLoginService.getAuthorizedURL();
//        response.sendRedirect(url);
//    }
//
//    @GetMapping("/kakao")
//    public void kakao() throws IOException {
//        String url = kakaoLoginService.getAuthorizedURL();
//        response.sendRedirect(url);
//    }

    @GetMapping("/{socialLoginType}")
    public void socialLogin(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) throws IOException {
        String url = oAuthService.requestAuthorizedURL(socialLoginType);
        response.sendRedirect(url);
    }

//    @GetMapping("/oauth2/code/naver")
//    public SocialMember naverLogin(@RequestParam Map<String, String> params) throws JsonProcessingException {
//        SocialToken socialToken = naverLoginService.requestLoginToken(params);
//        String userInfoJsonData = naverLoginService.requestUserInfo(socialToken);
//        return naverLoginService.createSocialMember(userInfoJsonData, SocialLoginType.NAVER);
//    }
//
//    @GetMapping("/oauth2/code/kakao")
//    public SocialMember kakaoLogin(@RequestParam Map<String, String> params) throws JsonProcessingException {
//        SocialToken socialToken = kakaoLoginService.requestLoginToken(params);
//        String userInfoJsonData = kakaoLoginService.requestUserInfo(socialToken);
//        return kakaoLoginService.createSocialMember(userInfoJsonData, SocialLoginType.KAKAO);
//    }

    @GetMapping("/oauth2/code/{socialLoginType}")
    public SocialMember socialLogins(@PathVariable SocialLoginType socialLoginType, @RequestParam Map<String, String> params) throws JsonProcessingException {
        return oAuthService.oAuthLogin(params, socialLoginType);
    }

    //unicer api key = ce6dc2f8-3d83-44ca-923a-7143022e5f3d
    @GetMapping
    @RequestMapping("/email")
    public String hel() {
        return schoolEmailVerificationService.sendEmailVerificationRequest();
    }

    @GetMapping
    @RequestMapping("/email/{code}")
    public String emailVerification(@PathVariable String code) {
        return schoolEmailVerificationService.requestCertification(code);
    }


    //    @GetMapping("/{socialLoginType}")
//    public void socialLogin(@PathVariable SocialLoginType socialLoginType) {
//
//    }
}
