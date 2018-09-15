package com.bridgelabz.microserice.fundoonotes.note.services;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
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
import com.bridgelabz.microserice.fundoonotes.note.models.Label;
import com.bridgelabz.microserice.fundoonotes.note.models.LabelDTO;
import com.bridgelabz.microserice.fundoonotes.note.models.Note;
import com.bridgelabz.microserice.fundoonotes.note.models.NoteDTO;
import com.bridgelabz.microserice.fundoonotes.note.models.URLInfo;
import com.bridgelabz.microserice.fundoonotes.note.models.UpdateNote;
import com.bridgelabz.microserice.fundoonotes.note.repositories.LabelAWSElasticRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.LabelElasticsearchRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.LabelRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.NoteAWSElasticRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.NoteElasticsearchRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.NoteRepository;
import com.bridgelabz.microserice.fundoonotes.note.utility.NoteUtility;

@Service
public class NoteServiceImpl implements NoteService {

	private static final String SUFFIX = "/";

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private Environment environment;

	@Autowired
	private ContentScrapper contentScrapper;

	@Autowired
	private ImageStorageService imageStorageService;

	@Autowired
	private NoteElasticsearchRepository noteElasticsearchRepository;

	@Autowired
	private LabelElasticsearchRepository labelElasticsearchRepository;

	@Autowired
	private NoteAWSElasticRepository noteAWSElasticRepository;

	@Autowired
	private LabelAWSElasticRepository labelAWSElasticRepository;

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
	@Override
	public NoteDTO createNote(CreateNote newNote, String userId)
			throws NoteException, ReminderException, GetLinkInfoException, ParseException, ElasticsearchFailException {
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

		List<Label> userLabels = labelAWSElasticRepository.findAllByUserId(userId);

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
					label.setCreatedAt(NoteUtility.getCurrentDate());
					label.setUserId(userId);

					labelRepository.save(label);

					labelAWSElasticRepository.save(label);

					LabelDTO viewLabelToSave = new LabelDTO();
					viewLabelToSave.setLabelId(label.getLabelId());
					viewLabelToSave.setLabelName(label.getLabelName());
					viewLabelToSave.setCreatedAt(label.getCreatedAt());
					toBeAddedLabels.add(viewLabelToSave);

				} else {
					List<Label> optionalLabelToSave = labelAWSElasticRepository
							.findAllByLabelName(labelNameList.get(i));

					for (int j = 0; j < optionalLabelToSave.size(); j++) {
						if (optionalLabelToSave.get(j).getUserId().equalsIgnoreCase(userId)) {
							LabelDTO viewLabel = new LabelDTO();
							viewLabel.setLabelName(optionalLabelToSave.get(j).getLabelName());
							viewLabel.setLabelId(optionalLabelToSave.get(j).getLabelId());
							viewLabel.setCreatedAt(optionalLabelToSave.get(j).getCreatedAt());
							toBeAddedLabels.add(viewLabel);
						}
					}

				}

			}
		}

		note.setListOfLabel(toBeAddedLabels);

		List<URLInfo> urlInfoList = contentScrapper.getAllLink(newNote.getDescription());

		note.setListOfUrl(urlInfoList);

		noteRepository.save(note);

		noteAWSElasticRepository.save(note);

		NoteDTO noteDto = modelMapper.map(note, NoteDTO.class);

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
	 * @throws ElasticsearchFailException
	 */
	public NoteDTO getNote(String userId, String noteId)
			throws NoteNotFoundException, UnauthorizedException, GetLinkInfoException, ElasticsearchFailException {

		Optional<Note> optionalNote = noteAWSElasticRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();

		NoteDTO noteDto = modelMapper.map(note, NoteDTO.class);

		return noteDto;

	}

	/**
	 * view a list of note owned by the user
	 * 
	 * @param userId
	 * @return list of ViewNoteDTO
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 * @throws ElasticsearchFailException
	 */
	public List<NoteDTO> getAllNotes(String userId)
			throws NoteNotFoundException, GetLinkInfoException, ElasticsearchFailException {

		List<Note> noteList = noteAWSElasticRepository.findAllByUserIdAndTrash(userId, false);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No Note Found");
		}

		List<NoteDTO> noteDtos = noteList.stream().map(filterNote -> modelMapper.map(filterNote, NoteDTO.class))
				.collect(Collectors.toList());

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
	 * @throws GetLinkInfoException
	 * @throws ElasticsearchFailException
	 */
	@Override
	public void updateNote(UpdateNote updateNote, String userId, String noteId)
			throws NoteException, NoteNotFoundException, UnauthorizedException, ReminderException, ParseException,
			GetLinkInfoException, ElasticsearchFailException {

		Optional<Note> optionalNote = noteAWSElasticRepository.findByNoteIdAndUserId(noteId, userId);

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

		List<URLInfo> urlInfoList = contentScrapper.getAllLink(note.getDescription());

		note.setListOfUrl(urlInfoList);

		note.setLastUpdated(NoteUtility.getCurrentDate());

		noteRepository.save(note);

		noteAWSElasticRepository.save(note);
	}

	/**
	 * move a note to trash
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException
	 */
	@Override
	public void deleteNote(String userId, String noteId)
			throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException {

		Optional<Note> optionalNote = noteAWSElasticRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setTrash(true);

		noteRepository.save(note);

		noteAWSElasticRepository.save(note);
	}

	/**
	 * delete a note from trash
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException
	 */
	@Override
	public void permanentNoteDelete(String userId, String noteId, boolean delete)
			throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException {

		Optional<Note> optionalNote = noteAWSElasticRepository.findById(noteId);

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

			noteAWSElasticRepository.deleteById(noteId);
		} else {
			Note note = optionalNote.get();
			note.setTrash(false);
			noteRepository.save(note);

			noteAWSElasticRepository.save(note);
		}

	}

	/**
	 * delete all notes in trash
	 * 
	 * @param userId
	 * @throws NoteNotFoundException
	 * @throws ElasticsearchFailException
	 */
	@Override
	public void emptyTrash(String userId) throws NoteNotFoundException, ElasticsearchFailException {

		List<Note> noteList = noteAWSElasticRepository.findAllByUserId(userId);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No Note Found");
		}

		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);
			System.out.println(note);
			if (noteList.get(i).getTrash()) {
				noteRepository.deleteById(note.getNoteId());

				noteAWSElasticRepository.deleteById(note.getNoteId());
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
	 * @throws ElasticsearchFailException
	 * 
	 */
	@Override
	public List<NoteDTO> getTrash(String userId)
			throws NoteNotFoundException, GetLinkInfoException, ElasticsearchFailException {
		List<Note> noteList = noteAWSElasticRepository.findAllByUserIdAndTrash(userId, true);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No Note Found");
		}

		List<NoteDTO> noteDtos = noteList.stream().map(filterNote -> modelMapper.map(filterNote, NoteDTO.class))
				.collect(Collectors.toList());

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
	 * @throws ElasticsearchFailException
	 */
	@Override
	public void addColour(String userId, String noteId, String colour)
			throws NoteNotFoundException, UnauthorizedException, NoteException, ElasticsearchFailException {
		Optional<Note> optionalNote = noteAWSElasticRepository.findById(noteId);

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

		noteAWSElasticRepository.save(note);
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
	 * @throws ElasticsearchFailException
	 */
	@Override
	public void addNoteReminder(String userId, String noteId, String reminderDate) throws NoteNotFoundException,
			UnauthorizedException, ReminderException, ParseException, ElasticsearchFailException {

		NoteUtility.validateDate(reminderDate);

		Optional<Note> optionalNote = noteAWSElasticRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setReminder(reminderDate);

		noteRepository.save(note);

		noteAWSElasticRepository.save(note);
	}

	/**
	 * to remove reminder from the note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException
	 */
	@Override
	public void removeReminder(String userId, String noteId)
			throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException {

		Optional<Note> optionalNote = noteAWSElasticRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setReminder(null);

		noteRepository.save(note);

		noteAWSElasticRepository.save(note);
	}

	/**
	 * Add pin to the note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException
	 * 
	 */
	@Override
	public void addPin(String userId, String noteId)
			throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException {

		Optional<Note> optionalNote = noteAWSElasticRepository.findById(noteId);

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

		noteAWSElasticRepository.save(note);
	}

	/**
	 * Removes pin on the note
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException
	 * 
	 */
	@Override
	public void removePin(String userId, String noteId)
			throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException {

		Optional<Note> optionalNote = noteAWSElasticRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setPin(false);

		noteRepository.save(note);

		noteAWSElasticRepository.save(note);
	}

	/**
	 * Adding note to archive
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException
	 * 
	 */
	@Override
	public void archiveNote(String userId, String noteId)
			throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException {

		Optional<Note> optionalNote = noteAWSElasticRepository.findById(noteId);

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

		noteAWSElasticRepository.save(note);
	}

	/**
	 * remove note from archive
	 * 
	 * @param userId
	 * @param noteId
	 * @throws NoteNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException
	 */
	@Override
	public void removeArchiveNote(String userId, String noteId)
			throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException {

		Optional<Note> optionalNote = noteAWSElasticRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();
		note.setArchive(false);

		noteRepository.save(note);

		noteAWSElasticRepository.save(note);
	}

	/**
	 * View archived notes
	 * 
	 * @param userId
	 * @return list of archived notes
	 * @throws NoteNotFoundException
	 * @throws GetLinkInfoException
	 * @throws ElasticsearchFailException
	 * 
	 */
	@Override
	public List<NoteDTO> getArchivedNote(String userId)
			throws NoteNotFoundException, GetLinkInfoException, ElasticsearchFailException {

		List<Note> noteList = noteAWSElasticRepository.findAllByUserIdAndTrash(userId, false);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No Note Found");
		}

		List<NoteDTO> noteDtos = noteList.stream().map(filterNote -> modelMapper.map(filterNote, NoteDTO.class))
				.collect(Collectors.toList());

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
	 * @throws ElasticsearchFailException
	 */
	@Override
	public void addLabel(String userId, String noteId, String labelName)
			throws NoteNotFoundException, UnauthorizedException, LabelException, LabelNotFoundException,
			InvalidLabelNameException, ElasticsearchFailException {
		if (labelName == null || labelName.trim().length() == 0) {
			throw new InvalidLabelNameException("Invalid LabelName");
		}

		Optional<Note> optionalNote = noteAWSElasticRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Note note = optionalNote.get();

		Optional<Label> optionalLabel = labelAWSElasticRepository.findByLabelNameAndUserId(labelName, userId);

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

			labelAWSElasticRepository.save(label);

			LabelDTO labelDto = modelMapper.map(label, LabelDTO.class);
			List<LabelDTO> labelDtoList = Stream.concat(note.getListOfLabel().stream(), Stream.of(labelDto))
					.collect(Collectors.toList());
			note.setListOfLabel(labelDtoList);
		}

		noteRepository.save(note);

		noteAWSElasticRepository.save(note);
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
	 * @throws ElasticsearchFailException
	 */
	@Override
	public void deleteNoteLabel(String userId, String noteId, String labelId)
			throws NoteNotFoundException, UnauthorizedException, LabelNotFoundException, ElasticsearchFailException {

		Optional<Note> optionalNote = noteAWSElasticRepository.findById(noteId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		Optional<Label> optionalLabel = labelAWSElasticRepository.findById(labelId);

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

		noteAWSElasticRepository.save(note);
	}

	@Override
	public List<NoteDTO> sortByTitleOrDate(String userId, String sortType, String format)
			throws NoteNotFoundException, ElasticsearchFailException {
		List<Note> noteList = noteAWSElasticRepository.findAllByUserIdAndTrash(userId, false);

		if (noteList.isEmpty()) {
			throw new NoteNotFoundException("No note found");
		}

		if (sortType == null || sortType.equalsIgnoreCase("Date")) {
			if (format == null || format.equalsIgnoreCase("ascending")) {
				return noteList.stream().sorted(Comparator.comparing(Note::getCreatedAt))
						.map(sortedList -> modelMapper.map(sortedList, NoteDTO.class)).collect(Collectors.toList());
			}

			return noteList.stream().sorted(Comparator.comparing(Note::getCreatedAt).reversed())
					.map(sortedList -> modelMapper.map(sortedList, NoteDTO.class)).collect(Collectors.toList());
		}

		if (format == null || format.equalsIgnoreCase("ascending")) {
			return noteList.stream().sorted(Comparator.comparing(Note::getTitle))
					.map(sortedList -> modelMapper.map(sortedList, NoteDTO.class)).collect(Collectors.toList());
		}

		return noteList.stream().sorted(Comparator.comparing(Note::getTitle).reversed())
				.map(sortedList -> modelMapper.map(sortedList, NoteDTO.class)).collect(Collectors.toList());
	}

	@Override
	public NoteDTO addImage(String userId, String noteId, MultipartFile image)
			throws NoteNotFoundException, UnauthorizedException, FileConversionException, ElasticsearchFailException {
		Optional<Note> optionalNote = noteAWSElasticRepository.findByNoteIdAndUserId(noteId, userId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		String folder = userId + SUFFIX + noteId;

		imageStorageService.uploadFile(folder, image);

		String picture = imageStorageService.getFile(folder, image.getOriginalFilename());

		Note note = optionalNote.get();
		List<String> imageUrls = note.getListOfImage();

		imageUrls.add(picture);

		note.setListOfImage(imageUrls);

		noteRepository.save(note);

		noteAWSElasticRepository.save(note);

		NoteDTO noteDto = modelMapper.map(note, NoteDTO.class);

		return noteDto;
	}

	@Override
	public NoteDTO removeImage(String userId, String noteId, String imageUrl)
			throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException {
		Optional<Note> optionalNote = noteAWSElasticRepository.findByNoteIdAndUserId(noteId, userId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		File file = new File(imageUrl);
		String fileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(SUFFIX) + 1);

		String folder = userId + SUFFIX + noteId;

		imageStorageService.deleteFile(folder, fileName);

		Note note = optionalNote.get();
		List<String> imageUrls = note.getListOfImage();
		imageUrls.remove(imageUrl);

		note.setListOfImage(imageUrls);

		noteRepository.save(note);

		noteAWSElasticRepository.save(note);

		NoteDTO noteDto = modelMapper.map(note, NoteDTO.class);

		return noteDto;
	}

	@Override
	public String getImageUrl(String userId, String noteId, String imageName)
			throws NoteNotFoundException, UnauthorizedException, ElasticsearchFailException {
		Optional<Note> optionalNote = noteAWSElasticRepository.findByNoteIdAndUserId(noteId, userId);

		if (!optionalNote.isPresent()) {
			throw new NoteNotFoundException(environment.getProperty("NoteNotFound"));
		}
		if (!optionalNote.get().getUserId().equals(userId)) {
			throw new UnauthorizedException(environment.getProperty("UnauthorizedUser"));
		}

		String folder = userId + SUFFIX + noteId;

		String picture = imageStorageService.getFile(folder, imageName);

		return picture;
	}
}
