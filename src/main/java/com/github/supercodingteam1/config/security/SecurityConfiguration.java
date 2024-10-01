package com.github.supercodingteam1.config.security;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                .requiresChannel(channel->channel
//                        .anyRequest()
//                        .requiresSecure())
                .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless 설정
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api-docs/**","/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**","/swagger-ui/**").permitAll()
                        .requestMatchers("/auth/signup", "/auth/duplicate","/auth/login","/items","/items/**", "/auth/refreshToken").permitAll()
                        .requestMatchers("/sell").hasRole("SELLER")
                        .requestMatchers("/auth/logout","/auth/withdraw", "/cart/**","/mypage/**").authenticated()
                        .anyRequest().denyAll()
                )

                .formLogin(AbstractHttpConfigurer::disable)  // 폼 로그인 활성화(form-data)
                .httpBasic(AbstractHttpConfigurer::disable)  // HTTP Basic 인증 비활성화
                .rememberMe(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers ->headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));  // Remember Me 기능 비활성화
        // 기존 frameOptions().sameOrigin() 설정에 해당하는 부분을 직접 설정
//        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

//        configuration.setAllowedOrigins(List.of("http://127.0.0.1:5173", "http://localhost:5173"));
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedHeaders(Arrays.asList("X-AUTH-TOKEN", "Content-Type", "time-zone", "*"));
//        configuration.setAllowCredentials(true); // token 주고 받을때 필요
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setMaxAge(3600L); //만료 시간

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}