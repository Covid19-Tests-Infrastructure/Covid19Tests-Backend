package de.drkhannover.tests.api.conf;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.drkhannover.tests.api.auth.JwtAuthenticationFilter;
import de.drkhannover.tests.api.auth.JwtAuthorizationFilter;
import de.drkhannover.tests.api.user.UserRole;
import de.drkhannover.tests.api.user.UserServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${recovery.enabled}")
    private String inMemoryEnabled;
    
    @Value("${recovery.password}")
    private String inMemoryPassword;
    
    @Value("${recovery.user}")
    private String inMemoryUser;
    
    @Autowired
    private ConfigurationValues confValues;
    
    @Autowired
    private UserServiceImpl userServiceImpl;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(ControllerPath.SWAGGER).permitAll()
            .antMatchers(ControllerPath.AUTHENTICATION_AUTH).permitAll()
            .antMatchers(ControllerPath.USERS_PREFIX).hasAuthority(UserRole.ADMIN.name()) // admin: allowed to add users
            .antMatchers(ControllerPath.AUTHENTICATION_CHECK).permitAll()
            .antMatchers(ControllerPath.FORMULAR_PRIVATE).hasAnyAuthority(UserRole.ADMIN.name(), UserRole.DEFAULT.name())
            .antMatchers(ControllerPath.FORMULAR_KVN).hasAnyAuthority(UserRole.ADMIN.name(), UserRole.KVN.name())
            .antMatchers("/**").authenticated()
            //.antMatchers("/**").permitAll()//maybe remove later
            .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), confValues))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), confValues))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    }
    
    @Override // allow swagger 
    // TODO Marcel: test if necessary
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v3/api-docs/**",
        						   "/swagger-ui/**",
                                   "/configuration/ui",
                                   "/swagger-resources/**",
                                   "/configuration/security",
                                   "/swagger-ui.html",
                                   "/webjars/**");
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (inMemoryEnabled != null && Boolean.parseBoolean(inMemoryEnabled)) {
            auth.inMemoryAuthentication()
            .withUser(inMemoryUser)
            .password(passwordEncoder.encode(inMemoryPassword))
            .roles(UserRole.ADMIN.name());
        }
        auth.userDetailsService(userServiceImpl);
    }
    
    @Bean("authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
    }
}