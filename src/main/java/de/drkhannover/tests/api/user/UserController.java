package de.drkhannover.tests.api.user;

import static de.drkhannover.tests.api.util.NullHelpers.notNull;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.drkhannover.tests.api.auth.exceptions.AccessViolationException;
import de.drkhannover.tests.api.conf.ControllerPath;
import de.drkhannover.tests.api.user.dto.NewUserDto;
import de.drkhannover.tests.api.user.dto.UserDto;
import de.drkhannover.tests.api.user.exceptions.MissingDataException;
import de.drkhannover.tests.api.user.exceptions.UserEditException;
import de.drkhannover.tests.api.user.jpa.User;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Marcel
 */
@RestController
public class UserController {

	private Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private IUserService userService;

	@PutMapping(ControllerPath.USER_ADD)
	public UserDto addLocalUser(@RequestBody @NotNull @Valid NewUserDto newUserDto) throws UserEditException {
		@Nonnull String username = notNull(newUserDto.username); // spring validation
		@Nonnull String password = notNull(newUserDto.password); // spring validation
		final var newUser = User.createDefaultUser(username, password);
		newUser.setRole(newUserDto.role);
		newUser.getProfileConfiguration().setFacility(newUserDto.getPersonalSettings().facility);
		if (!userService.isUserInDatabase(newUser)) {
			userService.storeUser(newUser);
			return User.userAsDto(newUser);
		} else {
			log.info("No user added: Duplicate entry");
			throw new UserEditException("Can't add user: Already existing");
		}
	}

	@PutMapping(ControllerPath.USER_EDIT)
	public UserDto editLocalUser(@RequestBody @NotNull @Nonnull @Valid UserDto userDto, @ApiIgnore @Nullable Authentication auth)
			throws MissingDataException, UsernameNotFoundException, AccessViolationException {
		if (auth == null) {
			throw new InternalError("Authentication not received");
		}
		String username = Optional.ofNullable(auth.getPrincipal()).orElse("").toString();
		boolean selfEdit = username.equals(userDto.username);
		UserRole role = authorityToRole(auth.getAuthorities());

		try {
			if (username.isBlank()) {
				log.warn("Unknown user type is logged in: " + auth.getPrincipal().toString());
				throw new AccessViolationException("User not known.");
			}
			User authenticatedUser = userService.findUserByUsername(username);
			if (role == UserRole.ADMIN) {
				UserDto.adminUserDtoEdit(authenticatedUser, userDto);
			} else if (selfEdit) {
				UserDto.defaultUserDtoEdit(authenticatedUser, userDto);
				userService.storeUser(authenticatedUser);
				return User.userAsDto(authenticatedUser);
			} else {
				log.warn("User " + username + " tries to modify data of " + userDto.username);
				throw new AccessViolationException("Could not edit other users data");
			}
		} catch (UsernameNotFoundException e) {
			log.info("User is logged  in but no data is in the database. Maybe database is down?");
			throw new UsernameNotFoundException("Could not edit user, reason: " + e.getMessage() + ". Maybe our databse"
					+ " is offline or the logged in user was deleted.");
		}
		return new UserDto();
	}

	public UserRole authorityToRole(Collection<? extends GrantedAuthority> collection) {
		var singleAuthy = (GrantedAuthority) collection.toArray()[0];
		return Enum.valueOf(UserRole.class, singleAuthy.getAuthority());
	}

	
//    @GetMapping("/user/delete")

	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	@ExceptionHandler(AccessViolationException.class)
	public String handleUserEditException(AccessViolationException ex) {
		return handleException(ex);
	}

	@ResponseStatus(code = HttpStatus.NOT_MODIFIED)
	@ExceptionHandler(MissingDataException.class)
	public String handleUserNotFound(MissingDataException ex) {
		return handleException(ex);
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler({ UsernameNotFoundException.class, UserEditException.class })
	public String handleException(Exception ex) {
		if (ex.getMessage() == null || ex.getMessage().isEmpty()) {
			return "There was a problem with the user data.";
		} else {
			return ex.getMessage();
		}
	}
}
