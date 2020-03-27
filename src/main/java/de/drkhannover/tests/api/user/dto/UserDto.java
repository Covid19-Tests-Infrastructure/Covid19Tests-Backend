package de.drkhannover.tests.api.user.dto;

import static de.drkhannover.tests.api.util.NullHelpers.notNull;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import de.drkhannover.tests.api.form.dto.AddressDto;
import de.drkhannover.tests.api.form.dto.FormDto;
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
     * Do not use - :(
     * @param userDto
     * @return
     */
    public static User transformToUser(@Nonnull UserDto userDto) {
        String username = userDto.username;
        String password = null;
        if (userDto.passwordDto != null) {
        	password = userDto.passwordDto.newPassword;
        }
        if (username != null && password != null) {
            var user =  User.createDefaultUser(username, password);
            user.setRole(userDto.role);
            final var settings = userDto.settings;
            if (settings != null) {                
                SettingsDto.applyPersonalSettings(user, settings);
            }
            return user;
        } else {
            throw new IllegalArgumentException("The Userdto has null values which are not allowed");
        }
    }
    
	public static @Nonnull UserDto transformToDto(User user) {
		var dto = new UserDto();
		dto.isActive = user.isActive();
		dto.role = user.getRole();
		dto.username = user.getUsername();
		dto.settings = new SettingsDto();
		dto.settings.ordererInfo = new FormDto.OrdererDto();
      	var orderer = dto.settings.ordererInfo;
      	var dbSettings = user.getProfileConfiguration();
      	dto.settings.facility = dbSettings.getFacility();
      	orderer.address = new AddressDto();
      	orderer.address.ort = dbSettings.addressOrt;
      	orderer.address.hnumber = dbSettings.addressHnumber;
      	orderer.address.zip = dbSettings.addressZip;
      	orderer.address.street = dbSettings.addressStreet;
      	orderer.bsnr = dbSettings.bsnr;
      	orderer.lanr = dbSettings.lanr;
      	orderer.phoneNumber = dbSettings.phoneNumber;
      	orderer.lastname = dbSettings.lastname;
      	orderer.firstname = dbSettings.firstlame;
      	orderer.fax = dbSettings.fax;
		return dto;
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
            SettingsDto.applyPersonalSettings(databaseUser, notNull(userDto.settings));
            databaseUser.setUsername(notNull(userDto.username));
            databaseUser.setActive(userDto.isActive);
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
    
    public boolean isActive;
}
