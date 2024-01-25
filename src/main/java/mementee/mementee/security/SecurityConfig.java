package mementee.mementee.security;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable);
        http
                .authorizeHttpRequests(
                        authorize -> authorize
                                //controller api 허용, swagger 허용
                                .requestMatchers("/api/member/signUp","/api/member/signIn","/api/members",
                                        "/api/school/{schoolName}","/api/schools/{keyWord}","/api/schools","/api/member/check", "/login/**",
                                        "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**", "/swagger-resources/**").permitAll()     //나중에 회원가입과 로그인을 제외하고는 인증 필요
                                //글 쓰기만
                                .requestMatchers(HttpMethod.POST, "/api/board").authenticated().anyRequest().permitAll()
                    )
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtFilter(secretKey), UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }
}

