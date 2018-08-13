package com.bridgelabz.microserice.fundoonotes.note.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bridgelabz.microserice.fundoonotes.note.exceptions.GetLinkInfoException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.InvalidLabelNameException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.LabelException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.LabelNotFoundException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.ReminderException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.microserice.fundoonotes.note.models.CreateNote;
import com.bridgelabz.microserice.fundoonotes.note.models.Label;
import com.bridgelabz.microserice.fundoonotes.note.models.LabelDTO;
import com.bridgelabz.microserice.fundoonotes.note.models.Note;
import com.bridgelabz.microserice.fundoonotes.note.models.NoteDTO;
import com.bridgelabz.microserice.fundoonotes.note.models.URLInfo;
import com.bridgelabz.microserice.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.microserice.fundoonotes.note.repositories.LabelElasticsearchRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.LabelRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.NoteElasticsearchRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.NoteRepository;
import com.bridgelabz.microserice.fundoonotes.note.utility.LinkInfoProvider;
import com.bridgelabz.microserice.fundoonotes.note.utility.NoteUtility;

@Service
public class NoteServiceImpl implements NoteService {

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private NoteElasticsearchRepository noteElasticsearchRepository;

	@Autowired
	private LabelElasticsearchRepository labelElasticsearchRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private Environment environment;

	@Autowired
	private LinkInfoProvider linkInfoProvider;

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
	@Override
	public NoteDTO createNote(CreateNote newNote, String userId)
			throws NoteException, ReminderException, GetLinkInfoException {
		NoteUtility.validateNewNote(newNote);

		Note note = new Note();
		note.setTitle(newNote.getTitle());
		note.setDescription(newNote.getDescription());
		note.setCreatedAt(NoteUtility.getCurrentDate());
		note.setLastUpdated(NoteUtility.getCurrentDate());
		note.setUserId(userId);

		if (newNote.getColour() != null || newNote.getColour().trim().length() != 0) {
			note.setColour(newNote.getColour());
		}
		if (newNote.getReminder() != null || newNote.getReminder().trim().length() != 0) {
			NoteUtility.validateDate(newNote.getReminder());
			note.setReminder(newNote.getReminder());
		}

		note.setPin(newNote.getPin());
		note.setArchive(newNote.getArchive());

		List<String> labelNameList = newNote.getListOfLabel();

		List<Label> userLabels = labelRepository.findAllByUserId(userId);

		ArrayList<String> userLabelList = new ArrayList<>();
		for (int j = 0; j < userLabels.size(); j++) {
			userLabelList.add(userLabels.get(j).getLabelName());
		}
		ArrayList<LabelDTO> toBeAddedLabels = new ArrayList<>();

		for (int i = 0; i < labelNameList.size(); i++) {
			if (labelNameList.get(i) != null || labelNameList.get(i).trim().equals("")) {

				if (!userLabelList.contains(labelNameList.get(i))) {
					Label label = new Label();
					label.setLabelName(labelNameList.get(i));
					label.setUserId(userId);

					labelRepository.save(label);

					labelElasticsearchRepository.save(label);

					LabelDTO viewLabelToSave = new LabelDTO();
					viewLabelToSave.setLabelId(label.getLabelId());
					viewLabelToSave.setLabelName(label.getLabelName());
					toBeAddedLabels.add(viewLabelToSave);

				} else {
					List<Label> optionalLabelToSave = labelRepository.findAllByLabelName(labelNameList.get(i));

					for (int j = 0; j < optionalLabelToSave.size(); j++) {
						if (optionalLabelToSave.get(j).getUserId().equalsIgnoreCase(userId)) {
							LabelDTO viewLabel = new LabelDTO();
							viewLabel.setLabelName(optionalLabelToSave.get(j).getLabelName());
							viewLabel.setLabelId(optionalLabelToSave.get(j).getLabelId());
							toBeAddedLabels.add(viewLabel);
						}
					}

				}

			}
		}

		note.setListOfLabel(toBeAddedLabels);

		String[] stringArray = newNote.getDescription().split(" ");

		List<String> urlList = NoteUtility.getUrlList(stringArray);

		note.setListOfUrl(urlList);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);

		NoteDTO noteDto = modelMapper.map(note, NoteDTO.class);

		List<URLInfo> urlInfoList = new ArrayList<>();
		for (int j = 0; j < urlList.size(); j++) {
			urlInfoList.add(linkInfoProvider.getLinkInformation(urlList.get(j)));
		}
		noteDto.setListOfUrl(urlInfoList);

		return noteDto;
	}

	/**
	 * view a note
	 * 
	 * @param userId
	 * @param noteId
	 * @return ViewNoteDTO
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws GetLinkInfoException
	 */
	public NoteDTO getNote(String userId, String noteId)
			throws NoteNotFoundException, UnauthorizedException, GetLinkInfoException {

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();

		NoteDTO noteDto = modelMapper.map(note, NoteDTO.class);

		List<String> urlList = note.getListOfUrl();
		List<URLInfo> urlInfoList = new ArrayList<>();
		for (int j = 0; j < urlList.size(); j++) {
			urlInfoList.add(linkInfoProvider.getLinkInformation(urlList.get(j)));
		}
		noteDto.setListOfUrl(urlInfoList);

		return noteDto;

	}

	/**
	 * view a list of note owned by the user
	 * 
	 * @param userId
	 * @return list of ViewNoteDTO
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 */
	public List<NoteDTO> getAllNotes(String userId) throws NoteNotFoundException, GetLinkInfoException {

		List<Note> noteList = noteRepository.findAllByUserIdAndTrash(userId, false);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No Note Found");
		}

		List<NoteDTO> noteDtos = noteList.stream().map(filterNote -> modelMapper.map(filterNote, NoteDTO.class))
				.collect(Collectors.toList());

		for (int i = 0; i < noteList.size(); i++) {
			List<String> urlList = noteList.get(i).getListOfUrl();

			List<URLInfo> urlInfoList = new ArrayList<>();

			for (int j = 0; j < urlList.size(); j++) {
				urlInfoList.add(linkInfoProvider.getLinkInformation(urlList.get(j)));
			}
			noteDtos.get(i).setListOfUrl(urlInfoList);
		}

		List<NoteDTO> pinnedNoteDtoList = noteDtos.stream().filter(NoteDTO::getPin).collect(Collectors.toList());

		List<NoteDTO> unpinnedNoteDtoList = noteDtos.stream().filter(noteStream -> !noteStream.getPin())
				.collect(Collectors.toList());

		List<NoteDTO> noteDtoList = Stream.concat(pinnedNoteDtoList.stream(), unpinnedNoteDtoList.stream())
				.collect(Collectors.toList());

		return noteDtoList;

	}

	/**
	 * update a note
	 * 
	 * @param updateNoteDTO
	 * @param userId
	 * @param noteId
	 * @throws NoteException
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 * @throws ParseException
	 */
	@Override
	public void updateNote(UpdateNote updateNote, String userId, String noteId)
			throws NoteException, NoteNotFoundException, UnauthorizedException, ReminderException{

		Optional<Note> optionalNote = noteRepository.findByNoteIdAndUserId(noteId, userId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}
		Note note = optionalNote.get();
		if (updateNote.getTitle() != null || updateNote.getTitle().trim().length() != 0) {
			note.setTitle(updateNote.getTitle());
			note.setLastUpdated(NoteUtility.getCurrentDate());
		}
		if (updateNote.getDescription() != null || updateNote.getDescription().trim().length() != 0) {
			note.setDescription(updateNote.getDescription());
			note.setLastUpdated(NoteUtility.getCurrentDate());
		}
		if (NoteUtility.validateDate(updateNote.getReminder())) {
			note.setReminder(updateNote.getReminder());
			note.setLastUpdated(NoteUtility.getCurrentDate());
		}
		if (updateNote.getColour() != null || updateNote.getColour().trim().length() != 0) {
			note.setColour(updateNote.getColour());
		}

		String[] stringArray = note.getDescription().split(" ");

		List<String> urlList = NoteUtility.getUrlList(stringArray);

		note.setListOfUrl(urlList);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * move a note to trash
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void deleteNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setTrash(true);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * delete a note from trash
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void permanentNoteDelete(String userId, String noteId, boolean delete)
			throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		if (!optionalNote.get().getTrash()) {
			throw new NoteNotFoundException("No such note found in trash");
		}

		if (delete) {
			noteRepository.deleteById(noteId);

			noteElasticsearchRepository.deleteById(noteId);
		} else {
			Note note = optionalNote.get();
			note.setTrash(false);
			noteRepository.save(note);

			noteElasticsearchRepository.save(note);
		}

	}

	/**
	 * delete all notes in trash
	 * 
	 * @param userId
	 * @throws NoteNotFoundException
	 */
	@Override
	public void emptyTrash(String userId) throws NoteNotFoundException {

		List<Note> noteList = noteRepository.findAllByUserId(userId);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No Note Found");
		}

		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);
			if (noteList.get(i).getTrash()) {
				noteRepository.deleteById(note.getNoteId());

				noteElasticsearchRepository.deleteById(note.getNoteId());
			}

		}
	}

	/**
	 * To view all trashed notes
	 * 
	 * @param userId
	 * @return list of notes
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 * 
	 */
	@Override
	public List<NoteDTO> getTrash(String userId) throws NoteNotFoundException, GetLinkInfoException {
		List<Note> noteList = noteRepository.findAllByUserIdAndTrash(userId, true);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No Note Found");
		}

		List<NoteDTO> noteDtos = noteList.stream().map(filterNote -> modelMapper.map(filterNote, NoteDTO.class))
				.collect(Collectors.toList());

		for (int i = 0; i < noteList.size(); i++) {
			List<String> urlList = noteList.get(i).getListOfUrl();

			List<URLInfo> urlInfoList = new ArrayList<>();

			for (int j = 0; j < urlList.size(); j++) {
				urlInfoList.add(linkInfoProvider.getLinkInformation(urlList.get(j)));
			}
			noteDtos.get(i).setListOfUrl(urlInfoList);
		}

		return noteDtos;
	}

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
	@Override
	public void addColour(String userId, String noteId, String colour)
			throws NoteNotFoundException, UnauthorizedException, NoteException {
		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		if (colour == null || colour.trim().length() == 0) {
			throw new NoteException("Color cannot be empty");
		}
		Note note = optionalNote.get();
		note.setColour(colour);
		note.setLastUpdated(NoteUtility.getCurrentDate());

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * to add reminder to the note
	 * 
	 * @param userId
	 * @param noteId
	 * @param reminderDate
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ReminderException
	 * @throws ParseException
	 */
	@Override
	public void addNoteReminder(String userId, String noteId, String reminderDate)
			throws NoteNotFoundException, UnauthorizedException, ReminderException {

		NoteUtility.validateDate(reminderDate);

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setReminder(reminderDate);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * to remove reminder from the note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void removeReminder(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setReminder(null);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * Add pin to the note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * 
	 */
	@Override
	public void addPin(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();

		if (!note.getPin()) {
			note.setArchive(false);
			note.setPin(true);
		} else {
			note.setPin(false);
		}

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * Removes pin on the note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * 
	 */
	@Override
	public void removePin(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setPin(false);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * Adding note to archive
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * 
	 */
	@Override
	public void archiveNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setPin(false);
		note.setArchive(true);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * remove note from archive
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 */
	@Override
	public void removeArchiveNote(String userId, String noteId) throws NoteNotFoundException, UnauthorizedException {

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setArchive(false);

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * View archived notes
	 * 
	 * @param userId
	 * @return list of archived notes
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 * 
	 */
	@Override
	public List<NoteDTO> getArchivedNote(String userId) throws NoteNotFoundException, GetLinkInfoException {

		List<Note> noteList = noteRepository.findAllByUserIdAndTrash(userId, false);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No Note Found");
		}

		List<NoteDTO> noteDtos = noteList.stream().map(filterNote -> modelMapper.map(filterNote, NoteDTO.class))
				.collect(Collectors.toList());

		for (int i = 0; i < noteList.size(); i++) {
			List<String> urlList = noteList.get(i).getListOfUrl();

			List<URLInfo> urlInfoList = new ArrayList<>();

			for (int j = 0; j < urlList.size(); j++) {
				urlInfoList.add(linkInfoProvider.getLinkInformation(urlList.get(j)));
			}
			noteDtos.get(i).setListOfUrl(urlInfoList);
		}

		List<NoteDTO> noteDtoList = noteDtos.stream().filter(NoteDTO::getArchive).collect(Collectors.toList());

		return noteDtoList;
	}

	/**
	 * add label to the note
	 * 
	 * @param userId
	 * @param noteId
	 * @return list of note
	 * @throws NoteNotFoundException
	 * @throws LabelNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelException
	 * @throws InvalidLabelNameException
	 */
	@Override
	public void addLabel(String userId, String noteId, String labelName) throws NoteNotFoundException,
			UnauthorizedException, LabelException, LabelNotFoundException, InvalidLabelNameException {
		if (labelName == null || labelName.trim().length() == 0) {
			throw new InvalidLabelNameException("Invalid LabelName");
		}

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();

		Optional<Label> optionalLabel = labelRepository.findByLabelNameAndUserId(labelName, userId);

		if (optionalLabel.isPresent()) {
			Label label = optionalLabel.get();
			LabelDTO labelDto = modelMapper.map(label, LabelDTO.class);
			List<LabelDTO> labelDtoList = Stream.concat(note.getListOfLabel().stream(), Stream.of(labelDto))
					.collect(Collectors.toList());
			note.setListOfLabel(labelDtoList);
		} else {
			Label label = new Label();
			label.setLabelName(labelName);
			label.setUserId(userId);
			labelRepository.save(label);

			labelElasticsearchRepository.save(label);

			LabelDTO labelDto = modelMapper.map(label, LabelDTO.class);
			List<LabelDTO> labelDtoList = Stream.concat(note.getListOfLabel().stream(), Stream.of(labelDto))
					.collect(Collectors.toList());
			note.setListOfLabel(labelDtoList);
		}

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

	/**
	 * to delete a label from the note
	 * 
	 * @param userId
	 * @param noteId
	 * @param labelId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 */
	@Override
	public void deleteNoteLabel(String userId, String noteId, String labelId)
			throws NoteNotFoundException, UnauthorizedException, LabelNotFoundException {

		Optional<Note> optionalNote = noteRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Optional<Label> optionalLabel = labelRepository.findById(labelId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}

		Note note = optionalNote.get();

		if (!note.getTrash()) {
			List<LabelDTO> labelList = note.getListOfLabel();
			for (int i = 0; i < labelList.size(); i++) {
				if (labelList.get(i).getLabelId().equals(labelId)) {
					labelList.remove(i);
				}
			}
			note.setListOfLabel(labelList);
		}

		noteRepository.save(note);

		noteElasticsearchRepository.save(note);
	}

}
