package de.drkhannover.tests.api.form.dto;

import de.drkhannover.tests.api.form.RKICriteria;

public class FormDto {
	public OrdererDto orderer;
	public PatientDto patient;
	public Covid19Info info;
	
	public static class OrdererDto {
		public String firstname;
		public String lastname;
		public AddressDto address;
		public String phoneNumber;
		public String fax;
		public String email;
	}

	public static class PatientDto {
		public String berufsgruppe;
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
