package de.drkhannover.tests.api.user.dto;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import de.drkhannover.tests.api.form.dto.FormDto;
import de.drkhannover.tests.api.user.jpa.User;

public class SettingsDto {
    
    public static void applyPersonalSettings(@Nonnull User user, @Nonnull SettingsDto settings) {
        de.drkhannover.tests.api.user.jpa.PersonalSettings dbSettings = user.getProfileConfiguration();
        var order = settings.ordererInfo;
        if (order != null) {
        	var address = order.address;
        	if (address != null) {
        		dbSettings.addressOrt = address.ort;
        		dbSettings.addressStreet = address.street;
        		dbSettings.addressZip = address.zip;
        		dbSettings.addressHnumber = address.hnumber;
        	}
        	dbSettings.lanr = order.lanr;
        	dbSettings.bsnr = order.bsnr;
        	dbSettings.email = order.email;
        	dbSettings.fax = order.fax;
        	dbSettings.firstlame = order.firstname;
        	dbSettings.lastname = order.lastname;
        	dbSettings.phoneNumber = order.phoneNumber;
        }
        dbSettings.setFacility(settings.facility);
    }
    
    @NotNull
    public String facility;
    public FormDto.OrdererDto ordererInfo;
}
