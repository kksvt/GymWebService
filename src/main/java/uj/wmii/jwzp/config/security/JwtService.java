package uj.wmii.jwzp.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import uj.wmii.jwzp.config.timezone.TimeZoneService;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String key;

    private final TimeZoneService timeZoneService;

    @Autowired
    public JwtService(TimeZoneService timeZoneService, @Value("${jwt.secret}") String key) {
        this.timeZoneService = timeZoneService;
        this.key = key;
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        LocalDateTime localDateTime = LocalDateTime.now(timeZoneService.getZoneId());
        return Jwts.builder().
                setClaims(extraClaims).
                setSubject(userDetails.getUsername()).
                setIssuedAt(Date.from(localDateTime.atZone(timeZoneService.getZoneId()).toInstant())).
                setExpiration(Date.from(localDateTime.plusDays(1).atZone(timeZoneService.getZoneId()).toInstant())).
                signWith(getSignInKey(), SignatureAlgorithm.HS256).
                compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(
                Date.from(
                        LocalDateTime.
                                now(timeZoneService.getZoneId()).
                                atZone(timeZoneService.getZoneId()).
                                toInstant()
                )
        );
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().
                setSigningKey(getSignInKey()).
                build().
                parseClaimsJws(token).
                getBody();
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }
}
