package com.bridgelabz.microserice.fundoonotes.note.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.microserice.fundoonotes.note.exceptions.ElasticsearchFailException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.GetLinkInfoException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.InvalidLabelNameException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.LabelException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.LabelNotFoundException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.microserice.fundoonotes.note.models.LabelDTO;
import com.bridgelabz.microserice.fundoonotes.note.models.NoteDTO;
import com.bridgelabz.microserice.fundoonotes.note.models.Response;
import com.bridgelabz.microserice.fundoonotes.note.services.LabelService;

@RestController
@RequestMapping("/labels")
public class LabelController {

	@Autowired
	private LabelService labelService;

	/**
	 * to create a new Label
	 * 
	 * @param request
	 * @param labelName
	 * @return ResponseDTO
	 * @throws LabelException
	 * @throws InvalidLabelNameException
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/createLabel", method = RequestMethod.POST)
	public ResponseEntity<LabelDTO> createLabel(HttpServletRequest request, @RequestParam String labelName)
			throws LabelException, InvalidLabelNameException, ElasticsearchFailException {
		String userId = request.getHeader("UserId");

		LabelDTO labelDto = labelService.createLabel(userId, labelName);

		return new ResponseEntity<>(labelDto, HttpStatus.OK);
	}

	/***
	 * To get all labels
	 * 
	 * @param request
	 * @param labelId
	 * @return list of LabelDTO
	 * @throws LabelNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/getLabels", method = RequestMethod.GET)
	public ResponseEntity<List<LabelDTO>> getAllLabel(HttpServletRequest request) throws LabelNotFoundException, ElasticsearchFailException {
		String userId = request.getHeader("UserId");

		List<LabelDTO> labelList = labelService.getAllLabel(userId);

		return new ResponseEntity<>(labelList, HttpStatus.OK);
	}

	/**
	 * to update label name
	 * 
	 * @param request
	 * @param labelId
	 * @param labelName
	 * @return ResponseDTO
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/updateLabel/{labelId}", method = RequestMethod.PUT)
	public ResponseEntity<Response> editLabel(HttpServletRequest request, @PathVariable String labelId,
			@RequestParam String labelName) throws UnauthorizedException, LabelNotFoundException, ElasticsearchFailException {
		String userId = request.getHeader("UserId");

		labelService.updateLabel(userId, labelId, labelName);

		Response response = new Response();
		response.setMessage("Label edited");
		response.setStatus(202);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To delete label
	 * 
	 * @param request
	 * @param labelId
	 * @return ResponseDTO
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/deleteLabel/{labelId}", method = RequestMethod.DELETE)
	public ResponseEntity<Response> deleteLabel(HttpServletRequest request, @PathVariable String labelId)
			throws LabelNotFoundException, ElasticsearchFailException {
		String userId = request.getHeader("UserId");

		labelService.deleteLabel(userId, labelId);

		Response response = new Response();
		response.setMessage("Label deleted");
		response.setStatus(202);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To get all notes by label Id
	 * 
	 * @param request
	 * @param labelId
	 * @return List of NoteDTO
	 * @throws LabelNotFoundException
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/getLabel{labelId}", method = RequestMethod.POST)
	public ResponseEntity<List<NoteDTO>> getLabel(HttpServletRequest request, @PathVariable String labelId)
			throws LabelNotFoundException, GetLinkInfoException, NoteNotFoundException, ElasticsearchFailException {
		String userId = request.getHeader("UserId");

		List<NoteDTO> notes = labelService.getLabel(userId, labelId);

		return new ResponseEntity<>(notes, HttpStatus.OK);
	}

	/**
	 * To sort labels by name
	 * 
	 * @param request
	 * @param format
	 * @return List of NoteDTO
	 * @throws LabelNotFoundException
	 * @throws ElasticsearchFailException 
	 */
	@RequestMapping(value = "/sortByNameOrDate", method = RequestMethod.GET)
	public ResponseEntity<List<LabelDTO>> sortLabelsByName(HttpServletRequest request,
			@RequestParam(required = false) String sortType, @RequestParam(required = false) String format)
			throws LabelNotFoundException, ElasticsearchFailException {
		String userId = (String) request.getAttribute("UserId");

		List<LabelDTO> noteDtoList = labelService.sortByName(userId, sortType, format);

		return new ResponseEntity<>(noteDtoList, HttpStatus.OK);
	}
}