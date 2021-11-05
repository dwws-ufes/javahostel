package br.ufes.informatica.javahostel.application;

public class UnderAgeGuestException extends Exception {
	private int age; 

	public UnderAgeGuestException(int age) { this. age = age; } 

	public int getAge() { return age; }

}
