package de.drkhannover.tests.api.user;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EnumType;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.drkhannover.tests.api.auth.exceptions.AccessViolationException;
import de.drkhannover.tests.api.conf.ControllerPath;
import de.drkhannover.tests.api.user.dto.UserDto;
import de.drkhannover.tests.api.user.exceptions.MissingDataException;
import de.drkhannover.tests.api.user.exceptions.UserEditException;
import de.drkhannover.tests.api.user.jpa.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

/**
 * @author Marcel
 */
@RestController
public class UserController {

	private Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private IUserService userService;

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@PutMapping(ControllerPath.USERS_PREFIX)
	public UserDto addLocalUser(@RequestBody @NotNull @Valid UserDto newUserDto) throws UserEditException {
		if (newUserDto.passwordDto == null) {
			throw new UserEditException("Password is required for new users");
		}
		var newUser = UserDto.transformToUser(newUserDto);
		if (!userService.isUserInDatabase(newUser)) {
			userService.storeUser(newUser);
			return User.userAsDto(newUser);
		} else {
			log.info("No user added: Duplicate entry");
			throw new UserEditException("Can't add user: Already existing");
		}
	}

	@PatchMapping(ControllerPath.USERS_PREFIX + "/{username}")
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	public UserDto editLocalUser(@RequestBody @NotNull @Nonnull @Valid UserDto userDto, @Nullable Authentication auth,
			@PathVariable("username") String usernamePath) throws MissingDataException, UsernameNotFoundException, AccessViolationException {
		if (auth == null) {
			throw new InternalError("Authentication not received");
		}
		String authenticatedUserName = Optional.ofNullable(auth.getPrincipal()).orElse("").toString();
		boolean selfEdit = authenticatedUserName.equals(usernamePath);
		UserRole role = authorityToRole(auth.getAuthorities());

		try {
			if (authenticatedUserName.isBlank() || usernamePath.isBlank()) {
				log.warn("Unknown user type is logged in: " + auth.getPrincipal().toString());
				throw new AccessViolationException("User not known.");
			}
			User userToEdit = userService.findUserByUsername(usernamePath);
			if (role == UserRole.ADMIN) {
				UserDto.adminUserDtoEdit(userToEdit, userDto);
			} else if (selfEdit) {
				UserDto.defaultUserDtoEdit(userToEdit, userDto);
			} else {
				log.warn("User " + authenticatedUserName + " tries to modify data of " + usernamePath);
				throw new AccessViolationException("Could not edit other users data");
			}
			userService.storeUser(userToEdit);
			return User.userAsDto(userToEdit);
		} catch (UsernameNotFoundException e) {
			log.info("A user which is not in the database was editied ");
			throw new UsernameNotFoundException("Could not edit user, : " + e.getMessage() + ". Maybe our database"
					+ " is offline or the user was deleted.");
		}
	}

	public UserRole authorityToRole(Collection<? extends GrantedAuthority> collection) {
		var singleAuthy = (GrantedAuthority) collection.toArray()[0];
		return EnumType.valueOf(UserRole.class, singleAuthy.getAuthority());
//		if (singleAuthy.getAuthority().equals("ROLE_ADMIN")) {
//			return UserRole.ADMIN;
//		}
//		return UserRole.DEFAULT;
	}

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(ControllerPath.USERS_PREFIX)
	public UserDto[] getAllUsers(Authentication auth) throws AccessViolationException {
		var role = authorityToRole(auth.getAuthorities());
		if (role != UserRole.ADMIN) {
			throw new AccessViolationException("");
		}
		var list = userService.findUsers();
		var dtoArray = new UserDto[list.size()];
		for (int i = 0; i < list.size(); i++) {
			dtoArray[i] = User.userAsDto(list.get(i));
		}
		return dtoArray;
	}

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@GetMapping(ControllerPath.USERS_PREFIX + "/{username}")
	public UserDto getUser(@PathVariable("username") String username, Authentication auth) throws AccessViolationException {
		var role = authorityToRole(auth.getAuthorities());
		if (username.equals(auth.getPrincipal()) || role == UserRole.ADMIN) {
			var user = userService.findUserByUsername(username);
			return User.userAsDto(user);
		} else {
			throw new AccessViolationException("");
		}
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
