package com.bridgelabz.notemicroservice;

import java.util.Map;

import org.springframework.http.HttpStatus;

public class JsonResponse {

	private HttpStatus status;

	private Map<String, Object> headers;

	private Map<String, Object> body;

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

	public Map<String, Object> getBody() {
		return body;
	}

	public void setBody(Map<String, Object> body) {
		this.body = body;
	}

}
