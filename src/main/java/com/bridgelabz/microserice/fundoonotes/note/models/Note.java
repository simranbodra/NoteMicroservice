package com.bridgelabz.microserice.fundoonotes.note.models;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "notesmsindex", type = "note")
public class Note {

	@Id
	private String noteId;
	private String userId;
	private String title;
	private String description;
	private String colour;
	private Date createdAt;
	private Date lastUpdated;
	private String reminder;
	private boolean trash;
	private boolean pin;
	private boolean archive;
	private List<LabelDTO> listOfLabel;
	private List<String> listOfUrl;

	public Note() {
		super();
	}

	public String getNoteId() {
		return noteId;
	}

	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getReminder() {
		return reminder;
	}

	public void setReminder(String reminder) {
		this.reminder = reminder;
	}

	public boolean getTrash() {
		return trash;
	}

	public void setTrash(boolean trash) {
		this.trash = trash;
	}

	public boolean getPin() {
		return pin;
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

	public List<String> getListOfUrl() {
		return listOfUrl;
	}

	public void setListOfUrl(List<String> listOfUrl) {
		this.listOfUrl = listOfUrl;
	}

	@Override
	public String toString() {
		return "Note [noteId=" + noteId + ", userId=" + userId + ", title=" + title + ", description=" + description
				+ ", colour=" + colour + ", createdAt=" + createdAt + ", lastUpdated=" + lastUpdated + ", reminder="
				+ reminder + ", trash=" + trash + ", pin=" + pin + ", archive=" + archive + ", listOfLabel="
				+ listOfLabel + ", listOfUrl=" + listOfUrl + "]";
	}

}
