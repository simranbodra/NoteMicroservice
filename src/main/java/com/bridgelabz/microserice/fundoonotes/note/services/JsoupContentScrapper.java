package com.bridgelabz.microserice.fundoonotes.note.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.bridgelabz.microserice.fundoonotes.note.exceptions.GetLinkInfoException;
import com.bridgelabz.microserice.fundoonotes.note.models.URLInfo;
import com.bridgelabz.microserice.fundoonotes.note.utility.NoteUtility;

@Service
public class JsoupContentScrapper implements ContentScrapper {

	@Override
	public URLInfo getLinkInformation(String link) throws GetLinkInfoException {
		String description = null;
		String imageUrl = null;
		try {
			Document doc = Jsoup.connect(link).get();
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

	@Override
	public List<URLInfo> getAllLink(String description) throws GetLinkInfoException {
		String[] stringArray = description.split(" ");

		List<String> urlList = NoteUtility.getUrlList(stringArray);

		List<URLInfo> urlInfoList = new ArrayList<>();
		for (int j = 0; j < urlList.size(); j++) {
			urlInfoList.add(getLinkInformation(urlList.get(j)));
		}

		return urlInfoList;
	}
}
