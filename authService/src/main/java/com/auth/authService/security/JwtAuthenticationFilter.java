package com.auth.authService.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header=request.getHeader("Authorization");

        if(header!=null && header.startsWith("Bearer ")) {
            String token=header.substring(7).trim();
            System.out.println("TOKEN: " + token);


            if(jwtUtil.validateToken(token)) {
                System.out.println("TOKEN VALID");
                String username= jwtUtil.getUsername(token);
                String role=jwtUtil.getRole(token);

                UsernamePasswordAuthenticationToken auth=
                        new UsernamePasswordAuthenticationToken(
                                username,null,
                                List.of(new SimpleGrantedAuthority("ROLE_"+role)));

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            else {
                System.out.println("INVALID TOKEN");
            }
        }

        filterChain.doFilter(request,response);
    }
}




