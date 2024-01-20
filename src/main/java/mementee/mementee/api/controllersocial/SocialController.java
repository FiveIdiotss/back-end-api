package mementee.mementee.api.controllersocial;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.controller.emailDTO.EmailVerificationRequest;
import mementee.mementee.api.controller.emailDTO.SendVerificationCodeRequest;
import mementee.mementee.api.service.social.OAuthService;
import mementee.mementee.api.service.social.SchoolEmailVerificationService;
import mementee.mementee.vo.SocialLoginType;
import mementee.mementee.vo.SocialMember;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "소셜 로그인")
@RequestMapping("/login")
public class SocialController {

    private final OAuthService oAuthService;
    private final HttpServletResponse response;
    private final SchoolEmailVerificationService schoolEmailVerificationService;

    @Operation(description = "해당 URL 클릭 시 해당 소셜 로그인 페이지로 이동")
    @GetMapping("/{socialLoginType}")
    public ResponseEntity<String> socialLogin(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) {
        String url = oAuthService.requestAuthorizedURL(socialLoginType);
        return ResponseEntity.ok().body(url);
    }

    @GetMapping("/oauth2/code/{socialLoginType}")
    public SocialMember socialLogins(@PathVariable SocialLoginType socialLoginType, @RequestParam String code) throws JsonProcessingException {
        return oAuthService.oAuthLogin(code, socialLoginType);
    }

    //unicert api key = ce6dc2f8-3d83-44ca-923a-7143022e5f3d
    @Operation(description = "테스트용")
    @GetMapping("/email")
    public String hel(@RequestBody SendVerificationCodeRequest request) throws JsonProcessingException {
        String requestBody = schoolEmailVerificationService.createRequestBody(request);
        return schoolEmailVerificationService.sendEmailVerificationRequest(requestBody);
    }

    @GetMapping("/email/verify")
    public String emailVerification(@RequestBody EmailVerificationRequest request) {
        return schoolEmailVerificationService.requestCertification(request);
    }
}
