package com.Edutrack.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.Edutrack.api_gateway.security.GatewayRoleAuthorization;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthWebFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthWebFilter.class);

    private final SecretKey secretKey;

    public JwtAuthWebFilter(@Value("${app.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        // Browsers send OPTIONS without Authorization for CORS preflight — do not block it.
        if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (!isBearerAuthorization(authHeader)) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            String token = authHeader.substring(7).trim();

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            if (userId == null || role == null) {
                log.warn("JWT is missing required claims: userId or role");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            HttpMethod method = exchange.getRequest().getMethod();
            if (method == null || !GatewayRoleAuthorization.isAllowed(role, method, path)) {
                log.warn("Forbidden for role={} method={} path={}", role, method, path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            ServerHttpRequest modifiedRequest = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Roles", GatewayRoleAuthorization.normalizeRole(role))
                    .build();

            log.info("JWT verified successfully: userId={}, role={}", userId, role);

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (Exception e) {
            log.warn("Invalid JWT token for path: {}", path, e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/auth")
                || path.startsWith("/public")
                || path.startsWith("/actuator")
                || path.equals("/");
    }

    /** Accepts {@code Authorization: Bearer <token>} with case-insensitive {@code Bearer}. */
    private static boolean isBearerAuthorization(String authHeader) {
        return authHeader != null
                && authHeader.length() > 7
                && authHeader.regionMatches(true, 0, "Bearer ", 0, 7);
    }
}