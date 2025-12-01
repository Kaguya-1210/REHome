package w7insvnter.com.rehome.admin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import w7insvnter.com.rehome.admin.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import w7insvnter.com.rehome.admin.pojo.entity.AdminInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import w7insvnter.com.rehome.admin.pojo.dto.LoginDTO;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    
    @PostMapping("/login")
    public String login(@RequestBody LoginDTO loginDTO) {
        //TODO: process POST request
        
        return null;
    }
    
}
