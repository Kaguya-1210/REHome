package w7insvnter.com.rehome.base.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class TokenBlacklistService {
    private final StringRedisTemplate redis;
    public TokenBlacklistService(StringRedisTemplate redis) { this.redis = redis; }
    public void blacklist(String jti, Duration ttl) { if (jti != null) redis.opsForValue().set(key(jti), "1", ttl); }
    public boolean isBlacklisted(String jti) { return jti != null && Boolean.TRUE.equals(redis.hasKey(key(jti))); }
    private String key(String jti) { return "jwt:blacklist:" + jti; }
}

