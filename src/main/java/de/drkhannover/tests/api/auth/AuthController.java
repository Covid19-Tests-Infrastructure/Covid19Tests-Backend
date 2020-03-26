package de.drkhannover.tests.api.auth;


import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.drkhannover.tests.api.conf.ControllerPath;
import de.drkhannover.tests.api.user.IUserService;
import de.drkhannover.tests.api.user.dto.UserDto;
import de.drkhannover.tests.api.user.jpa.User;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Controller for authentication
 * @author Marcel
 */
@RestController
public class AuthController {

	@Autowired
	private IUserService userService;

    /**
     * This method does nothing. The method header is important to let swagger list this authentication method.
     * The authentication is handled through {@link JwtAuthenticationFilter} which listens on the same
     * path than this method.
     * 
     * @param username Username of the user
     * @param password Password of the user
     */
    @PostMapping(value = ControllerPath.AUTHENTICATION_AUTH) 
    public String authenticate(@RequestParam(value = "username") @NotNull String username, 
                        @RequestParam(value = "password") @NotNull String password) {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the user is authenticated with a given JWT Token. If the token is valid, the controller is reachable
     * otherwise it would be blocked through spring security and FORBIDDEN is returned. 
     * 
     * @param auth Injected through spring if the user is logged in - holds authentication information
     * @return user information which are stored in the jwt token
     */
    @GetMapping(value = ControllerPath.AUTHENTICATION_CHECK) 
    public UserDto isTokenValid(@ApiIgnore @Nonnull Authentication auth) {
    	String username = auth.getPrincipal().toString();
    	var user = userService.findUserByUsername(username);
    	var dto = User.userAsDto(user);
    	return dto;
    }
}
