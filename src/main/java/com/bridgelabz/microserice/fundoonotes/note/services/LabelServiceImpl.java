package com.bridgelabz.microserice.fundoonotes.note.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bridgelabz.microserice.fundoonotes.note.exceptions.ElasticsearchFailException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.GetLinkInfoException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.InvalidLabelNameException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.LabelException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.LabelNotFoundException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.NoteNotFoundException;
import com.bridgelabz.microserice.fundoonotes.note.exceptions.UnauthorizedException;
import com.bridgelabz.microserice.fundoonotes.note.models.Label;
import com.bridgelabz.microserice.fundoonotes.note.models.LabelDTO;
import com.bridgelabz.microserice.fundoonotes.note.models.Note;
import com.bridgelabz.microserice.fundoonotes.note.models.NoteDTO;
import com.bridgelabz.microserice.fundoonotes.note.repositories.LabelAWSElasticRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.LabelRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.NoteAWSElasticRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.NoteRepository;
import com.bridgelabz.microserice.fundoonotes.note.utility.NoteUtility;

@Service
public class LabelServiceImpl implements LabelService {

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private NoteAWSElasticRepository noteAWSElasticRepository;

	@Autowired
	private LabelAWSElasticRepository labelAWSElasticRepository;

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
	@Override
	public LabelDTO createLabel(String userId, String labelName)
			throws LabelException, InvalidLabelNameException, ElasticsearchFailException {
		if (labelName == null || labelName.trim().length() == 0) {
			throw new InvalidLabelNameException("Invalid LabelName");
		}

		if (labelName.trim().length() > 10) {
			throw new InvalidLabelNameException("Maximum label name length can be 10 characters");
		}

		List<Label> labelList = labelAWSElasticRepository.findAllByUserId(userId);
		for (int i = 0; i < labelList.size(); i++) {
			if (labelList.get(i).getLabelName().equals(labelName)) {
				throw new LabelException("Label with this name already exists");
			}
		}

		Label label = new Label();
		label.setLabelName(labelName);
		label.setUserId(userId);
		label.setCreatedAt(NoteUtility.getCurrentDate());

		labelRepository.save(label);

		labelAWSElasticRepository.save(label);

		LabelDTO labelDto = new LabelDTO();
		labelDto.setLabelId(label.getLabelId());
		labelDto.setLabelName(labelName);
		labelDto.setCreatedAt(label.getCreatedAt());

		return labelDto;
	}

	/**
	 * To get all the labels created
	 * 
	 * @param userId
	 * @param labelId
	 * @return list of labelDTO
	 * @throws LabelNotFoundException
	 * @throws ElasticsearchFailException
	 */
	@Override
	public List<LabelDTO> getAllLabel(String userId) throws LabelNotFoundException, ElasticsearchFailException {
		List<Label> labelList = labelAWSElasticRepository.findAllByUserId(userId);

		if (labelList.isEmpty()) {
			throw new LabelNotFoundException("No Labels found");
		}

		List<LabelDTO> viewLabelList = labelList.stream()
				.map(labelStream -> modelMapper.map(labelStream, LabelDTO.class)).collect(Collectors.toList());

		return viewLabelList;
	}

	/**
	 * To update the label name
	 * 
	 * @param userId
	 * @param labelId
	 * @param labelName
	 * @throws LabelNotFoundException
	 * @throws UnauthorizedException
	 * @throws ElasticsearchFailException
	 */
	@Override
	public void updateLabel(String userId, String labelId, String labelName)
			throws UnauthorizedException, LabelNotFoundException, ElasticsearchFailException {

		Optional<Label> optionalLabel = labelAWSElasticRepository.findByLabelIdAndUserId(labelId, userId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}

		Label label = optionalLabel.get();
		label.setLabelName(labelName);

		labelRepository.save(label);

		labelAWSElasticRepository.save(label);

		List<Note> noteList = noteRepository.findAllByUserId(userId);

		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);
			for (int j = 0; j < note.getListOfLabel().size(); j++) {
				if (note.getListOfLabel().get(j).getLabelId().equals(labelId)) {
					note.getListOfLabel().get(j).setLabelName(labelName);
					noteRepository.save(note);

					noteAWSElasticRepository.save(note);
				}
			}
		}

	}

	/**
	 * To delete label
	 * 
	 * @param userId
	 * @param labelId
	 * @throws UnauthorizedException
	 * @throws LabelNotFoundException
	 * @throws ElasticsearchFailException
	 */
	@Override
	public void deleteLabel(String userId, String labelId) throws LabelNotFoundException, ElasticsearchFailException {
		Optional<Label> optionalLabel = labelRepository.findByLabelIdAndUserId(labelId, userId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}

		labelAWSElasticRepository.deleteById(labelId);

		labelRepository.deleteById(labelId);

		List<Note> noteListByUserId = noteRepository.findAllByUserId(userId);

		for (int i = 0; i < noteListByUserId.size(); i++) {
			Note note = noteListByUserId.get(i);

			List<LabelDTO> labelList = note.getListOfLabel();

			labelList.removeIf(labelDto -> labelDto.getLabelId().equals(labelId));

			noteRepository.save(note);

			noteAWSElasticRepository.save(note);
		}

	}

	/**
	 * To get all notes by label Id
	 * 
	 * @param userId
	 * @param labelId
	 * @return list of note
	 * @throws LabelNotFoundException
	 * @throws GetLinkInfoException
	 * @throws NoteNotFoundException
	 * @throws ElasticsearchFailException
	 */
	@Override
	public List<NoteDTO> getLabel(String userId, String labelId)
			throws LabelNotFoundException, GetLinkInfoException, NoteNotFoundException, ElasticsearchFailException {
		Optional<Label> optionalLabel = labelAWSElasticRepository.findByLabelIdAndUserId(labelId, userId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}

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

	@Override
	public List<LabelDTO> sortByName(String userId, String sortType, String format)
			throws LabelNotFoundException, ElasticsearchFailException {
		List<Label> labelList = labelAWSElasticRepository.findAllByUserId(userId);

		if (labelList.isEmpty()) {
			throw new LabelNotFoundException("No label found");
		}

		if (sortType == null || sortType.equalsIgnoreCase("Date")) {
			if (format == null || format.equalsIgnoreCase("ascending")) {
				return labelList.stream().sorted(Comparator.comparing(Label::getCreatedAt))
						.map(sortedList -> modelMapper.map(sortedList, LabelDTO.class)).collect(Collectors.toList());
			}

			return labelList.stream().sorted(Comparator.comparing(Label::getCreatedAt).reversed())
					.map(sortedList -> modelMapper.map(sortedList, LabelDTO.class)).collect(Collectors.toList());
		}

		if (format == null || format.equalsIgnoreCase("ascending")) {
			return labelList.stream().sorted(Comparator.comparing(Label::getLabelName))
					.map(sortedList -> modelMapper.map(sortedList, LabelDTO.class)).collect(Collectors.toList());
		}

		return labelList.stream().sorted(Comparator.comparing(Label::getLabelName).reversed())
				.map(sortedList -> modelMapper.map(sortedList, LabelDTO.class)).collect(Collectors.toList());
	}
}
