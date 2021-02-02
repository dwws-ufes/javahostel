package br.ufes.informatica.javahostel.control;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.enterprise.inject.Model;

import br.ufes.informatica.javahostel.application.RegistrationService;
import br.ufes.informatica.javahostel.application.UnderAgeGuestException;
import br.ufes.informatica.javahostel.domain.Guest;

@Model
public class RegistrationController implements Serializable {
	@EJB
	private RegistrationService registrationService;
	private Guest guest = new Guest();
	private int age;

	public Guest getGuest() {
		return guest;
	}

	public int getAge() {
		return age;
	}

	public String register() {
		try {
			registrationService.register(guest);
		} catch (UnderAgeGuestException e) {
			age = e.getAge();
			return "/registration/underage.xhtml";
		}
		return "/registration/success.xhtml";
	}

}
