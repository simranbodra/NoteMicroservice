package com.bridgelabz.microserice.fundoonotes.note.services;

import java.text.ParseException;
import java.util.List;

import com.bridgelabz.microserice.fundoonotes.note.exceptions.GetLinkInfoException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.InvalidLabelNameException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.LabelException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.LabelNotFoundException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.ReminderException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.microserice.fundoonotes.note.models.CreateNote;
import com.bridgelabz.microserice.fundoonotes.note.models.NoteDTO;
import com.bridgelabz.microserice.fundoonotes.note.models.UpdateNote;

public interface NoteService {

	/**
	 * create a new note
	 * 
	 * @param newNote
	 * @param jwToken
	 * @return ViewNoteDTO
	 * @throws NoteException
	 * @throws ReminderException
	 * @throws ParseException
	 * @throws GetLinkInfoException
	 */
	public NoteDTO createNote(CreateNote newNote, String userId)
			throws NoteException, ReminderException, GetLinkInfoException;

	/**
	 * To update a note
	 * 
	 * @param updateNote
	 * @param userId
	 * @param noteId
	 * @throws NoteException
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 * @throws ParseException
	 */
	public void updateNote(UpdateNote updateNote, String userId, String noteId)
			throws NoteException, NoteNotFoundException, UnauthorizedException, ReminderException;

	/**
	 * Move note to trash
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void deleteNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To remove note from trash
	 * 
	 * @param userId
	 * @param noteId
	 * @param restore
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void permanentNoteDelete(String userId, String noteId, boolean restore)
			throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To get a note
	 * 
	 * @param userId
	 * @param noteId
	 * @return NoteDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws GetLinkInfoException
	 */
	public NoteDTO getNote(String userId, String noteId)
			throws NoteNotFoundException, UnauthorizedException, GetLinkInfoException;

	/**
	 * To get list of notes
	 * 
	 * @param userId
	 * @return list of NoteDTO
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 */
	public List<NoteDTO> getAllNotes(String userId) throws NoteNotFoundException, GetLinkInfoException;

	/**
	 * To empty trash
	 * 
	 * @param userId
	 * @throws NoteNotFoundException
	 */
	public void emptyTrash(String userId) throws NoteNotFoundException;

	/**
	 * To get trash notes
	 * 
	 * @param userId
	 * @return list of NoteDTO
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 */
	public List<NoteDTO> getTrash(String userId) throws NoteNotFoundException, GetLinkInfoException;

	/**
	 * To set color on the note
	 * 
	 * @param userId
	 * @param noteId
	 * @param colour
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws NoteException
	 */
	public void addColour(String userId, String noteId, String colour)
			throws NoteNotFoundException, UnauthorizedException, NoteException;

	/**
	 * To add reminder to note
	 * 
	 * @param userId
	 * @param noteId
	 * @param reminderDate
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 * @throws ParseException
	 */
	public void addNoteReminder(String userId, String noteId, String reminderDate)
			throws NoteNotFoundException, UnauthorizedException, ReminderException;

	/**
	 * To remove reminder from note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void removeReminder(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To pin a note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void addPin(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To remove pin on note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void removePin(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To archive a note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void archiveNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * TO remove note from archive
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	public void removeArchiveNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException;

	/**
	 * To get archive notes
	 * 
	 * @param userId
	 * @return list of NoteDTO
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 */
	public List<NoteDTO> getArchivedNote(String userId) throws NoteNotFoundException, GetLinkInfoException;

	/**
	 * To add a label to note
	 * 
	 * @param userId
	 * @param noteId
	 * @param labelName
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelException
	 * @throws LabelNotFoundException
	 * @throws InvalidLabelNameException
	 */
	public void addLabel(String userId, String noteId, String labelName) throws NoteNotFoundException,
			UnauthorizedException, LabelException, LabelNotFoundException, InvalidLabelNameException;

	/**
	 * To delete a label from a note
	 * 
	 * @param userId
	 * @param noteId
	 * @param labelId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 */
	public void deleteNoteLabel(String userId, String noteId, String labelId)
			throws NoteNotFoundException, UnauthorizedException, LabelNotFoundException;

}
