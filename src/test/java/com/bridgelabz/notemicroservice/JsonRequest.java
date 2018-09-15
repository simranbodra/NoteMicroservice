package com.bridgelabz.notemicroservice;

import java.util.Map;

import org.springframework.http.HttpHeaders;

public class JsonRequest {

	private String url;

	private HttpHeaders headers;

	private Map<String, Object> body;

	private String method;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}

	public Map<String, Object> getBody() {
		return body;
	}

	public void setBody(Map<String, Object> body) {
		this.body = body;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

}
