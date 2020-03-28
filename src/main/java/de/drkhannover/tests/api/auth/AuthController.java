package de.drkhannover.tests.api.auth;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.drkhannover.tests.api.auth.exceptions.AccessViolationException;
import de.drkhannover.tests.api.conf.ConfigurationValues;
import de.drkhannover.tests.api.conf.ControllerPath;
import de.drkhannover.tests.api.user.IUserService;
import de.drkhannover.tests.api.user.dto.TokenDto;
import de.drkhannover.tests.api.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * Controller for authentication
 * 
 * @author Marcel
 */
@RestController
public class AuthController {
	static class AuthenticationInfoDto implements Serializable {
		private static final long serialVersionUID = 6315307325184022178L;
		UserDto userDto;
		TokenDto tokenDto;
	}

	@Autowired
	private ConfigurationValues confValues;

	@Autowired
	private IUserService userService;

	/**
	 * This method does nothing. The method header is important to let swagger list
	 * this authentication method. The authentication is handled through
	 * {@link JwtAuthenticationFilter} which listens on the same path than this
	 * method.
	 * 
	 * @param username Username of the user
	 * @param password Password of the user
	 */
	@PostMapping(value = ControllerPath.AUTHENTICATION_AUTH)
	public String authenticate(@NotNull String username, @NotNull String password) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks if the user is authenticated with a given JWT Token. If the token is
	 * valid, the controller is reachable otherwise it would be blocked through
	 * spring security and FORBIDDEN is returned.
	 * 
	 * @param auth Injected through spring if the user is logged in - holds
	 *             authentication information
	 * @return user information which are stored in the jwt token
	 * @throws AccessViolationException
	 */
	@GetMapping(value = ControllerPath.AUTHENTICATION_CHECK)
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	public UserDto isTokenValid(@Nullable Authentication auth, HttpServletRequest request)
			throws AccessViolationException {
		if (auth == null) { // check what went wrong
			var jwtToken = request.getHeader(confValues.getJwtTokenHeader());
			if (!StringUtils.isEmpty(jwtToken) && jwtToken.startsWith(confValues.getJwtTokenPrefix())) {
				JwtAuth.readJwtToken(jwtToken, confValues.getJwtSecret()); // should throw something
			}
			throw new AccessViolationException("Not authenticated");
		}
		String username = auth.getPrincipal().toString();
		var dto = new AuthenticationInfoDto();
		dto.userDto = userService.findUserByUsername(username).asDto();
		dto.tokenDto = (TokenDto) auth.getCredentials();
		//return dto;
		return dto.userDto;
	}

	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	@ExceptionHandler(AccessViolationException.class)
	public String handleUserEditException(AccessViolationException ex) {
		return handleException(ex);
	}

	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	@ExceptionHandler({ Exception.class })
	public String handleException(Exception ex) {
		if (ex.getMessage() == null || ex.getMessage().isEmpty()) {
			return "There was a problem with the user data.";
		} else {
			return ex.getMessage();
		}
	}
}
