package de.drkhannover.tests.api.form.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.lang.NonNull;

import de.drkhannover.tests.api.form.dto.FormKvnDto.GENDER;

public class DefaultFormDto {
	@NotBlank
	public String firstname;
	@NotBlank
	public String lastname;
	@NotNull
	public GENDER gender;
	@NotBlank
	public String bday;
	public String phoneNumber;
	@NonNull
	public AddressDto address;
	public String insuranceType;
	public boolean mobile;
	public AddressDto mobileAddress;
	public String comment;
}
