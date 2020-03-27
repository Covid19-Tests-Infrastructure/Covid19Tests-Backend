package de.drkhannover.tests.api.form.dto;

import javax.validation.constraints.NotNull;

import de.drkhannover.tests.api.form.RKICriteria;

public class FormDto {
	
	@NotNull
	public OrdererDto orderer;
	
	@NotNull
	public PatientDto patient;

	@NotNull
	public Covid19Info info;
	
	public static class OrdererDto {
		public String firstname;
		public String lastname;
		public String lanr;
		public String bsnr;
		public AddressDto address;
		public String phoneNumber;
		public String fax;
		public String email;
	}

	public static class PatientDto {
		public String occupationGroup;
		public String firstname;
		public String lastname;
		public String bday;
		public AddressDto address;
		public String phoneNumber;
		public String healthCareOrganisationNumber;
		public String personalHealthCareNumber;
		public String insuranceType;
		public boolean mobile;
	}
	
	public static class Covid19Info {
		public String diagnose;
		public RKICriteria rkiCrit;
		public String rkiReason;
	}
}
