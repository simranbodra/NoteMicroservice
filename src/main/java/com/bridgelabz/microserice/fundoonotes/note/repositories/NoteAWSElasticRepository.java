package com.bridgelabz.microserice.fundoonotes.note.repositories;

import java.util.List;
import java.util.Optional;

import com.bridgelabz.microserice.fundoonotes.note.exceptions.ElasticsearchFailException;
import com.bridgelabz.microserice.fundoonotes.note.models.Note;

public interface NoteAWSElasticRepository {
	
	public void save(Note note) throws ElasticsearchFailException;
	
	public Optional<Note> findById(String noteId) throws ElasticsearchFailException;
	
	public List<Note> findAllByUserId(String userId) throws ElasticsearchFailException;

	public List<Note> findAllByUserIdAndTrash(String userId, boolean isTrashed) throws ElasticsearchFailException;

	public Optional<Note> findByNoteIdAndUserId(String noteId, String userId) throws ElasticsearchFailException;
	
	public void deleteById(String noteId) throws ElasticsearchFailException;
}
