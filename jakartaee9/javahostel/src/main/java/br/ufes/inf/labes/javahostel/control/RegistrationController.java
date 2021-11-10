package br.ufes.inf.labes.javahostel.control;

import java.io.Serializable;

import jakarta.ejb.EJB;
import jakarta.enterprise.inject.Model;

import br.ufes.inf.labes.javahostel.application.RegistrationService;
import br.ufes.inf.labes.javahostel.application.UnderAgeGuestException;
import br.ufes.inf.labes.javahostel.domain.Guest;

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
