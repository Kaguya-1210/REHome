package w7insvnter.com.rehome.admin.service.impl;

import org.springframework.stereotype.Service;
import w7insvnter.com.rehome.admin.service.AdminService;
import w7insvnter.com.rehome.base.response.StatusCode;
import w7insvnter.com.rehome.admin.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import w7insvnter.com.rehome.admin.pojo.entity.AdminInfo;
import lombok.extern.slf4j.Slf4j;
import w7insvnter.com.rehome.base.exception.ServiceException;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;

@Service
@Slf4j
public class AdminSeviceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public AdminInfo login(String account, String password) {
        String lockKey = "login:lock:" + account;
        Boolean locked = stringRedisTemplate.hasKey(lockKey);
        if (Boolean.TRUE.equals(locked)) {
            throw new ServiceException(StatusCode.OPERATION_FAILED);
        }
        AdminInfo data = adminMapper.login(account, password);
        log.info("data:{}", data);
        if (data == null) {
            String failKey = "login:fail:" + account;
            Long fail = stringRedisTemplate.opsForValue().increment(failKey);
            stringRedisTemplate.expire(failKey, Duration.ofMinutes(15));
            if (fail != null && fail >= 5) {
                stringRedisTemplate.opsForValue().set(lockKey, "1", Duration.ofMinutes(15));
            }
            throw new ServiceException(StatusCode.PASSWORD_ERROR);
        }
        if (!data.getAccount().equals(account) || !data.getPassword().equals(password)) {
            String failKey = "login:fail:" + account;
            Long fail = stringRedisTemplate.opsForValue().increment(failKey);
            stringRedisTemplate.expire(failKey, Duration.ofMinutes(15));
            if (fail != null && fail >= 5) {
                stringRedisTemplate.opsForValue().set(lockKey, "1", Duration.ofMinutes(15));
            }
            throw new ServiceException(StatusCode.PASSWORD_ERROR);
        }
        stringRedisTemplate.delete("login:fail:" + account);
        return data;
    }
}
