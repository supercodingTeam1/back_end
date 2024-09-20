package com.github.supercodingteam1.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless 설정
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api-docs/**","/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**","/swagger-ui/**").permitAll()
//                        .requestMatchers("/auth/signup", "/auth/duplicate","/auth/login","/items/**").permitAll()
//                        .requestMatchers("/sell").hasRole("SELLER")
//                        .requestMatchers("/auth/logout","/auth/withdraw", "/cart/**","/mypage/**").authenticated()
//                        .anyRequest().denyAll()
//                )
                .authorizeHttpRequests(authorize->authorize
                        .anyRequest().permitAll())
                .formLogin(form -> form.permitAll())  // 폼 로그인 활성화(form-data)
                .httpBasic(httpBasic -> httpBasic.disable())  // HTTP Basic 인증 비활성화
                .rememberMe(rememberMe -> rememberMe.disable());  // Remember Me 기능 비활성화

        // 기존 frameOptions().sameOrigin() 설정에 해당하는 부분을 직접 설정
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //임시적으로 CORS 문제 해결
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:63342","http://localhost:3000", "https://notice-board-fe.pages.dev"));

        configuration.setAllowCredentials(true); // token 주고 받을때 필요
        configuration.addExposedHeader("Authorization"); //token

        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        configuration.setMaxAge(3600L); //만료 시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}