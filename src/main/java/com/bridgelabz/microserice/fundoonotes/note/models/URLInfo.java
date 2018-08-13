package com.bridgelabz.microserice.fundoonotes.note.models;

public class URLInfo {

	private String imageURL;
	private String description;
	private String link;

	public URLInfo() {
		super();
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "URLInfo [imageURL=" + imageURL + ", description=" + description + ", link=" + link + "]";
	}
}
