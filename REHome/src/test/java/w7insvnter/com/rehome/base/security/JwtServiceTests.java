package w7insvnter.com.rehome.base.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class JwtServiceTests {
    @Autowired
    JwtService jwtService;

    @Test
    void generateAndParseAccessToken() {
        String token = jwtService.generateAccessToken("admin", Map.of("role","ADMIN"));
        assertNotNull(token);
        String sub = jwtService.getSubject(token);
        assertEquals("admin", sub);
        String jti = jwtService.getJti(token);
        assertNotNull(jti);
    }

    @Test
    void invalidTokenThrows() {
        assertThrows(Exception.class, () -> jwtService.getSubject("invalid.token.value"));
    }
}

