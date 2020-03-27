package de.drkhannover.tests.api.form.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import de.drkhannover.tests.api.form.RKICriteria;

public class FormDto {
	public enum GENDER {M,W,D}
	
	@NotNull
	public OrdererDto orderer;
	
	@NotNull
	public PatientDto patient;

	@NotNull
	public Covid19Info info;
	
	public static class OrdererDto {
		@NotBlank
		public String firstname;
		@NotBlank
		public String lastname;
		public String lanr;
		public String bsnr;
		@NotBlank
		public AddressDto address;
		@NotBlank
		public String phoneNumber;
		public String fax;
		@NotBlank
		public String email;
	}

	public static class PatientDto {
		@NotBlank
		public String occupationGroup;
		@NotBlank
		public String firstname;
		@NotBlank
		public String lastname;
		@NotBlank
		public GENDER gender;
		@NotBlank
		public String bday;
		public AddressDto address;
		@NotBlank
		public String phoneNumber;
		public String healthCareOrganisationNumber;
		@NotBlank
		public String personalHealthCareNumber;
		@NotBlank
		public String insuranceType;
		@NotBlank
		public boolean mobile;
	}
	
	public static class Covid19Info {
		public String diagnose;
		public RKICriteria rkiCrit;
		public String rkiReason;
	}
}
