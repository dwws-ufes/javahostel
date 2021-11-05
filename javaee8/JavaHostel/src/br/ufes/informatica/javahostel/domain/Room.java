package br.ufes.informatica.javahostel.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Room {
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private int number;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "room")
	private Set<Bed> beds;
}
