package w7insvnter.com.rehome.base.web;

import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

public class RequestIdInterceptor implements HandlerInterceptor {
    private static final String KEY = "requestId";
    private static final String HEADER = "X-Request-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String id = UUID.randomUUID().toString();
        MDC.put(KEY, id);
        response.setHeader(HEADER, id);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        MDC.remove(KEY);
    }
}

