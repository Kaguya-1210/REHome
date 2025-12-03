package w7insvnter.com.rehome.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import w7insvnter.com.rehome.admin.service.AdminService;
import w7insvnter.com.rehome.admin.pojo.entity.AdminInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import w7insvnter.com.rehome.admin.pojo.dto.LoginDTO;
import w7insvnter.com.rehome.base.response.JsonResult;
import w7insvnter.com.rehome.base.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import w7insvnter.com.rehome.base.security.JwtService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private JwtService jwtService;
    
    @Value("${security.require-https:false}")
    private boolean requireHttps;

    @PostMapping("/login")
    public JsonResult login(@RequestBody @Valid LoginDTO loginDTO, @RequestParam("code") String code, HttpSession session, HttpServletResponse response) {
        Object stored = session.getAttribute("CAPTCHA_CODE");
        String expected = stored == null ? null : stored.toString();
        String provided = code == null ? null : code.trim();
        log.info("sessionId={}, expected={}, provided={}", session.getId(), expected, provided);
        if (expected == null || provided == null || !expected.equalsIgnoreCase(provided)) {
            return new JsonResult(StatusCode.VALIDATE_ERROR.getCode(), "验证码错误或过期");
        }
        session.removeAttribute("CAPTCHA_CODE");
        AdminInfo adminInfo = adminService.login(loginDTO.getAccount(), loginDTO.getPassword());
        String access = jwtService.generateAccessToken(adminInfo.getAccount(), java.util.Map.of("ip", "", "ua", ""));
        String refresh = jwtService.generateRefreshToken(adminInfo.getAccount());
        ResponseCookie accessCookie = ResponseCookie.from("access_token", access)
                .httpOnly(true).secure(requireHttps).sameSite("Strict").path("/")
                .maxAge(java.time.Duration.ofMinutes(30)).build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refresh)
                .httpOnly(true).secure(requireHttps).sameSite("Strict").path("/")
                .maxAge(java.time.Duration.ofDays(7)).build();
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        return new JsonResult(java.util.Map.of("user", adminInfo));
    }
    
}
