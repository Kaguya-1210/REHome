package w7insvnter.com.rehome.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import w7insvnter.com.rehome.admin.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import w7insvnter.com.rehome.admin.pojo.entity.AdminInfo;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    
    @GetMapping("/test")
    public AdminInfo Test() {
        return adminService.getByAccount("admin");
    }
}
