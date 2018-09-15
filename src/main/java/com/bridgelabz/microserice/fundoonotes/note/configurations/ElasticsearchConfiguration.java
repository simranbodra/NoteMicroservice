package com.bridgelabz.microserice.fundoonotes.note.configurations;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfiguration extends AbstractFactoryBean<RestHighLevelClient> {

	private RestHighLevelClient restHighLevelClient;

	@Override
	public Class<RestHighLevelClient> getObjectType() {
		return RestHighLevelClient.class;
	}

	@Override
	public RestHighLevelClient createInstance() {
		return buildClient();
	}

	private RestHighLevelClient buildClient() {
		try {
			restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return restHighLevelClient;
	}

}