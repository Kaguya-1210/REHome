package w7insvnter.com.rehome.admin.service.impl;

import org.springframework.stereotype.Service;
import w7insvnter.com.rehome.admin.service.AdminService;
import w7insvnter.com.rehome.admin.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import w7insvnter.com.rehome.admin.pojo.entity.AdminInfo;

@Service
public class AdminSeviceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public AdminInfo getByAccount(String account) {
        return adminMapper.getByAccount(account);
    }
}
