package com.bridgelabz.microserice.fundoonotes.note.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonInclude;

@Document(indexName = "labelsmsindex", type = "labels")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Label {

	@Id
	private String labelId;
	private String labelName;
	private String userId;
	private String createdAt;

	public Label() {
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "Label [labelId=" + labelId + ", labelName=" + labelName + ", userId=" + userId + ", createdAt="
				+ createdAt + "]";
	}
}
