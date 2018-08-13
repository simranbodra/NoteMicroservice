package com.bridgelabz.microserice.fundoonotes.note.utility;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import com.bridgelabz.microserice.fundoonotes.note.exceptions.GetLinkInfoException;
import com.bridgelabz.microserice.fundoonotes.note.models.URLInfo;


@Component
public class LinkInfoProvider {

	public URLInfo getLinkInformation(String link) throws GetLinkInfoException {
		Document doc = null;
		String description = null;
		String imageUrl = null;
		try {
			doc = Jsoup.connect(link).get();
			description = doc.select("meta[name=description]").get(0).attr("content");
			imageUrl = doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]").attr("src");
		} catch (IOException exception) {
			throw new GetLinkInfoException("unable to fetch link information");
		}

		URLInfo urlInfo = new URLInfo();
		urlInfo.setLink(link);
		urlInfo.setImageURL(imageUrl);
		urlInfo.setDescription(description);

		return urlInfo;
	}
}
