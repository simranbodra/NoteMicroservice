package com.bridgelabz.microserice.fundoonotes.note.repositories;

import java.util.List;
import java.util.Optional;

import com.bridgelabz.microserice.fundoonotes.note.exceptions.ElasticsearchFailException;
import com.bridgelabz.microserice.fundoonotes.note.models.Label;

public interface LabelAWSElasticRepository {
	
	public void save(Label label) throws ElasticsearchFailException;
	
	public Optional<Label> findById(String labelId) throws ElasticsearchFailException;

	public Optional<Label> findByLabelName(String labelName) throws ElasticsearchFailException;

	public List<Label> findAllByUserId(String userId) throws ElasticsearchFailException;

	public List<Label> findAllByLabelName(String labelName) throws ElasticsearchFailException;

	public Optional<Label> findByLabelIdAndUserId(String labelId, String userId) throws ElasticsearchFailException;

	public Optional<Label> findByLabelNameAndUserId(String labelName, String userId) throws ElasticsearchFailException;
	
	public void deleteById(String labelId) throws ElasticsearchFailException;
}
