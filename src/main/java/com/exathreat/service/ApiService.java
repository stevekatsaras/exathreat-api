package com.exathreat.service;

import java.util.List;
import java.util.Map;

import com.exathreat.config.factory.JsonFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiService {

	@Autowired
	private AuthService authService;

	@Autowired
	private IngestService ingestService;

	@Autowired
	private JsonFactory jsonFactory;

	public ResponseEntity<Map<String, Object>> auth(RequestEntity<String> request) {
		ResponseEntity<Map<String, Object>> responseEntity = null;
		try {
			Map<String, Object> responseBody = jsonFactory.validateAuthRequest(request.getBody());
			if (responseBody != null) {
				responseEntity = new ResponseEntity<Map<String,Object>>(responseBody, HttpStatus.BAD_REQUEST);
			}
			else {
				responseBody = authService.authenticate(jsonFactory.marshal(request.getBody()), "Auth");

				if (!(boolean) responseBody.get("authenticated")) {
					responseEntity = new ResponseEntity<Map<String,Object>>(responseBody, HttpStatus.UNAUTHORIZED);
				}
				else {
					responseEntity = new ResponseEntity<Map<String,Object>>(responseBody, HttpStatus.OK);
				}
			}
		}
		catch (Exception exception) {
			log.error("[ApiService.auth] - exception: " + exception.getMessage() + ".", exception);

			responseEntity = new ResponseEntity<Map<String, Object>>(
				Map.of("authenticated", false, "errorCode", "server_error", "errorMsg", "A server error has occurred.", "errorCause", List.of()), 
				HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}

	public ResponseEntity<Map<String, Object>> ingest(RequestEntity<String> request) {
		ResponseEntity<Map<String, Object>> responseEntity = null;
		try {
			Map<String, Object> responseBody = jsonFactory.validateIngestRequest(request.getBody());
			if (responseBody != null) {
				responseEntity = new ResponseEntity<Map<String,Object>>(responseBody, HttpStatus.BAD_REQUEST);
			}
			else {
				Map<String, Object> requestBody = jsonFactory.marshal(request.getBody());

				responseBody = authService.authenticate(requestBody, "Ingest");
				if (responseBody != null) {
					responseEntity = new ResponseEntity<Map<String,Object>>(responseBody, HttpStatus.UNAUTHORIZED);
				}
				else {
					ingestService.process(requestBody);
					responseEntity = new ResponseEntity<Map<String,Object>>(Map.of("status", "Okay"), HttpStatus.OK);
				}
			}
		}
		catch (Exception exception) {
			log.error("[ApiService.ingest] - exception: " + exception.getMessage() + ".", exception);

			responseEntity = new ResponseEntity<Map<String, Object>>(
				Map.of("authenticated", false, "errorCode", "server_error", "errorMsg", "A server error has occurred.", "errorCause", List.of()), 
				HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return responseEntity;
	}
	
}