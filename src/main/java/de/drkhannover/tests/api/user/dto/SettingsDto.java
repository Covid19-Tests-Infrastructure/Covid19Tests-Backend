package de.drkhannover.tests.api.user.dto;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import de.drkhannover.tests.api.user.jpa.User;

public class SettingsDto {
    
    @Nonnull
    public static User applyPersonalSettings(@Nonnull User user, @Nonnull SettingsDto settings) {
        de.drkhannover.tests.api.user.jpa.PersonalSettings dbSettings = user.getProfileConfiguration();
        dbSettings.setFacility(settings.facility);
        return user;
    }
    
    @NotNull
    public String facility;
    
}
