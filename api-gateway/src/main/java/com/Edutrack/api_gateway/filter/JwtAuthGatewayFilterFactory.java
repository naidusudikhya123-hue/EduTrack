//package com.Edutrack.api_gateway.filter;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.sql.SQLOutput;
//import java.util.List;
//
//@Component
//public class JwtAuthGatewayFilterFactory
//        extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {
//
//    public static class Config {
//    }
//
//    private final SecretKey secretKey;
//
//    public JwtAuthGatewayFilterFactory(
//            @Value("${app.jwt.secret}") String secret) {
//        super(Config.class);
//        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//
//        return (exchange, chain) -> {
//
//            String path = exchange.getRequest().getURI().getPath();
//
//            // ✅ PUBLIC ENDPOINTS
//            if (path.startsWith("/auth")) {
//                return chain.filter(exchange);
//            }
//
//            String authHeader = exchange.getRequest()
//                    .getHeaders()
//                    .getFirst("Authorization");
//
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                System.out.println("JWT FILTER HIT");
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//
//
//            try {
//                String token = authHeader.substring(7);
//
//                Claims claims = Jwts.parser()
//                        .verifyWith(secretKey)
//                        .build()
//                        .parseSignedClaims(token)
//                        .getPayload();
//                String userId = claims.getSubject();   // ✅ FIX
//                String role = claims.get("role", String.class);
//
////                List<String> roles = (List<String>) claims.get("roles");
//
//                ServerHttpRequest modifiedRequest = exchange.getRequest()
//                        .mutate()
//                        .header("X-User-Id", userId)
//                        .header("X-User-Roles", role)
//                        .build();
//                System.out.println("userid"+userId);
//                System.out.println("role"+role);
//
//                return chain.filter(exchange.mutate().request(modifiedRequest).build());
//
//            } catch (Exception e) {
//
//                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                return exchange.getResponse().setComplete();
//            }
//        };
//    }
//}