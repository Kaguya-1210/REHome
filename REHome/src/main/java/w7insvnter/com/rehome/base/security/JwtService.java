package w7insvnter.com.rehome.base.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access-exp-min}")
    private long accessExpMin;
    @Value("${jwt.refresh-exp-days}")
    private long refreshExpDays;
    private static final long CLOCK_SKEW_SECONDS = 60;
    private volatile Key cachedKey;

    private Key key() {
        if (cachedKey != null) return cachedKey;
        synchronized (this) {
            if (cachedKey != null) return cachedKey;
            byte[] bytes = null;
            try {
                byte[] decoded = Decoders.BASE64.decode(secret);
                if (decoded != null && decoded.length >= 32) {
                    bytes = decoded;
                }
            } catch (Exception ignored) {}
            if (bytes == null || bytes.length < 32) {
                try {
                    bytes = java.security.MessageDigest.getInstance("SHA-256")
                            .digest(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                } catch (java.security.NoSuchAlgorithmException ex) {
                    byte[] raw = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    bytes = new byte[32];
                    for (int i = 0; i < bytes.length; i++) bytes[i] = raw[i % raw.length];
                }
            }
            cachedKey = Keys.hmacShaKeyFor(bytes);
            return cachedKey;
        }
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder().setId(UUID.randomUUID().toString()).setSubject(subject).addClaims(claims).setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plusSeconds(accessExpMin * 60))).signWith(key(), SignatureAlgorithm.HS256).compact();
    }

    public String generateRefreshToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder().setId(UUID.randomUUID().toString()).setSubject(subject).setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plusSeconds(refreshExpDays * 24 * 60 * 60))).signWith(key(), SignatureAlgorithm.HS256).compact();
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS).build().parseClaimsJws(token).getBody().getSubject();
    }

    public String getJti(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).setAllowedClockSkewSeconds(CLOCK_SKEW_SECONDS).build().parseClaimsJws(token).getBody().getId();
    }
}
