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
import com.bridgelabz.microserice.fundoonotes.note.models.Label;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class LabelAWSElasticRepositoryImpl implements LabelAWSElasticRepository {

	@Value("${labelIndex}")
	private String labelIndex;

	@Value("${labelType}")
	private String labelType;

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public void save(Label label) throws ElasticsearchFailException {
		Map dataMap = objectMapper.convertValue(label, Map.class);
		System.out.println(labelIndex);
		System.out.println(labelType);
		IndexRequest indexRequest = new IndexRequest(labelIndex, labelType, label.getLabelId()).source(dataMap);

		try {
			IndexResponse response = restHighLevelClient.index(indexRequest);
		} catch (ElasticsearchException | IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}
	}

	@Override
	public Optional<Label> findById(String labelId) throws ElasticsearchFailException {
		GetRequest getRequest = new GetRequest(labelIndex, labelType, labelId);

		GetResponse getResponse = null;

		Optional<Label> optionalLabel = null;

		try {
			getResponse = restHighLevelClient.get(getRequest);

			String labelData = getResponse.getSourceAsString();

			optionalLabel = Optional.of(objectMapper.readValue(labelData, Label.class));
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		return optionalLabel;
	}

	@Override
	public Optional<Label> findByLabelName(String labelName) throws ElasticsearchFailException {

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("labelName", labelName));

		SearchRequest searchRequest = new SearchRequest(labelIndex);
		searchRequest.types(labelType);
		searchRequest.source(sourceBuilder);

		Optional<Label> optionalLabel = null;
		SearchResponse searchResponse = null;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SearchHits hits = searchResponse.getHits();

		String label = hits.getAt(0).getSourceAsString();

		try {
			optionalLabel = Optional.of(objectMapper.readValue(label, Label.class));
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		return optionalLabel;
	}

	@Override
	public List<Label> findAllByUserId(String userId) throws ElasticsearchFailException {

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("userId", userId));

		SearchRequest searchRequest = new SearchRequest(labelIndex);
		searchRequest.types(labelType);
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = null;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();

		List<Label> labelList = new ArrayList<>();

		try {
			for (int i = 0; i < searchHits.length; i++) {
				Label label = objectMapper.readValue(hits.getAt(i).getSourceAsString(), Label.class);
				labelList.add(label);
			}
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		return labelList;

	}

	@Override
	public List<Label> findAllByLabelName(String labelName) throws ElasticsearchFailException {

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery("labelName", labelName));

		SearchRequest searchRequest = new SearchRequest(labelIndex);
		searchRequest.types(labelType);
		searchRequest.source(sourceBuilder);

		SearchResponse searchResponse = null;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}
		SearchHits hits = searchResponse.getHits();
		SearchHit[] searchHits = hits.getHits();

		List<Label> labelList = new ArrayList<>();

		try {
			for (int i = 0; i < searchHits.length; i++) {
				Label label = objectMapper.readValue(hits.getAt(i).getSourceAsString(), Label.class);
				labelList.add(label);
			}
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		return labelList;
	}

	@Override
	public Optional<Label> findByLabelIdAndUserId(String labelId, String userId) throws ElasticsearchFailException {

		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		boolQuery.must(QueryBuilders.matchPhrasePrefixQuery("labelId", labelId));
		boolQuery.must(QueryBuilders.matchPhrasePrefixQuery("userId", userId));

		SearchRequest searchRequest = new SearchRequest(labelIndex);
		searchRequest.types(labelType);

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

		Optional<Label> optionalLabel = null;

		String label = hits.getAt(0).getSourceAsString();

		try {
			optionalLabel = Optional.of(objectMapper.readValue(label, Label.class));
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

		return optionalLabel;
	}

	@Override
	public Optional<Label> findByLabelNameAndUserId(String labelName, String userId) throws ElasticsearchFailException {

		BoolQueryBuilder boolQuery = new BoolQueryBuilder();
		boolQuery.must(QueryBuilders.matchPhrasePrefixQuery("labelName", labelName));
		boolQuery.must(QueryBuilders.matchPhrasePrefixQuery("userId", userId));

		SearchRequest searchRequest = new SearchRequest(labelIndex);
		searchRequest.types(labelType);

		SearchSourceBuilder searchSourceBUilder = new SearchSourceBuilder();
		searchSourceBUilder.query(boolQuery);

		searchRequest.source(searchSourceBUilder);

		SearchResponse searchResponse = null;

		Optional<Label> optionalLabel = null;

		try {
			searchResponse = restHighLevelClient.search(searchRequest);

			throw new ElasticsearchFailException("Fail to get response");

			//SearchHits hits = searchResponse.getHits();

			/*SearchHits searchHits = searchResponse.getHits();
			for (SearchHit searchHit : searchHits) {
				optionalLabel = Optional.of(objectMapper.convertValue(searchHits.getAt(0), Label.class));

			}
*/
		} catch (IOException | ArrayIndexOutOfBoundsException exception) {
			// throw new ElasticsearchFailException("Fail to get response");
		}

		return optionalLabel;
	}

	@Override
	public void deleteById(String labelId) throws ElasticsearchFailException {
		DeleteRequest deleteRequest = new DeleteRequest(labelIndex, labelType, labelId);

		try {
			restHighLevelClient.delete(deleteRequest);
		} catch (IOException exception) {
			throw new ElasticsearchFailException("Fail to get response");
		}

	}
}
