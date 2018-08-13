package com.bridgelabz.microserice.fundoonotes.note.controllers;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.bridgelabz.microserice.fundoonotes.note.models.Response;
import com.bridgelabz.microserice.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.microserice.fundoonotes.note.services.NoteService;

@RestController
@RequestMapping("/notes")
public class NoteController {

	@Autowired
	private NoteService noteService;

	/**
	 * to create a new note
	 * 
	 * @param request
	 * @param newNote
	 * @return NoteDTO
	 * @throws NoteException
	 * @throws ReminderException
	 * @throws ParseException
	 * @throws GetLinkInfoException
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<NoteDTO> createNote(HttpServletRequest request, @RequestBody CreateNote newNote)
			throws NoteException, ReminderException, GetLinkInfoException {

		String userId = request.getHeader("UserId");

		NoteDTO noteDto = noteService.createNote(newNote, userId);

		return new ResponseEntity<>(noteDto, HttpStatus.CREATED);
	}

	/**
	 * open a note with given note Id
	 * 
	 * @param request
	 * @param noteId
	 * @return NoteDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws GetLinkInfoException
	 */
	@RequestMapping(value = "/getNote/{noteId}", method = RequestMethod.GET)
	public ResponseEntity<NoteDTO> getNote(HttpServletRequest request, @PathVariable String noteId)
			throws NoteNotFoundException, UnauthorizedException, GetLinkInfoException {
		String userId = request.getHeader("UserId");

		NoteDTO noteDto = noteService.getNote(userId, noteId);

		return new ResponseEntity<>(noteDto, HttpStatus.OK);
	}

	/**
	 * Open all note of user
	 * 
	 * @param request
	 * @return List of notes
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 */
	@RequestMapping(value = "/getAllNotes", method = RequestMethod.GET)
	public ResponseEntity<List<NoteDTO>> getAllNotes(HttpServletRequest request)
			throws NoteNotFoundException, GetLinkInfoException {
		String userId = request.getHeader("UserId");
		System.out.println(userId);

		List<NoteDTO> noteList = noteService.getAllNotes(userId);

		return new ResponseEntity<>(noteList, HttpStatus.OK);
	}

	/**
	 * Update a note
	 * 
	 * @param request
	 * @param noteId
	 * @param updateNote
	 * @return ResponseDTO
	 * @throws NoteException
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/update/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> updateNote(HttpServletRequest request, @PathVariable String noteId,
			@RequestBody UpdateNote updateNote)
			throws NoteException, NoteNotFoundException, UnauthorizedException, ReminderException {

		String userId = request.getHeader("UserId");

		noteService.updateNote(updateNote, userId, noteId);

		Response response = new Response();
		response.setMessage("Note Successfully updated");
		response.setStatus(91);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * to move a note to trash
	 * 
	 * @param request
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/delete/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> deleteNote(HttpServletRequest request, @PathVariable String noteId)
			throws NoteNotFoundException, UnauthorizedException {
		String userId = request.getHeader("UserId");

		noteService.deleteNote(userId, noteId);

		Response response = new Response();
		response.setMessage("Note Successfully moved to trash");
		response.setStatus(92);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * delete note from trash
	 * 
	 * @param request
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/permanentDeleteRestore/{noteId}", method = RequestMethod.DELETE)
	public ResponseEntity<Response> deleteFromTrash(HttpServletRequest request, @PathVariable String noteId,
			@RequestParam boolean delete) throws NoteNotFoundException, UnauthorizedException {
		String userId = request.getHeader("UserId");

		noteService.permanentNoteDelete(userId, noteId, delete);

		Response response = new Response();
		response.setMessage("Note deleted permanently");
		response.setStatus(93);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * to empty the trash notes
	 * 
	 * @param request
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 */
	@RequestMapping(value = "/emptyTrash", method = RequestMethod.DELETE)
	public ResponseEntity<Response> emptyTrash(HttpServletRequest request) throws NoteNotFoundException {
		String userId = request.getHeader("UserId");

		noteService.emptyTrash(userId);

		Response response = new Response();
		response.setMessage("Trash is emptied");
		response.setStatus(94);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To view trash notes
	 * 
	 * @param request
	 * @return list of trash notes
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 */
	@RequestMapping(value = "/getTrash", method = RequestMethod.GET)
	public ResponseEntity<List<NoteDTO>> getTrash(HttpServletRequest request)
			throws NoteNotFoundException, GetLinkInfoException {
		String userId = request.getHeader("UserId");

		List<NoteDTO> trashList = noteService.getTrash(userId);

		return new ResponseEntity<>(trashList, HttpStatus.OK);
	}

	/**
	 * To set color on the note
	 * 
	 * @param request
	 * @param noteId
	 * @param colour
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws NoteException
	 */
	@RequestMapping(value = "/setColour/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> addColour(HttpServletRequest request, @PathVariable String noteId,
			@RequestParam String colour) throws NoteNotFoundException, UnauthorizedException, NoteException {
		String userId = request.getHeader("UserId");

		noteService.addColour(userId, noteId, colour);

		Response response = new Response();
		response.setMessage("Colour add to the note");
		response.setStatus(20);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * to add reminder to the note
	 * 
	 * @param request
	 * @param noteId
	 * @param reminderDate
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/addReminder/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> addReminder(HttpServletRequest request, @PathVariable String noteId,
			@RequestParam String reminder) throws NoteNotFoundException, UnauthorizedException, ReminderException {
		String userId = request.getHeader("UserId");

		noteService.addNoteReminder(userId, noteId, reminder);

		Response response = new Response();
		response.setMessage("Reminder added to the note");
		response.setStatus(80);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * to remove reminder on the note
	 * 
	 * @param request
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/removeReminder/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> removeReminder(HttpServletRequest request, @PathVariable String noteId)
			throws NoteNotFoundException, UnauthorizedException {
		String userId = request.getHeader("UserId");

		noteService.removeReminder(userId, noteId);

		Response response = new Response();
		response.setMessage("Removed reminder on the note");
		response.setStatus(81);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To add pin to the note
	 * 
	 * @param request
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/addPin/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> addPin(HttpServletRequest request, @PathVariable String noteId)
			throws NoteNotFoundException, UnauthorizedException {
		String userId = request.getHeader("UserId");

		noteService.addPin(userId, noteId);

		Response response = new Response();
		response.setMessage("Pinned the note");
		response.setStatus(70);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Remove pin on the note
	 * 
	 * @param request
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/removePin/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> removePin(HttpServletRequest request, @PathVariable String noteId)
			throws NoteNotFoundException, UnauthorizedException {
		String userId = request.getHeader("UserId");

		noteService.removePin(userId, noteId);

		Response response = new Response();
		response.setMessage("Pin removed on the note");
		response.setStatus(71);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Add note to archive
	 * 
	 * @param request
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/addToArchive/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> addArchive(HttpServletRequest request, @PathVariable String noteId)
			throws NoteNotFoundException, UnauthorizedException {
		String userId = request.getHeader("UserId");

		noteService.archiveNote(userId, noteId);

		Response response = new Response();
		response.setMessage("Archived the note");
		response.setStatus(60);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * remove note from archive
	 * 
	 * @param request
	 * @param noteId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@RequestMapping(value = "/removeFromArchive/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> removeArchive(HttpServletRequest request, @PathVariable String noteId)
			throws NoteNotFoundException, UnauthorizedException {
		String userId = request.getHeader("UserId");

		noteService.removeArchiveNote(userId, noteId);

		Response response = new Response();
		response.setMessage("Remove archive note");
		response.setStatus(61);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * View archived notes
	 * 
	 * @param request
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 */
	@RequestMapping(value = "/getArchiveNotes", method = RequestMethod.POST)
	public ResponseEntity<List<NoteDTO>> viewArchiveNotes(HttpServletRequest request)
			throws NoteNotFoundException, GetLinkInfoException {
		String userId = request.getHeader("UserId");

		List<NoteDTO> archivedNoteList = noteService.getArchivedNote(userId);

		return new ResponseEntity<>(archivedNoteList, HttpStatus.OK);
	}

	/**
	 * Add label to the note
	 * 
	 * @param request
	 * @param noteId
	 * @param labelName
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelException
	 * @throws LabelNotFoundException
	 * @throws InvalidLabelNameException
	 */
	@RequestMapping(value = "/addLabelToNote/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> addLabel(HttpServletRequest request, @PathVariable String noteId,
			@RequestParam String labelName) throws NoteNotFoundException, UnauthorizedException, LabelException,
			LabelNotFoundException, InvalidLabelNameException {
		String userId = request.getHeader("UserId");

		noteService.addLabel(userId, noteId, labelName);

		Response response = new Response();
		response.setMessage("Label added to the note");
		response.setStatus(201);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To delete label from a note
	 * 
	 * @param request
	 * @param noteId
	 * @param labelId
	 * @return ResponseDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 */
	@RequestMapping(value = "/deleteLabelFromNote/{noteId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> deteleLabelFromNote(HttpServletRequest request, @PathVariable String noteId,
			@RequestParam String labelId) throws NoteNotFoundException, UnauthorizedException, LabelNotFoundException {
		String userId = request.getHeader("UserId");

		noteService.deleteNoteLabel(userId, noteId, labelId);

		Response response = new Response();
		response.setMessage("Label deleted from note");
		response.setStatus(203);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
