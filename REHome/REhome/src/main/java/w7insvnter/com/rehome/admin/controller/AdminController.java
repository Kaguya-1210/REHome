package w7insvnter.com.rehome.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import w7insvnter.com.rehome.admin.service.AdminService;
import w7insvnter.com.rehome.admin.pojo.entity.AdminInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import w7insvnter.com.rehome.admin.pojo.dto.LoginDTO;
import w7insvnter.com.rehome.base.response.JsonResult;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    
    @PostMapping("/login")
    public JsonResult login(@RequestBody LoginDTO loginDTO) {
        AdminInfo adminInfo = adminService.login(loginDTO.getAccount(), loginDTO.getPassword());
        return new JsonResult(adminInfo);
    }
    
}
