package ws.core.security;

import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider {
	private Logger log = LogManager.getLogger(JwtTokenProvider.class);
	
	// Đoạn JWT_SECRET này là bí mật, chỉ có phía server biết
    private final String JWT_SECRET = "c0r3ciemes";

    //Thời gian có hiệu lực của chuỗi jwt 10 ngày (10*24*60*60*1000)
    private final long JWT_EXPIRATION = 604800000L;
    
    //Thời gian có hiệu lực của chuỗi jwt 15 ngày (15*24*60*60*1000)
    private final long JWT_REFRESH_EXPIRATION = 1296000000L;
    
    public Date generateExpiryDate() {
    	return new Date(new Date().getTime() + JWT_EXPIRATION);
    }
    
    // Tạo ra jwt từ thông tin user
    public String generateToken(CustomUserDetails userDetails, Date expiryDate) {
        // Tạo chuỗi json web token từ id của user.
        return Jwts.builder()
                   .setSubject(userDetails.getUser().username)
                   .setIssuedAt(new Date())
                   .setExpiration(expiryDate)
                   .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                   .compact();
    }

    // Lấy thông tin user từ jwt
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                            .setSigningKey(JWT_SECRET)
                            .parseClaimsJws(token)
                            .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
    
    public String getJWTRefreshToken() {
    	return UUID.randomUUID().toString();
    }
    
    public Date getJWTRefreshExpirationDate() {
    	return new Date(new Date().getTime() + JWT_REFRESH_EXPIRATION);
    }
}
