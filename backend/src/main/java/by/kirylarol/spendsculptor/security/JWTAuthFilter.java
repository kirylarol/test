package by.kirylarol.spendsculptor.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Map;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTComponent jwtComponent;

    @Autowired
    public JWTAuthFilter(JWTComponent jwtComponent) {
        this.jwtComponent = jwtComponent;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        jwt = authHeader.substring(7);
        try {
            Map<String, String> credentials = jwtComponent.validateToken(jwt);
            if (credentials.get("login") == null) throw new Exception("invalid JWT");
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    credentials.get("login"),
                    credentials.get("password"),
                    jwtComponent.getRoles(jwt)
            );
            SecurityContextHolder.getContext().setAuthentication(token);
        }catch (Exception exception){
            filterChain.doFilter(request,response);
        }
        filterChain.doFilter(request,response);
    }
}
