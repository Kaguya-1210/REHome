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
import w7insvnter.com.rehome.admin.pojo.dto.LoginDTO;
import w7insvnter.com.rehome.base.response.JsonResult;
import w7insvnter.com.rehome.base.response.StatusCode;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private AdminService adminService;
    
    @PostMapping("/login")
    public JsonResult login(@RequestBody LoginDTO loginDTO, @RequestParam("code") String code, HttpSession session) {
        Object stored = session.getAttribute("CAPTCHA_CODE");
        String expected = stored == null ? null : stored.toString();
        String provided = code == null ? null : code.trim();
        log.info("sessionId={}, expected={}, provided={}", session.getId(), expected, provided);
        if (expected == null || provided == null || !expected.equalsIgnoreCase(provided)) {
            return new JsonResult(StatusCode.VALIDATE_ERROR.getCode(), "验证码错误或过期");
        }
        session.removeAttribute("CAPTCHA_CODE");
        AdminInfo adminInfo = adminService.login(loginDTO.getAccount(), loginDTO.getPassword());
        return new JsonResult(adminInfo);
    }
    
}
