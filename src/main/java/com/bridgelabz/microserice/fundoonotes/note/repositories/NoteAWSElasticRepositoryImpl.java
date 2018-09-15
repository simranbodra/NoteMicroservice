package com.bridgelabz.microserice.fundoonotes.note.repositories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.bridgelabz.microserice.fundoonotes.note.exceptions.ElasticsearchFailException;
import com.bridgelabz.microserice.fundoonotes.note.models.Note;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class NoteAWSElasticRepositoryImpl implements NoteAWSElasticRepository{

	@Value("${noteIndex}")
	private String noteIndex;

	@Value("${noteType}")
	private String noteType;

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void save(Note note) throws ElasticsearchFailException {
		Map dataMap = objectMapper.convertValue(note, Map.class);
		IndexRequest indexRequest = new IndexRequest(noteIndex, noteType, note.getNoteId()).source(dataMap);

		try {
			IndexResponse response = restHighLevelClient.index(indexRequest);
		} catch (ElasticsearchException | IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}
	}

	@Override
	public Optional<Note> findById(String noteId) throws ElasticsearchFailException {
		GetRequest getRequest = new GetRequest(noteIndex, noteType, noteId);

		GetResponse getResponse = null;

		Optional<Note> optionalNote = null;

		try {
			getResponse = restHighLevelClient.get(getRequest);

			String noteData = getResponse.getSourceAsString();

			optionalNote = Optional.of(objectMapper.readValue(noteData, Note.class));
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		return optionalNote;
	}

	@Override
	public void deleteById(String noteId) throws ElasticsearchFailException {

		DeleteRequest deleteRequest = new DeleteRequest(noteIndex, noteType, noteId);

		try {
			restHighLevelClient.delete(deleteRequest);
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}
	}

	@Override
	public List<Note> findAllByUserId(String userId) throws ElasticsearchFailException {

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("userId", userId));

		SearchRequest searchRequest = new SearchRequest(noteIndex);
		searchRequest.types(noteType);
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = null;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();

		List<Note> noteList = new ArrayList<>();

		try {
			for (int i = 0; i < searchHits.length; i++) {
				Note note = objectMapper.readValue(hits.getAt(i).getSourceAsString(), Note.class);
				noteList.add(note);
			}
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		return noteList;
	}

	@Override
	public List<Note> findAllByUserIdAndTrash(String userId, boolean isTrashed) throws ElasticsearchFailException {

		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		boolQuery.must(QueryBuilders.matchPhrasePrefixQuery("userId", userId));
		boolQuery.must(QueryBuilders.matchPhrasePrefixQuery("trash", isTrashed));

		SearchRequest searchRequest = new SearchRequest(noteIndex);
		searchRequest.types(noteType);

		SearchSourceBuilder searchSourceBUilder = new SearchSourceBuilder();
		searchSourceBUilder.query(boolQuery);

		searchRequest.source(searchSourceBUilder);

		SearchResponse searchResponse = null;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();

		List<Note> noteList = new ArrayList<>();

		try {
			for (int i = 0; i < searchHits.length; i++) {
				Note note = objectMapper.readValue(hits.getAt(i).getSourceAsString(), Note.class);
				noteList.add(note);
			}
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		return noteList;
	}

	@Override
	public Optional<Note> findByNoteIdAndUserId(String noteId, String userId) throws ElasticsearchFailException {

		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		boolQuery.must(QueryBuilders.matchPhrasePrefixQuery("noteId", noteId));
		boolQuery.must(QueryBuilders.matchPhrasePrefixQuery("userId", userId));

		SearchRequest searchRequest = new SearchRequest(noteIndex);
		searchRequest.types(noteType);

		SearchSourceBuilder searchSourceBUilder = new SearchSourceBuilder();
		searchSourceBUilder.query(boolQuery);

		searchRequest.source(searchSourceBUilder);

		SearchResponse searchResponse = null;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		SearchHits hits = searchResponse.getHits();

		Optional<Note> optionalNote = null;

		String note = hits.getAt(0).getSourceAsString();

		try {
			optionalNote = Optional.of(objectMapper.readValue(note, Note.class));
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		return optionalNote;
	}
}
