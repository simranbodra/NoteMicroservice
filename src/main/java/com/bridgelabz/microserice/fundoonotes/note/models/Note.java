package com.bridgelabz.microserice.fundoonotes.note.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

@Document(indexName = "notesmsindex", type = "notes")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Note {

	@Id
	private String noteId;
	private String userId;
	private String title;
	private String description;
	private String colour;
	private String createdAt;
	private String lastUpdated;
	private String reminder;
	private boolean trash;
	private boolean pin;
	private boolean archive;
	private List<LabelDTO> listOfLabel;
	private List<URLInfo> listOfUrl;
	private List<String> listOfImage=new ArrayList<>();

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
		return "Note [noteId=" + noteId + ", userId=" + userId + ", title=" + title + ", description=" + description
				+ ", colour=" + colour + ", createdAt=" + createdAt + ", lastUpdated=" + lastUpdated + ", reminder="
				+ reminder + ", trash=" + trash + ", pin=" + pin + ", archive=" + archive + ", listOfLabel="
				+ listOfLabel + ", listOfUrl=" + listOfUrl + ", listOfImage=" + listOfImage + "]";
	}

}
