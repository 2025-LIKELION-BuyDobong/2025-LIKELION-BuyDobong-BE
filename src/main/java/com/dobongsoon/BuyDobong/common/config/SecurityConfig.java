package com.dobongsoon.BuyDobong.common.config;

import com.dobongsoon.BuyDobong.common.jwt.JwtAuthenticationFilter;
import com.dobongsoon.BuyDobong.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/h2-console/**",
                                "/api/auth/**",
                                "/api/sms/**",
                                "/api/auth/**",
                                "/api/consumer/**",
                                "/api/store/*/detail/*"
                        ).permitAll()
                        .anyRequest().authenticated()
                        // 소비자용 상점 상세 조회
                        //.requestMatchers(org.springframework.http.HttpMethod.GET, "/api/store/*/detail").permitAll()
                );

        http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
