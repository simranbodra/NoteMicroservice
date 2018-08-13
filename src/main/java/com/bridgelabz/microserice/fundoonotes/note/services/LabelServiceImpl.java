package com.bridgelabz.microserice.fundoonotes.note.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.bridgelabz.microserice.fundoonotes.note.models.URLInfo;
import com.bridgelabz.microserice.fundoonotes.note.repositories.LabelElasticsearchRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.LabelRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.NoteElasticsearchRepository;
import com.bridgelabz.microserice.fundoonotes.note.repositories.NoteRepository;
import com.bridgelabz.microserice.fundoonotes.note.utility.LinkInfoProvider;

@Service
public class LabelServiceImpl implements LabelService {

	@Autowired
	private LabelRepository labelRepository;

	@Autowired
	private LabelElasticsearchRepository labelElasticsearchRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Autowired
	private NoteElasticsearchRepository noteElasticsearchRepository;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private LinkInfoProvider linkInfoProvider;

	/**
	 * To create a label
	 * 
	 * @param userId
	 * @param labelName
	 * @return LabelDTO
	 * @throws LabelException
	 * @throws InvalidLabelNameException
	 */
	@Override
	public LabelDTO createLabel(String userId, String labelName) throws LabelException, InvalidLabelNameException {
		if (labelName == null || labelName.trim().length() == 0) {
			throw new InvalidLabelNameException("Invalid LabelName");
		}

		Optional<Label> optionalLabel = labelRepository.findByLabelNameAndUserId(labelName, userId);
		if (optionalLabel.isPresent()) {
			throw new LabelException("Label with this name already exists");
		}

		Label label = new Label();
		label.setLabelName(labelName);
		label.setUserId(userId);

		labelRepository.save(label);

		labelElasticsearchRepository.save(label);

		LabelDTO labelDto = new LabelDTO();
		labelDto.setLabelId(label.getLabelId());
		labelDto.setLabelName(labelName);

		return labelDto;
	}

	/**
	 * To get all the labels created
	 * 
	 * @param userId
	 * @param labelId
	 * @return list of labelDTO
	 * @throws LabelNotFoundException
	 */
	@Override
	public List<LabelDTO> getAllLabel(String userId) throws LabelNotFoundException {
		List<Label> labelList = labelRepository.findAllByUserId(userId);

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
	 */
	@Override
	public void updateLabel(String userId, String labelId, String labelName)
			throws UnauthorizedException, LabelNotFoundException {

		Optional<Label> optionalLabel = labelRepository.findByLabelIdAndUserId(labelId, userId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}

		Label label = optionalLabel.get();
		label.setLabelName(labelName);

		labelRepository.save(label);

		labelElasticsearchRepository.save(label);

		List<Note> noteList = noteRepository.findAllByUserId(userId);

		for (int i = 0; i < noteList.size(); i++) {
			Note note = noteList.get(i);
			for (int j = 0; j < note.getListOfLabel().size(); j++) {
				if (note.getListOfLabel().get(j).getLabelId().equals(labelId)) {
					note.getListOfLabel().get(j).setLabelName(labelName);
					noteRepository.save(note);

					noteElasticsearchRepository.save(note);
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
	 */
	@Override
	public void deleteLabel(String userId, String labelId) throws LabelNotFoundException {
		Optional<Label> optionalLabel = labelRepository.findByLabelIdAndUserId(labelId, userId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}

		labelElasticsearchRepository.deleteById(labelId);

		labelRepository.deleteById(labelId);

		List<Note> noteListByUserId = noteRepository.findAllByUserId(userId);

		for (int i = 0; i < noteListByUserId.size(); i++) {
			Note note = noteListByUserId.get(i);

			List<LabelDTO> labelList = note.getListOfLabel();

			labelList.removeIf(labelDto -> labelDto.getLabelId().equals(labelId));

			noteRepository.save(note);

			noteElasticsearchRepository.save(note);
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
	 */
	@Override
	public List<NoteDTO> getLabel(String userId, String labelId) throws LabelNotFoundException, GetLinkInfoException, NoteNotFoundException {
		Optional<Label> optionalLabel = labelRepository.findByLabelIdAndUserId(labelId, userId);

		if (!optionalLabel.isPresent()) {
			throw new LabelNotFoundException("No such label found");
		}

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
}
