package br.ufes.inf.labes.javahostel.application;

import java.util.Calendar;
import java.util.Date;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import br.ufes.inf.labes.javahostel.domain.Guest;

@Stateless
@LocalBean
public class RegistrationService {
	@PersistenceContext
	private EntityManager entityManager;

	public void register(Guest guest) throws UnderAgeGuestException {
		int age = calculateAge(guest.getBirthDate());
		if (age < 18)
			throw new UnderAgeGuestException(age);
		entityManager.persist(guest);
	}

	private static int calculateAge(Date birthDate) {
		if (birthDate == null)
			return 0;
		Calendar birth = Calendar.getInstance();
		birth.setTime(birthDate);
		Calendar today = Calendar.getInstance();
		today.setTime(new Date(System.currentTimeMillis()));
		int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
		birth.add(Calendar.YEAR, age);
		if (birth.after(today))
			age--;
		return age;
	}

}
