package w7insvnter.com.rehome.admin.service;
import w7insvnter.com.rehome.admin.pojo.entity.AdminInfo;

public interface AdminService {
    AdminInfo getByAccount(String account);
}
