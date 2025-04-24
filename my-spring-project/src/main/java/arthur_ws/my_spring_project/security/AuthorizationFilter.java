package arthur_ws.my_spring_project.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(SecurityConstants.Token_Header);

        if (header == null || !header.startsWith(SecurityConstants.Token_Prefix)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        String authorizationHeader = request.getHeader(SecurityConstants.Token_Header);

        if (authorizationHeader == null) {
            return null;
        }

        String token = authorizationHeader.replace(SecurityConstants.Token_Prefix, "");

        byte[] secretKeyBytes = SecurityConstants.getTokenSecret().getBytes();
        SecretKey key = Keys.hmacShaKeyFor(secretKeyBytes);

        JwtParser parser = Jwts.parser().verifyWith(key).build();

        Claims claims = parser.parseClaimsJws(token).getPayload();
        String subject = (String) claims.get("sub");

//        byte[] secretKeyBytes = Base64.getEncoder().encode(SecurityConstants.Token_Secret.getBytes());
//        SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());
//
//        JwtParser jwtParser = Jwts.parser().setSigningKey(secretKey).build();
//
//        Jwt<Header, Claims> jwt = (Jwt<Header, Claims>) jwtParser.parse(token);
//        String subject = jwt.getBody().getSubject();

        if(subject == null) {
            return null;
        }


        List<GrantedAuthority> authorities = new ArrayList<>();
        Object rolesObject = claims.get("roles");
        if(rolesObject instanceof List<?>) {
            for (Object role : (List<?>) rolesObject) {
                authorities.add(new SimpleGrantedAuthority(role.toString()));
            }
        }
        return new UsernamePasswordAuthenticationToken(subject, null, authorities);
    }
}
