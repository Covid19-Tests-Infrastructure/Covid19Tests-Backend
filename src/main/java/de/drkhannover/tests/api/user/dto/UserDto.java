package de.drkhannover.tests.api.user.dto;

import static de.drkhannover.tests.api.util.NullHelpers.notNull;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import de.drkhannover.tests.api.user.UserRole;
import de.drkhannover.tests.api.user.jpa.User;

public class UserDto implements Serializable {

    private static final long serialVersionUID = 289473289744543L;

    public static class ChangePasswordDto {
        public String oldPassword;

        @NotBlank
        public String newPassword; 
    }

    /**
     * Changes the values of the given user with values from the DTO. This happens recursive (it will
     * change {@link SettingsDto} and {@link ChangePasswordDto} as well).<br><br>
     * 
     * This is done with "limit permissions". Which means some fields which should only be changed with higher 
     * permissions won't be changed and are skipped.
     * 
     * Those permissions are:
     * </li><li> The old password must be provided in order to change it
     * </li><li> User can not modify roles: {@link User#setRole(Enum)}
     * </ul>
     * 
     * @param databaseUser User which values should be changed
     * @param userDto Transfer object which holds the new data
     */
    public static void defaultUserDtoEdit(@Nonnull User databaseUser, @Nonnull UserDto userDto ) {
        editUserFromDto(databaseUser, userDto, false);
    }

    /**
     * Changes the values of the given user with values from the DTO. This happens recursive (it will
     * change {@link SettingsDto} and {@link ChangePasswordDto} as well). <br>
     * 
     * Any other data will be modified.
     * 
     * @param databaseUser User which values should be changed
     * @param userDto Holds the new data
     */
    public static void adminUserDtoEdit(@Nonnull User databaseUser, @Nonnull UserDto userDto) {
        editUserFromDto(databaseUser, userDto, true);
    }

    private static void editUserFromDto(@Nonnull User databaseUser, @Nonnull UserDto userDto, boolean adminMode) {
        if (userDto.settings != null && userDto.username != null) {
            databaseUser = SettingsDto.applyPersonalSettings(databaseUser, notNull(userDto.settings));
            databaseUser.setUsername(notNull(userDto.username));
            boolean changePassword = userDto.passwordDto != null;
            boolean adminPassChange = adminMode && changePassword && userDto.passwordDto.newPassword != null;
            if (changePassword) {
                defaultApplyNewPasswordFromDto(databaseUser, userDto.passwordDto);
            } else if (adminPassChange) {
                adminApplyNewPasswordFromDto(databaseUser, userDto.passwordDto.newPassword);
            }
            if (adminMode && userDto.role != null) {
                databaseUser.setRole(notNull(userDto.role));
            }
        }
    }

    /**
     * Try to apply a new password to the given user. The {@link ChangePasswordDto#oldPassword} must
     * match the one which is already stored in the database. Otherwise the password won't be changed.
     * 
     * @param databaseUserDetails user who's password should be changed
     * @param passwordDto contains old and new password (both values can be null)
     */
    public static void defaultApplyNewPasswordFromDto(@Nullable User databaseUser,
                                                      @Nullable UserDto.ChangePasswordDto passwordDto) {
        if (passwordDto != null && databaseUser != null) {
            @Nullable String newPassword = passwordDto.newPassword;
            @Nullable String oldPassword = passwordDto.oldPassword;
            if (newPassword != null && oldPassword != null) {
                applyPasswordFromDto(databaseUser, newPassword, oldPassword, false);
            }
        }
    }

    /**
     * Apply a new password to the given user. 
     * 
     * @param databaseUser User where the password should be changed
     * @param newPassword New raw password
     */
    public static void adminApplyNewPasswordFromDto(@Nullable User databaseUser,
                                                    @Nullable String newPassword) {
        if (databaseUser != null && newPassword != null) {
            applyPasswordFromDto(databaseUser, newPassword, "", true);
        }
    }

    /**
     * Changes the password of a given use with the given password DTO. <br>
     * In case of an admin mode, only a new password must be provided in order to succeed.
     * 
     * @param databaseUser User to edit
     * @param newPassword New raw password (necessary)
     * @param oldPassword The old hashed password (only on non adminEdits necessary)
     * @param adminEdit Decide if the old password must match with the new one
     */
    private static void applyPasswordFromDto(@Nonnull User databaseUser, @Nonnull String newPassword,
                                             @Nonnull String oldPassword, boolean adminEdit) {
        if (!newPassword.isBlank()) {
            if (adminEdit || databaseUser.getEncoder().matches(oldPassword, databaseUser.getPassword())) {
            	databaseUser.encodeAndSetPassword(newPassword);
            } 
        }
    }

    @NotBlank
    public String username;

    /**
     * Not necessary. 
     */
    @Valid
    public ChangePasswordDto passwordDto;

    @Valid
    @NotNull
    public SettingsDto settings;

    public UserRole role;
}
