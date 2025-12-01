package w7insvnter.com.rehome.admin.service.impl;

import org.springframework.stereotype.Service;
import w7insvnter.com.rehome.admin.service.AdminService;
import w7insvnter.com.rehome.base.response.StatusCode;
import w7insvnter.com.rehome.admin.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import w7insvnter.com.rehome.admin.pojo.entity.AdminInfo;
import lombok.extern.slf4j.Slf4j;
import w7insvnter.com.rehome.base.exception.ServiceException;

@Service
@Slf4j
public class AdminSeviceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public AdminInfo login(String account, String password) {
        AdminInfo data = adminMapper.login(account, password);
        log.info("data:{}", data);
        // 添加空检查，避免空指针异常
        if (data == null) {
            throw new ServiceException(StatusCode.PASSWORD_ERROR);
        }
        // 检查账号密码是否匹配
        if (!data.getAccount().equals(account) || !data.getPassword().equals(password)) {
            throw new ServiceException(StatusCode.PASSWORD_ERROR);
        }
        return data;
    }
}
