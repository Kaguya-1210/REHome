package w7insvnter.com.rehome.admin.mapper;

import org.springframework.stereotype.Repository;
import w7insvnter.com.rehome.admin.pojo.entity.AdminInfo;

@Repository
public interface AdminMapper {
    AdminInfo getByAccount(String account);
}
