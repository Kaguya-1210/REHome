package w7insvnter.com.rehome.base.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.data.redis.core.StringRedisTemplate;
import w7insvnter.com.rehome.base.security.CaptchaRateLimitInterceptor;
import java.time.Duration;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        //去掉原有的内容,编写我们自己的请求策略
        registry.addMapping("/**")//允许前端发过来的任意请求
                .allowedHeaders("*")//请求带任意请求头都可以
                .allowedMethods("*")//任意请求方式都可以 POST/GET/PUT...
                .allowedOriginPatterns("*")//任意域都可以(任意请求地址或端口号)
                .allowCredentials(true)//允许携带会话信息(cookie/session)
                .maxAge(3600);//同一请求一小时内不再检测,直接放行
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CaptchaRateLimitInterceptor(stringRedisTemplate, 30, Duration.ofMinutes(1)))
                .addPathPatterns("/captcha/**");
    }
}
