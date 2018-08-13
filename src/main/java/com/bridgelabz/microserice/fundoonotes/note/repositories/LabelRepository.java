package com.bridgelabz.microserice.fundoonotes.note.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bridgelabz.microserice.fundoonotes.note.models.Label;

public interface LabelRepository extends MongoRepository<Label, String> {

	public Optional<Label> findByLabelName(String labelName);

	public List<Label> findAllByUserId(String userId);

	public List<Label> findAllByLabelName(String labelName);

	public Optional<Label> findByLabelIdAndUserId(String labelId, String userId);

	public Optional<Label> findByLabelNameAndUserId(String labelName, String userId);

}
