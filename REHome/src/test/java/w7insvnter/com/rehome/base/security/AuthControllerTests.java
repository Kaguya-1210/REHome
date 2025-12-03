package w7insvnter.com.rehome.base.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.HttpHeaders;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTests {
    @Autowired
    MockMvc mvc;

    @Autowired
    JwtService jwtService;

    @Autowired
    TokenBlacklistService blacklistService;

    @Test
    void logoutBlacklistsTokens() throws Exception {
        String access = jwtService.generateAccessToken("admin", java.util.Map.of());
        String refresh = jwtService.generateRefreshToken("admin");
        mvc.perform(post("/auth/logout").header(HttpHeaders.AUTHORIZATION, "Bearer " + access))
                .andExpect(status().isOk());
        String aj = jwtService.getJti(access);
        String rj = jwtService.getJti(refresh);
        blacklistService.blacklist(rj, java.time.Duration.ofSeconds(1));
        assert blacklistService.isBlacklisted(aj);
    }
}

