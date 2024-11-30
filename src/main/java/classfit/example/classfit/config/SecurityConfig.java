package classfit.example.classfit.config;

import classfit.example.classfit.auth.filter.CustomLoginFilter;
import classfit.example.classfit.auth.filter.JWTFilter;
import classfit.example.classfit.auth.jwt.CustomAuthenticationEntryPoint;
import classfit.example.classfit.auth.jwt.JWTUtil;
import classfit.example.classfit.auth.service.CustomUserDetailService;
import classfit.example.classfit.common.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final RedisUtil redisUtil;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
        security
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        security
            .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()  // 스웨거 관련 엔드포인트 허용
                .requestMatchers("/api/v1/login", "/api/v1/sign_in", "/api/v1/reissue", "/api/v1/mail/send", "/api/v1/mail/verify").permitAll()
                .requestMatchers("/api/v1/academy/code", "/api/v1/academy/register").permitAll()
                .requestMatchers("/api/v1/**").hasAnyRole("MEMBER", "ADMIN")
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated());

        security
            .addFilterBefore(new JWTFilter(customUserDetailService, jwtUtil), CustomLoginFilter.class)
            .addFilterAt(new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, redisUtil), UsernamePasswordAuthenticationFilter.class)

            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(customAuthenticationEntryPoint)
            );

        return security.build();
    }

}
