package de.drkhannover.tests.api.user;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.drkhannover.tests.api.user.jpa.User;


/**
 * Business logic for {@link StoredUser} and {@link StoredUserDetails}. This class is also used for a 
 * {@link UserDetailsService} authentication method through spring. It must provide a method for 
 * 
 * @author Marcel
 */
public interface IUserService extends UserDetailsService {

    /**
     * Make a user persistent (in any kind of data storage).
     * 
     * @param <T> A class which must extends from {@link StoredUser} (which holds the JPA definitions).
     * @param user Is saved in a persistence way (must hold username)
     */
    <T extends User> void storeUser(@Nonnull T user);

    /**
     * Searches a data storage for an explicit user identified by unique id. 
     * 
     * @param id unique identifier
     * @return StoredUser with the id
     * @throws UserNotFoundException If no user with the given id is found in data storage
     */
    @Nonnull User findUserById(int id) throws UsernameNotFoundException;
    
    @Nonnull User findUserByUsername(@Nullable String username) throws UsernameNotFoundException;

    /**
     * Finds all users in the database.
     * 
     * @return List of user, may be empty
     */
    @Nonnull List<User> findUsers();

    /**
     * Checks if the given user is already stored in the used data storage. This could used as an indicator if the 
     * user will be edited or a new one is created.
     * 
     * @param user The user to check
     * @return <code>true</code> if the user was already stored in the past, <code>false</code> otherwise
     */
    boolean isUserInDatabase(@Nullable User user);

    /**
     * Is used by SpringSecurity for getting user details with a given username. 
     * 
     * @param username name to look for
     * @return userDetails Details loaded from a data storage which is identified by the given username
     * @throws When a the given username is not found
     */
    @Override
    @Nonnull UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    
    void deleteUser(@Nullable User user);
    void deleteUser(@Nullable String username);
}
