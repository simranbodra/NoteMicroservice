package com.bridgelabz.microserice.fundoonotes.note.exceptions;

public class NoteNotFoundException extends Exception {

	private static final long serialVersionUID = -6872447415437700174L;

	public NoteNotFoundException(String message) {
		super(message);
	}
}