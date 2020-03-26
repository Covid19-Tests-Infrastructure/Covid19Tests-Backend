package de.drkhannover.tests.api.auth;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import de.drkhannover.tests.api.conf.ConfigurationValues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ConfigurationValues confValues;

    private final Logger log = LoggerFactory.getLogger(JwtAuth.class);

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ConfigurationValues jwtConf) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl(ConfigurationValues.AUTH_LOGIN_URL);
        this.confValues = jwtConf;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        var userDetails = JwtAuth.extractCredentialsFromHttpRequest(request);
        log.debug("Authentication attempt");
        return authenticationManager.authenticate(userDetails);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) {
        log.info("Successful authentication with JWT");
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            var details = (UserDetails) principal;
            String token = JwtAuth.createJwtToken(details, confValues);
            response.addHeader(confValues.getJwtTokenHeader(), confValues.getJwtTokenPrefix() + " " + token);
        }
    }
}
