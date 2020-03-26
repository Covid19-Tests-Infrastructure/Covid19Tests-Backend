package de.drkhannover.tests.api.auth;

import static de.drkhannover.tests.api.util.NullHelpers.notNull;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.drkhannover.tests.api.conf.ConfigurationValues;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtAuth {
	static class Credentials {
		public String password;
		public String username;
	}
    private JwtAuth() {}

    public static UsernamePasswordAuthenticationToken extractCredentialsFromHttpRequest(HttpServletRequest request) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null && password == null) {
        	try {
        		Credentials cred = new ObjectMapper().readValue(request.getInputStream(), Credentials.class); 
        	    username = cred.username;
        		password = cred.password;
        	} catch (java.io.IOException e) {
        		throw new RuntimeException(e);
        	}
        }
        return new UsernamePasswordAuthenticationToken(username, password);
    }

    public static @Nullable String createJwtFromAuthentication(Authentication authentication, ConfigurationValues jwtConf) {
        if (authentication.getPrincipal() instanceof UserDetails) {
            return createJwtToken((UserDetails) authentication, jwtConf);
        } else {
            return null;
        }
    }

    public static @Nonnull String createJwtToken(UserDetails user, ConfigurationValues jwtConf) {
        var roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
            var signingKey = jwtConf.getJwtSecret().getBytes();
            var token = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
                .setHeaderParam("typ", jwtConf.getJwtTokenType())
                .setIssuer(jwtConf.getJwtTokenIssuer())
                .setAudience(jwtConf.getJwtTokenAudience())
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000))
                .claim("rol", roles)
                .compact();
            return notNull(token);
    }

    public static @Nullable UsernamePasswordAuthenticationToken readJwtToken(String token, String jwtSecret) {
        var signingKey = jwtSecret.getBytes();
        var parsedToken = Jwts.parser()
            .setSigningKey(signingKey)
            .parseClaimsJws(token.replace("Bearer ", ""));
        var username = parsedToken
            .getBody()
            .getSubject();
        var authorities = ((List<?>) parsedToken.getBody()
            .get("rol")).stream()
            .map(authority -> new SimpleGrantedAuthority((String) authority))
            .collect(Collectors.toList());
        if (!StringUtils.isEmpty(username)) {
        	return new UsernamePasswordAuthenticationToken(username, null, authorities);
        } else {
        	return null;
        }
    }
}
