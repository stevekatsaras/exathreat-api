package com.exathreat.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.exathreat.service.ApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

	@Autowired
	private ApiService apiService;

	/*
		An incoming JSON request is: 
		{
			"apiKey": "baac41f2-6383-4ccd-a699-c8ef75dcac79"
		}

		A failed JSON response is:
		{
			"authenticated": false,
			"errorMsg": "A sample error message",
			"errorCause": [
				"A sample error outlining the cause of the error"
			],
			"errorCode": "error_code"
		}

		A successful JSON response is:
		{
			"authenticated": true,
			"orgCode": "abc123def456",
			"orgName": "My dummy Org"
		}
	*/
	@PostMapping("/auth")
	public CompletableFuture<ResponseEntity<Map<String, Object>>> auth(RequestEntity<String> request) throws Exception {
		return CompletableFuture.supplyAsync(() -> {
			return apiService.auth(request);
		});
	}

	/*
		An incoming JSON request is: 
		{
			"apiKey": "baac41f2-6383-4ccd-a699-c8ef75dcac79",
			"orgCode": "abc123def456",
			"orgName": "My dummy Org",
			"events": [
				{
					"event": "my network or log message #1"
				},
				{
					"event": "my network or log message #2"
				},
				{
					"event": "my network or log message #n"
				}
			]
		}

		A JSON response is:
		{
			"status": "Okay"
		}
	*/
	@PostMapping("/ingest")
	public CompletableFuture<ResponseEntity<Map<String, Object>>> ingest(RequestEntity<String> request) throws Exception {
		return CompletableFuture.supplyAsync(() -> {
			return apiService.ingest(request);
		});
	}
}