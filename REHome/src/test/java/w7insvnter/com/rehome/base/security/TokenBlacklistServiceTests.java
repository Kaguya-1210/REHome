package w7insvnter.com.rehome.base.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

@SpringBootTest
class TokenBlacklistServiceTests {
    @Autowired
    TokenBlacklistService service;

    @Test
    void blacklistAndCheck() {
        String jti = java.util.UUID.randomUUID().toString();
        assertFalse(service.isBlacklisted(jti));
        service.blacklist(jti, Duration.ofMinutes(1));
        assertTrue(service.isBlacklisted(jti));
    }
}

