package de.drkhannover.tests.api.form;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import de.drkhannover.tests.api.conf.ControllerPath;
import de.drkhannover.tests.api.form.dto.FormDto;
import de.drkhannover.tests.api.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author Marcel
 */
@RestController
public class FormController {
	
	public IUserService userService;

	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	@PutMapping(ControllerPath.FORMULAR_ADD)
	public void addFormular(@RequestBody @Nonnull @NotNull @Valid FormDto formValues, @AssertTrue boolean privacyAccepted) {
		// send email
	}
}
