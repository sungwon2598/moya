package com.study.moya.global.config.security;

import com.study.moya.auth.jwt.JwtAuthenticationFilter;
import com.study.moya.auth.jwt.JwtAuthorizationFilter;
import com.study.moya.auth.jwt.JwtTokenProvider;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationConfiguration authenticationConfiguration;

//    @Value("${cors.allowed-origins}")
//    private List<String> allowedOrigins;
//
//    @Value("${cors.allowed-methods}")
//    private List<String> allowedMethods;
//
//    @Value("${cors.allowed-headers}")
//    private List<String> allowedHeaders;
//
//    @Value("${cors.allow-credentials}")
//    private boolean allowCredentials;
//
//    @Value("${cors.max-age}")
//    private long maxAge;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/**","/api/auth/**", "/css/**", "/js/**", "/*.ico", "/dashboard",
                                "/webjars/**", "/swagger-ui.html", "/swagger-ui/**", "/actuator/**",
                                "/mermaid/**", "/api/mermaid/**",
                                "/api-docs/**", "/v3/api-docs/**", "/result", "/",
                                "/video", "/signal", "/vid", // 추가된 부분
                                "/error", "/roadmap/**", // 에러 페이지도 허용
                                "/whiteboard", " /api/auth/oauth/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration),
                                jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                List.of(
                        "https://www.moyastudy.com",
                        "https://moyastudy.com",
                        "http://localhost:5173",
                        "http://localhost:4173",
                        "https://api.moyastudy.com",
                        "http://localhost:3000",
                        "http://localhost:8000",
                        "http://localhost:8080"
                )
        );
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}