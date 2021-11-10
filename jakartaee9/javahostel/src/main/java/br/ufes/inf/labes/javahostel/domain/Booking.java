package br.ufes.inf.labes.javahostel.domain;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Booking {
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private Guest guest;

	@OneToMany
	private Set<Bed> beds;

	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Temporal(TemporalType.DATE)
	private Date endDate;
}
