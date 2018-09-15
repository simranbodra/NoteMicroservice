package com.bridgelabz.microserice.fundoonotes.note.exceptionhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
import com.bridgelabz.microserice.fundoonotes.note.models.Response;

public class NoteExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(NoteExceptionHandler.class);

	@ExceptionHandler(NoteException.class)
	public ResponseEntity<Response> handleRegistrationException(NoteException exception) {
		logger.info("Error occured while creating new node " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(91);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NoteNotFoundException.class)
	public ResponseEntity<Response> handleNoteNotFoundException(NoteNotFoundException exception) {
		logger.info("Error while searching for noteId " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(92);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ReminderException.class)
	public ResponseEntity<Response> handleReminderException(ReminderException exception) {
		logger.info("Error while setting reminder " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(4);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<Response> handleUnauthorizedException(UnauthorizedException exception) {
		logger.info("Error while authentication " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(93);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(LabelException.class)
	public ResponseEntity<Response> handleLabelException(LabelException exception) {
		logger.info("Error while operating with label " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(94);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(LabelNotFoundException.class)
	public ResponseEntity<Response> handleLabelNotFoundException(LabelNotFoundException exception) {
		logger.info("Error while searching label " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(95);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidLabelNameException.class)
	public ResponseEntity<Response> handleInvalidLabelNameException(InvalidLabelNameException exception) {
		logger.info("Error while validating label name " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(96);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(GetLinkInfoException.class)
	public ResponseEntity<Response> handleGetLinkInfoException(GetLinkInfoException exception) {
		logger.info("Error while getting link information " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(97);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(FileConversionException.class)
	public ResponseEntity<Response> handleFileConversionException(FileConversionException exception) {
		logger.info("Error in multipart file conversion " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(98);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ElasticsearchFailException.class)
	public ResponseEntity<Response> handleElasticsearchFailException(ElasticsearchFailException exception) {
		logger.info("Error while retreivinf data from elasticsearch " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(99);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> handleException(Exception exception) {
		logger.info("Something went worng " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(99);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
