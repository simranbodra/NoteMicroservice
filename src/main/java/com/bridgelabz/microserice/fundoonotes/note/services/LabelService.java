package com.bridgelabz.microserice.fundoonotes.note.services;

import java.util.List;

import com.bridgelabz.microserice.fundoonotes.note.exceptions.ElasticsearchFailException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.GetLinkInfoException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.InvalidLabelNameException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.LabelException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.LabelNotFoundException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.microserice.fundoonotes.note.models.LabelDTO;
import com.bridgelabz.microserice.fundoonotes.note.models.NoteDTO;

public interface LabelService {

	/**
	 * To create a label
	 * 
	 * @param userId
	 * @param labelName
	 * @return LabelDTO
	 * @throws LabelException
	 * @throws InvalidLabelNameException
	 * @throws ElasticsearchFailException 
	 */
	public LabelDTO createLabel(String userId, String labelName) throws LabelException, InvalidLabelNameException, ElasticsearchFailException;

	/**
	 * To list of label created
	 * 
	 * @param userId
	 * @return LabelDTO list
	 * @throws LabelNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	public List<LabelDTO> getAllLabel(String userId) throws LabelNotFoundException, ElasticsearchFailException;

	/**
	 * To update label name
	 * 
	 * @param userId
	 * @param labelId
	 * @param labelName
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	public void updateLabel(String userId, String labelId, String labelName)
			throws UnauthorizedException, LabelNotFoundException, ElasticsearchFailException;

	/**
	 * To delete a label
	 * 
	 * @param userId
	 * @param labelId
	 * @throws LabelNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	public void deleteLabel(String userId, String labelId) throws LabelNotFoundException, ElasticsearchFailException;

	/**
	 * To get all notes having a particular label
	 * 
	 * @param userId
	 * @param labelId
	 * @return
	 * @throws LabelNotFoundException
	 * @throws GetLinkInfoException
	 * @throws NoteNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	public List<NoteDTO> getLabel(String userId, String labelId)
			throws LabelNotFoundException, GetLinkInfoException, NoteNotFoundException, ElasticsearchFailException;

	/**
	 * To sort labels by name
	 * 
	 * @param userId
	 * @param format
	 * @param format
	 * @return LabelDTO list
	 * @throws LabelNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	public List<LabelDTO> sortByName(String userId, String sortType, String format) throws LabelNotFoundException, ElasticsearchFailException;

}
