package com.bridgelabz.microserice.fundoonotes.note.services;

import java.util.List;

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
	 * @param userId
	 * @param labelName
	 * @return LabelDTO
	 * @throws LabelException
	 * @throws InvalidLabelNameException
	 */
	public LabelDTO createLabel(String userId, String labelName) throws LabelException, InvalidLabelNameException;

	/**
	 * To list of label created
	 * @param userId
	 * @return LabelDTO list
	 * @throws LabelNotFoundException
	 */
	public List<LabelDTO> getAllLabel(String userId) throws LabelNotFoundException;

	/**
	 * To update label name
	 * @param userId
	 * @param labelId
	 * @param labelName
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 */
	public void updateLabel(String userId, String labelId, String labelName)
			throws UnauthorizedException, LabelNotFoundException;
	
	/**
	 * To delete a label
	 * @param userId
	 * @param labelId
	 * @throws LabelNotFoundException
	 */
	public void deleteLabel(String userId, String labelId) throws LabelNotFoundException;
	
	/**
	 * To get all notes having a particular label
	 * @param userId
	 * @param labelId
	 * @return
	 * @throws LabelNotFoundException
	 * @throws GetLinkInfoException 
	 * @throws NoteNotFoundException 
	 */
	public List<NoteDTO> getLabel(String userId, String labelId) throws LabelNotFoundException, GetLinkInfoException, NoteNotFoundException;
}
