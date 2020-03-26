package de.drkhannover.tests.api.user;

import static de.drkhannover.tests.api.util.NullHelpers.notNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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

/**
 * @author Marcel
 */
@RestController
public class UserController {

    private Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService userService;
    
    @PutMapping(ControllerPath.USER_ADD)
    public String addLocalUser(@RequestBody @NotNull @Valid NewUserDto newUserDto) throws UserEditException {
        @Nonnull String username = notNull(newUserDto.username); // spring validation
        @Nonnull String password = notNull(newUserDto.password); // spring validation
        
        final var newUser = User.createDefaultUser(username, password);
        if (!userService.isUserInDatabase(newUser)) {
            userService.storeUser(newUser);
            //return newUser.toString();
        } else {
            log.info("No user added: Duplicate entry");
            throw new UserEditException("Can't add user: Already existing");
        }
    }

    @PutMapping(ControllerPath.MANAGEMENT_EDIT_USER)
    public void editLocalUser(@RequestBody @NotNull @Nonnull @Valid UserDto userDto, @Nullable Authentication auth)
                              throws MissingDataException, UsernameNotFoundException, AccessViolationException {
        if (auth == null) {
            throw new InternalError("Authentication not received");
        }
        @Nullable User authenticatedUser = auth.getPrincipal();
        boolean selfEdit = authenticatedUser.getUserName().equals(userDto.username);
        
        try {
            if (authenticatedUser == null) {
                log.warn("Unknown user type is logged in: " + auth.getPrincipal().toString());
                throw new AccessViolationException("User not known.");
            }
            boolean selfEdit = authenticatedUser.getUserName().equals(userDto.username) 
                    && authenticatedUser.getRealm().equals(userDto.realm);
            if (authenticatedUser.getRole().equals(UserRole.ADMIN.name())) {
                UserDto.adminUserDtoEdit(authenticatedUser, userDto);
            } else if (selfEdit) {
                UserDto.defaultUserDtoEdit(authenticatedUser, userDto);
                userService.storeUser(authenticatedUser);
            } else {
                log.warn("User " + authenticatedUser.getUserName() + " tries to modify data of " + userDto.username);
                throw new AccessViolationException("Could not edit other users data");
            }
        } catch (UserNotFoundException e) {
            log.info("User is logged  in but no data is in the database. Maybe database is down?");
            throw new UserNotFoundException("Could not edit user, reason: " + e.getMessage() + ". Maybe our databse"
                    + " is offline or the logged in user was deleted.");
        }
    }

//    @GetMapping("/user/delete")
//    @GetMapping("/user/changepass")a

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
    @ExceptionHandler({ UserNotFoundException.class, UserEditException.class })
    public String handleException(Exception ex) {
        if (ex.getMessage() == null || ex.getMessage().isEmpty()) {
            return "There was a problem with the user data.";
        } else {
            return ex.getMessage();
        }
    }
}
