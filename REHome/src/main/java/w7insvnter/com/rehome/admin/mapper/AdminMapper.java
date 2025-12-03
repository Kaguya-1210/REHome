package w7insvnter.com.rehome.admin.mapper;

import org.springframework.stereotype.Repository;
import w7insvnter.com.rehome.admin.pojo.entity.AdminInfo;
import org.apache.ibatis.annotations.Param;

@Repository
public interface AdminMapper {
    AdminInfo findByAccount(@Param("account") String account);
    int updatePasswordByAccount(@Param("account") String account, @Param("password") String password);
}
