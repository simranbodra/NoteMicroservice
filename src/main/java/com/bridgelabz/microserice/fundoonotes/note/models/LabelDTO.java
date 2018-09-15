package com.bridgelabz.microserice.fundoonotes.note.models;

import java.util.Date;

public class LabelDTO {

	private String labelId;
	private String labelName;
	private String createdAt;

	public LabelDTO() {
		super();
	}

	public String getLabelId() {
		return labelId;
	}

	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "LabelDTO [labelId=" + labelId + ", labelName=" + labelName + ", createdAt=" + createdAt + "]";
	}
}
