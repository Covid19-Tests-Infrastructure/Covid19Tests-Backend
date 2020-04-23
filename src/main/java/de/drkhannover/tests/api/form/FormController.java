package de.drkhannover.tests.api.form;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.drkhannover.tests.api.conf.ControllerPath;
import de.drkhannover.tests.api.form.dto.FormKvnDto;
import de.drkhannover.tests.api.form.dto.FormKvnDto.PatientDto;
import de.drkhannover.tests.api.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author Marcel
 */
@RestController
public class FormController {
    public static class PriceDto {
        public Map<String, Integer> priceList;
    }

    @Autowired
    public IUserService userService;

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PutMapping(ControllerPath.PRICE_GET)
    public PriceDto getPrice() {
        var p = new PriceDto();
        p.priceList = new HashMap<String, Integer>();
        p.priceList.put("single", 40);
        return p;
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PutMapping(ControllerPath.FORMULAR_PRIVATE)
    public void addFormularPrivate(@RequestBody @Nonnull @NotNull @Valid PatientDto patient, Authentication auth) {
        var user = userService.findUserByUsername((String) auth.getPrincipal());
        if (user.getProfileConfiguration().getFacility() == null) {
            throw new Error("User has no facility");
        }
        var builder = new StringBuilder();
        builder.append("FacilityNr: " + user.getProfileConfiguration().getFacility() + "\n");
        builder.append("PatientOccupation: " + patient.occupationGroup + "\n");
        builder.append("PatientVorname: " + patient.firstname + "\n");
        builder.append("PatientLastname: " + patient.lastname + "\n");
        builder.append("PatientGender: " + patient.gender.name() + "\n");
        builder.append("PatientBday: " + patient.bday + "\n");
        builder.append("PatientStreet: " + patient.address.street + "\n");
        builder.append("PatientHnumber: " + patient.address.hnumber + "\n");
        builder.append("PatientZip: " + patient.address.zip + "\n");
        builder.append("PatientOrt: " + patient.address.ort + "\n");
        builder.append("PatientHealthCareOrganisation: " + patient.healthCareOrganisationNumber + "\n");
        builder.append("PatientHealthCareNumber: " + patient.personalHealthCareNumber + "\n");
        builder.append("PatientPhoneNumber: " + patient.phoneNumber + "\n");
        builder.append("PatientInsuranceType: " + patient.insuranceType + "\n");
        builder.append("PatientMobilityState: " + patient.mobile + "\n");
        sendSimpleMessage("covid19@drk-hannover.de", "CovidTests Privat", builder.toString());
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PutMapping(ControllerPath.FORMULAR_KVN)
    public void addFormularKvn(@RequestBody @Nonnull @NotNull @Valid FormKvnDto formValues) {
        var builder = new StringBuilder();
        builder.append("OrdererFirstName: " + formValues.orderer.firstname + "\n");
        builder.append("OrdererLastName: " + formValues.orderer.lastname + "\n");
        builder.append("OrdererLANR: " + formValues.orderer.lanr + "\n");
        builder.append("OrdererBSNR: " + formValues.orderer.bsnr + "\n");
        builder.append("OrdererStreet: " + formValues.orderer.address.street + "\n");
        builder.append("OrdererHnumber: " + formValues.orderer.address.hnumber + "\n");
        builder.append("OrdererZip: " + formValues.orderer.address.zip + "\n");
        builder.append("OrdererOrt: " + formValues.orderer.address.ort + "\n");
        builder.append("OrdererPhoneNumber: " + formValues.orderer.phoneNumber + "\n");
        builder.append("OrdererFax: " + formValues.orderer.fax + "\n");
        builder.append("OrdererEmail: " + formValues.orderer.email + "\n");
        builder.append("PatientOccupation: " + formValues.patient.occupationGroup + "\n");
        builder.append("PatientVorname: " + formValues.patient.firstname + "\n");
        builder.append("PatientLastname: " + formValues.patient.lastname + "\n");
        builder.append("PatientGender: " + formValues.patient.gender.name() + "\n");
        builder.append("PatientBday: " + formValues.patient.bday + "\n");
        builder.append("PatientStreet: " + formValues.patient.address.street + "\n");
        builder.append("PatientHnumber: " + formValues.patient.address.hnumber + "\n");
        builder.append("PatientZip: " + formValues.patient.address.zip + "\n");
        builder.append("PatientOrt: " + formValues.patient.address.ort + "\n");
        builder.append("PatientHealthCareOrganisation: " + formValues.patient.healthCareOrganisationNumber + "\n");
        builder.append("PatientHealthCareNumber: " + formValues.patient.personalHealthCareNumber + "\n");
        builder.append("PatientPhoneNumber: " + formValues.patient.phoneNumber + "\n");
        builder.append("PatientInsuranceType: " + formValues.patient.insuranceType + "\n");
        builder.append("PatientMobilityState: " + formValues.patient.mobile + "\n");
        builder.append("C19InfoDiagnose: " + formValues.info.diagnose + "\n");
        builder.append("C19InfoCriteria: " + formValues.info.rkiCrit + "\n");
        builder.append("C19InfoReason: " + formValues.info.rkiReason + "\n");
        sendSimpleMessage("covid19@drk-hannover.de", "CovidTestsKI", builder.toString());
    }

    @Autowired
    public JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setFrom("covid19@drk-hannover.de");
        message.setText(text);

        emailSender.send(message);
    }

    
}
