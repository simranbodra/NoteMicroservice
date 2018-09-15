package com.bridgelabz.microserice.fundoonotes.note.models;

import java.util.Date;
import java.util.List;

public class NoteDTO {

	private String noteId;
	private String title;
	private String description;
	private String createdAt;
	private String lastUpdated;
	private String reminder;
	private boolean pin;
	private boolean archive;
	private List<LabelDTO> listOfLabel;
	private List<URLInfo> listOfUrl;
	private List<String> listOfImage;

	public NoteDTO() {
		super();
	}

	public String getNoteId() {
		return noteId;
	}

	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getReminder() {
		return reminder;
	}

	public void setReminder(String reminder) {
		this.reminder = reminder;
	}

	public boolean getPin() {
		return this.pin;
	}

	public void setPin(boolean pin) {
		this.pin = pin;
	}

	public boolean getArchive() {
		return this.archive;
	}

	public void setArchive(boolean archive) {
		this.archive = archive;
	}

	public List<LabelDTO> getListOfLabel() {
		return this.listOfLabel;
	}

	public void setListOfLabel(List<LabelDTO> listOfLabel) {
		this.listOfLabel = listOfLabel;
	}

	public List<URLInfo> getListOfUrl() {
		return listOfUrl;
	}

	public void setListOfUrl(List<URLInfo> listOfUrl) {
		this.listOfUrl = listOfUrl;
	}

	public List<String> getListOfImage() {
		return listOfImage;
	}

	public void setListOfImage(List<String> listOfImage) {
		this.listOfImage = listOfImage;
	}

	@Override
	public String toString() {
		return "NoteDTO [noteId=" + noteId + ", title=" + title + ", description=" + description + ", createdAt="
				+ createdAt + ", lastUpdated=" + lastUpdated + ", reminder=" + reminder + ", pin=" + pin + ", archive="
				+ archive + ", listOfLabel=" + listOfLabel + ", listOfUrl=" + listOfUrl + "]";
	}

}
