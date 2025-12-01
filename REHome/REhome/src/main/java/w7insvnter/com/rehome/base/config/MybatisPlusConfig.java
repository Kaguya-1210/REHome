package w7insvnter.com.rehome.base.config;

import org.springframework.context.annotation.Configuration;
import org.mybatis.spring.annotation.MapperScan;

@Configuration
@MapperScan("w7insvnter.com.rehome.*.mapper")
public class MybatisPlusConfig {

}
