package w7insvnter.com.rehome.admin.pojo.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AdminInfo {
    private Integer id;
    private String account;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
