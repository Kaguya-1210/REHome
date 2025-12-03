package w7insvnter.com.rehome.base.security;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import w7insvnter.com.rehome.base.response.JsonResult;
import java.time.Duration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private TokenBlacklistService blacklistService;
    @Value("${security.require-https:false}")
    private boolean requireHttps;

    @PostMapping("/refresh")
    public JsonResult refresh(@RequestHeader(value = "Authorization", required = false) String header,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        String token = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        }
        if (token == null) {
            var cookies = request.getCookies();
            if (cookies != null) {
                for (var c : cookies) {
                    if ("refresh_token".equals(c.getName())) { token = c.getValue(); break; }
                }
            }
        }
        if (token == null) return new JsonResult(5002, "缺少刷新令牌");
        String sub;
        try { sub = jwtService.getSubject(token); } catch (Exception e) { return new JsonResult(5002, "刷新令牌无效"); }
        String jti;
        try { jti = jwtService.getJti(token); } catch (Exception e) { jti = null; }
        if (jti != null) blacklistService.blacklist(jti, Duration.ofDays(7));
        String access = jwtService.generateAccessToken(sub, java.util.Map.of());
        String refresh = jwtService.generateRefreshToken(sub);
        ResponseCookie accessCookie = ResponseCookie.from("access_token", access)
                .httpOnly(true).secure(requireHttps).sameSite("Strict").path("/")
                .maxAge(java.time.Duration.ofMinutes(30)).build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refresh)
                .httpOnly(true).secure(requireHttps).sameSite("Strict").path("/")
                .maxAge(java.time.Duration.ofDays(7)).build();
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        return new JsonResult(java.util.Map.of("ok", true));
    }
    @PostMapping("/logout")
    public JsonResult logout(@RequestHeader(value = "Authorization", required = false) String header,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        String access = null;
        String refresh = null;
        if (header != null && header.startsWith("Bearer ")) access = header.substring(7);
        var cookies = request.getCookies();
        if (cookies != null) {
            for (var c : cookies) {
                if ("access_token".equals(c.getName())) access = access == null ? c.getValue() : access;
                if ("refresh_token".equals(c.getName())) refresh = refresh == null ? c.getValue() : refresh;
            }
        }
        try {
            if (access != null) {
                String aj = jwtService.getJti(access);
                blacklistService.blacklist(aj, java.time.Duration.ofMinutes(30));
            }
        } catch (Exception ignored) {}
        try {
            if (refresh != null) {
                String rj = jwtService.getJti(refresh);
                blacklistService.blacklist(rj, java.time.Duration.ofDays(7));
            }
        } catch (Exception ignored) {}
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true).secure(requireHttps).sameSite("Strict").path("/")
                .maxAge(java.time.Duration.ZERO).build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true).secure(requireHttps).sameSite("Strict").path("/")
                .maxAge(java.time.Duration.ZERO).build();
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        return new JsonResult(java.util.Map.of("ok", true));
    }
    @org.springframework.web.bind.annotation.GetMapping("/check")
    public JsonResult check(@RequestHeader(value = "Authorization", required = false) String header,
                            HttpServletRequest request) {
        String token = null;
        if (header != null && header.startsWith("Bearer ")) token = header.substring(7);
        if (token == null && request.getCookies() != null) {
            for (var c : request.getCookies()) if ("access_token".equals(c.getName())) { token = c.getValue(); break; }
        }
        if (token == null) return new JsonResult(401, "未认证");
        try {
            String sub = jwtService.getSubject(token);
            return new JsonResult(java.util.Map.of("subject", sub));
        } catch (Exception e) {
            return new JsonResult(401, "令牌无效或过期");
        }
    }
}
