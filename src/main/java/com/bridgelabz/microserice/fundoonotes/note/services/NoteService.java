package com.bridgelabz.microserice.fundoonotes.note.services;

import java.text.ParseException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.microserice.fundoonotes.note.exceptions.ElasticsearchFailException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.FileConversionException;
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
	 * @throws ElasticsearchFailException 
	 */
	public NoteDTO createNote(CreateNote newNote, String userId)
			throws NoteException, ReminderException, GetLinkInfoException, ParseException, ElasticsearchFailException;

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
	 * @throws GetLinkInfoException 
	 * @throws ParseException
	 * @throws ElasticsearchFailException 
	 */
	public void updateNote(UpdateNote updateNote, String userId, String noteId)
			throws NoteException, NoteNotFoundException, UnauthorizedException, ReminderException, ParseException, GetLinkInfoException, ElasticsearchFailException;

	/**
	 * Move note to trash
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException 
	 */
	public void deleteNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException;

	/**
	 * To remove note from trash
	 * 
	 * @param userId
	 * @param noteId
	 * @param restore
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException 
	 */
	public void permanentNoteDelete(String userId, String noteId, boolean restore)
			throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException;

	/**
	 * To get a note
	 * 
	 * @param userId
	 * @param noteId
	 * @return NoteDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws GetLinkInfoException
	 * @throws ElasticsearchFailException 
	 */
	public NoteDTO getNote(String userId, String noteId)
			throws NoteNotFoundException, UnauthorizedException, GetLinkInfoException, ElasticsearchFailException;

	/**
	 * To get list of notes
	 * 
	 * @param userId
	 * @return list of NoteDTO
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 * @throws ElasticsearchFailException 
	 */
	public List<NoteDTO> getAllNotes(String userId) throws NoteNotFoundException, GetLinkInfoException, ElasticsearchFailException;

	/**
	 * To empty trash
	 * 
	 * @param userId
	 * @throws NoteNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	public void emptyTrash(String userId) throws NoteNotFoundException, ElasticsearchFailException;

	/**
	 * To get trash notes
	 * 
	 * @param userId
	 * @return list of NoteDTO
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 * @throws ElasticsearchFailException 
	 */
	public List<NoteDTO> getTrash(String userId) throws NoteNotFoundException, GetLinkInfoException, ElasticsearchFailException;

	/**
	 * To set color on the note
	 * 
	 * @param userId
	 * @param noteId
	 * @param colour
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws NoteException
	 * @throws ElasticsearchFailException 
	 */
	public void addColour(String userId, String noteId, String colour)
			throws NoteNotFoundException, UnauthorizedException, NoteException, ElasticsearchFailException;

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
	 * @throws ElasticsearchFailException 
	 */
	public void addNoteReminder(String userId, String noteId, String reminderDate)
			throws NoteNotFoundException, UnauthorizedException, ReminderException, ParseException, ElasticsearchFailException;

	/**
	 * To remove reminder from note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException 
	 */
	public void removeReminder(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException;

	/**
	 * To pin a note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException 
	 */
	public void addPin(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException;

	/**
	 * To remove pin on note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException 
	 */
	public void removePin(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException;

	/**
	 * To archive a note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException 
	 */
	public void archiveNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException;

	/**
	 * TO remove note from archive
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException 
	 */
	public void removeArchiveNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException;

	/**
	 * To get archive notes
	 * 
	 * @param userId
	 * @return list of NoteDTO
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 * @throws ElasticsearchFailException 
	 */
	public List<NoteDTO> getArchivedNote(String userId) throws NoteNotFoundException, GetLinkInfoException, ElasticsearchFailException;

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
	 * @throws ElasticsearchFailException 
	 */
	public void addLabel(String userId, String noteId, String labelName) throws NoteNotFoundException,
			UnauthorizedException, LabelException, LabelNotFoundException, InvalidLabelNameException, ElasticsearchFailException;

	/**
	 * To delete a label from a note
	 * 
	 * @param userId
	 * @param noteId
	 * @param labelId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	public void deleteNoteLabel(String userId, String noteId, String labelId)
			throws NoteNotFoundException, UnauthorizedException, LabelNotFoundException, ElasticsearchFailException;

	/**
	 * To sort note by name
	 * 
	 * @param userId
	 * @param format
	 * @param format2
	 * @return NoteDTO List
	 * @throws NoteNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	public List<NoteDTO> sortByTitleOrDate(String userId, String format, String format2) throws NoteNotFoundException, ElasticsearchFailException;

	/**
	 * To add image to note
	 * @param userId
	 * @param noteId
	 * @param image
	 * @return NoteDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws FileConversionException 
	 * @throws ElasticsearchFailException 
	 */
	public NoteDTO addImage(String userId, String noteId, MultipartFile image) throws NoteNotFoundException, UnauthorizedException, FileConversionException, ElasticsearchFailException;

	/**
	 * To remove image from note
	 * @param userId
	 * @param noteId
	 * @param imageName
	 * @return NoteDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException 
	 */
	public NoteDTO removeImage(String userId, String noteId, String imageUrl) throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException;

	/**
	 * To get image on the note
	 * @param userId
	 * @param noteId
	 * @param imageName
	 * @return Image URL
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException 
	 */
	public String getImageUrl(String userId, String noteId, String imageName) throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException;

}
