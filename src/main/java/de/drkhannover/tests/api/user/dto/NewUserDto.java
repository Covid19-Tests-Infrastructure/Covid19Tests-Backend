package de.drkhannover.tests.api.user.dto;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import de.drkhannover.tests.api.user.UserRole;
import de.drkhannover.tests.api.user.jpa.User;
import de.drkhannover.tests.api.validation.ValidPassword;

public class NewUserDto {

    /**
     * Performs a transformation from DTO object to a StoredUserDetails. 
     * 
     * @param newUser valid DTO (username and password required)
     * @return user with the values of the DTO
     */
    public static User transformToUser(@Nonnull NewUserDto newUser) {
        String username = newUser.username;
        String password = newUser.password;
        if (username != null && password != null) {
            var user =  User.createDefaultUser(username, password);
            user.setRole(newUser.role);
            final var settings = newUser.personalSettings;
            if (settings != null) {                
                SettingsDto.applyPersonalSettings(user, settings);
            }
            return user;
        } else {
            throw new IllegalArgumentException("The NewUserDto hast null values which are not allowed");
        }
    }

    @NotBlank
    public String username;

    @NotNull
    @ValidPassword
    public String password; 

    public UserRole role;

    /**
     * Optional settings. If they aren't provided by the request body a new default set of settings will be generated.
     */
    @Valid
    private SettingsDto personalSettings;

    /**
     * Getter for Spring validation framework
     */
    public SettingsDto getPersonalSettings() {
        return personalSettings;
    }

    /**
     * Setter for Spring validation framework
     */
    public void setPersonalSettings(SettingsDto personalSettings) {
        this.personalSettings = personalSettings;
    }
}
