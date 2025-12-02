package w7insvnter.com.rehome.base.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import java.time.Duration;

public class CaptchaRateLimitInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate redis;
    private final int limit;
    private final Duration window;

    public CaptchaRateLimitInterceptor(StringRedisTemplate redis, int limit, Duration window) {
        this.redis = redis;
        this.limit = limit;
        this.window = window;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        String key = "captcha:ip:" + ip;
        Long count = redis.opsForValue().increment(key);
        redis.expire(key, window);
        if (count != null && count > limit) {
            response.setStatus(429);
            return false;
        }
        return true;
    }
}

