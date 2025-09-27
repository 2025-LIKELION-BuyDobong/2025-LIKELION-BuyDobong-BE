package com.dobongsoon.BuyDobong.common.jwt;

import com.dobongsoon.BuyDobong.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
//    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String bearer = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearer != null && bearer.startsWith("Bearer ")) {
            try {
                Claims claims = jwtProvider.parse(bearer.substring(7)).getPayload();
                String sub  = claims.getSubject();
                String role = claims.get("role", String.class);
                if (sub == null || role == null) {
                    chain.doFilter(req, res);
                    return;
                }

                long userId = Long.parseLong(sub);
//
//                // 탈퇴한 사용자 차단 (DB 존재 검사)
//                if (!userRepository.existsById(userId)) {
//                    chain.doFilter(req, res);
//                    return;
//                }

                var authentication = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.debug("JWT parse failed: {}", e.getMessage());
            }
        }
        chain.doFilter(req, res);
    }
}
