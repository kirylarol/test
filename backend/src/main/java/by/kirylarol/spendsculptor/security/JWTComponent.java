package by.kirylarol.spendsculptor.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class JWTComponent {
    @Value("${jwt_secret}")
    private String secret;

    @Value("${jwt_issuer}")
    private String issuer;

    private DecodedJWT jwt;

    public String generateToken (UserCredentials userCredentials){
        Date expire = Date.from(ZonedDateTime.now().plusDays(1).toInstant());
        List<String> rolesList = userCredentials.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return JWT.create()
                .withSubject("User Credentials")
                .withClaim("login", userCredentials.getUsername())
                .withClaim("password",userCredentials.getPassword())
                .withIssuedAt(new Date())
                .withExpiresAt(expire)
                .withIssuer(issuer)
                .withClaim("roles",rolesList)
                .sign(Algorithm.HMAC512(secret));
    }

    public Map<String, String> validateToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secret)).withSubject("User Credentials").withIssuer(issuer).build();
        try {
            jwt = verifier.verify(token);
            return Map.of("login",jwt.getClaim("login").asString(),"password",jwt.getClaim("password").asString());
        }catch (Exception exception){
            return null;
        }
    }
    public List<SimpleGrantedAuthority> getRoles(String token) {
        return jwt.getClaim("roles").asList(SimpleGrantedAuthority.class);
    }


}
