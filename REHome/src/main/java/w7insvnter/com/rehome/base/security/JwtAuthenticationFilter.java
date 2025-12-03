package w7insvnter.com.rehome.base.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @org.springframework.beans.factory.annotation.Autowired
    private JwtService jwtService;
    @org.springframework.beans.factory.annotation.Autowired
    private TokenBlacklistService blacklistService;
    public JwtAuthenticationFilter() {}
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            token = header.substring(7);
        } else if (request.getCookies() != null) {
            for (var c : request.getCookies()) {
                if ("access_token".equals(c.getName())) { token = c.getValue(); break; }
            }
        }
        if (token != null) {
            try {
                String jti = jwtService.getJti(token);
                if (!blacklistService.isBlacklisted(jti)) {
                    String sub = jwtService.getSubject(token);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(sub, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    log.warn("JWT blacklisted jti={}", jti);
                    response.setStatus(401);
                    return;
                }
            } catch (ExpiredJwtException e) {
                log.warn("JWT expired: {}", e.getMessage());
                response.setStatus(401);
                return;
            } catch (SignatureException e) {
                log.warn("JWT signature invalid: {}", e.getMessage());
                response.setStatus(401);
                return;
            } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
                log.warn("JWT invalid: {}", e.getMessage());
                response.setStatus(401);
                return;
            } catch (Exception e) {
                log.error("JWT parse error", e);
                response.setStatus(401);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
