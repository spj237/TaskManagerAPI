package com.example.TaskManagement.security;

import com.example.TaskManagement.Repositories.BlackListRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailService userDetailsService;
    private final BlackListRepository blackListRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();


        try {
            if (path.startsWith("/auth/")) {
                filterChain.doFilter(request, response);
                return;
            }

            String header = request.getHeader("Authorization");
            String token = null;
            CustomUserDetails userDetails = null;
            if (header == null || !header.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401


                response.setContentType("application/json");

                response.getWriter().write("""
                            {
                                "status": 401,
                                "messages": ["user needs to login no token found"],
                                "path": "%s",
                                "timestamp": "%s"
                            }
                        """.formatted(request.getRequestURI(), LocalDateTime.now()));
                response.getWriter().flush();

                return;
            }

            token = header.substring(7);
            String userId = jwtUtil.extractUserId(token);
            userDetails = (CustomUserDetails) userDetailsService.loadUserById(Long.parseLong(userId));
            if (userDetails == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401


                response.setContentType("application/json");

                response.getWriter().write("""
                            {
                                "status": 401,
                                "messages": ["no such user"],
                                "path": "%s",
                                "timestamp": "%s"
                            }
                        """.formatted(request.getRequestURI(), LocalDateTime.now()));
                response.getWriter().flush();

                return;

            }

            if (blackListRepository.existsByToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401


                response.setContentType("application/json");

                response.getWriter().write("""
                            {
                                "status": 401,
                                "messages": ["user needs to login"],
                                "path": "%s",
                                "timestamp": "%s"
                            }
                        """.formatted(request.getRequestURI(), LocalDateTime.now()));
                response.getWriter().flush();

                return;
            }

            if (!jwtUtil.validToken(token, userDetails)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("""
                            {
                                "status": 401,
                                "messages": ["JWT token has expired"],
                                "path": "%s",
                                "timestamp": "%s"
                            }
                        """.formatted(request.getRequestURI(), LocalDateTime.now()));
                response.getWriter().flush();
                return;
            }

            // stop filter chain

            if (jwtUtil.validToken(token, userDetails) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                        {
                            "status": 401,
                            "messages": ["JWT token has expired"],
                            "path": "%s",
                            "timestamp": "%s"
                        }
                    """.formatted(request.getRequestURI(), LocalDateTime.now()));
            response.getWriter().flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("""
                        {
                            "status": 400,
                            "messages": ["%s"],
                            "path": "%s",
                            "timestamp": "%s"
                        }
                    """.formatted(e.getMessage(),request.getRequestURI(), LocalDateTime.now()));
            response.getWriter().flush();
        }
    }
}
