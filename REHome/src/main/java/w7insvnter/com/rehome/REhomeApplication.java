package w7insvnter.com.rehome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.dustlight.captcha.annotations.EnableCaptcha;

@SpringBootApplication
@EnableCaptcha
public class REhomeApplication {
    public static void main(String[] args) {
        SpringApplication.run(REhomeApplication.class, args);
    }

}
