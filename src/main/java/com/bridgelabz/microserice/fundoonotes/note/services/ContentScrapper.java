package com.bridgelabz.microserice.fundoonotes.note.services;

import java.util.List;

import com.bridgelabz.microserice.fundoonotes.note.exceptions.GetLinkInfoException;
import com.bridgelabz.microserice.fundoonotes.note.models.URLInfo;

public interface ContentScrapper {

	/**
	 * To get the information for the link
	 * 
	 * @param link
	 * @return URLInfo DTO
	 * @throws GetLinkInfoException
	 */
	public URLInfo getLinkInformation(String link) throws GetLinkInfoException;

	/**
	 * To get all link
	 * 
	 * @param description
	 * @return
	 * @throws GetLinkInfoException
	 */
	public List<URLInfo> getAllLink(String description) throws GetLinkInfoException;
}
