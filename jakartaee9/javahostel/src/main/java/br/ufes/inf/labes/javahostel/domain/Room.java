package br.ufes.inf.labes.javahostel.domain;

import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Room {
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private int number;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "room")
	private Set<Bed> beds;
}
