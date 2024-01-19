package mementee.mementee.api.controllersocial;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.service.social.OAuthService;
import mementee.mementee.api.service.social.SchoolEmailVerificationService;
import mementee.mementee.vo.SocialLoginType;
import mementee.mementee.vo.SocialMember;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "소셜 로그인")
@RequestMapping("/social")
public class SocialController {

    private final OAuthService oAuthService;
    private final HttpServletResponse response;
    private final SchoolEmailVerificationService schoolEmailVerificationService;

//    @Operation(description = "해당 URL 클릭 시 해당 소셜 로그인 페이지로 이동")
//    @GetMapping("/{socialLoginType}")
//    public void socialLogin(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) throws IOException {
//        String url = oAuthService.requestAuthorizedURL(socialLoginType);
//        response.sendRedirect(url);
//    }

    @Operation(description = "해당 URL 클릭 시 해당 소셜 로그인 페이지로 이동")
    @GetMapping("/{socialLoginType}")
    public ResponseEntity<String> socialLogin(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) throws IOException {
        String url = oAuthService.requestAuthorizedURL(socialLoginType);
        return ResponseEntity.ok().body(url);
    }

    @GetMapping("/oauth2/code/{socialLoginType}")
    public SocialMember socialLogins(@PathVariable SocialLoginType socialLoginType, @RequestParam Map<String, String> params) throws JsonProcessingException, JsonProcessingException {
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
}
