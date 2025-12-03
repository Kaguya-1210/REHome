package w7insvnter.com.rehome.admin.pojo.dto;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class LoginDTO {
    @NotBlank
    @Size(min = 3, max = 64)
    private String account;
    @NotBlank
    @Size(min = 6, max = 128)
    private String password;
}
