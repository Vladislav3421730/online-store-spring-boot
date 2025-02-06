package com.example.onlinestorespringboot.config;


import com.example.onlinestorespringboot.util.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtTokenUtils.getUsername(jwt);
            } catch (ExpiredJwtException e) {
                log.debug("Token expiration time has passed");
            } catch (SignatureException e) {
                log.debug("Invalid signature");
            } catch (MalformedJwtException e) {
                log.debug("Invalid token");
            } catch (UnsupportedJwtException e) {
                log.debug("Token format is not supported");
            } catch (IllegalArgumentException e) {
                log.debug("Token is empty or invalid");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    jwtTokenUtils.getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
            );
            SecurityContextHolder.getContext().setAuthentication(token);
        }

        filterChain.doFilter(request, response);
    }
}
