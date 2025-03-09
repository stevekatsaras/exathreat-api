package com.exathreat.config.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @NoArgsConstructor @AllArgsConstructor @ToString
public class JsonFactory {
	private final TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {};
	private ObjectMapper objectMapper;
	private JsonSchema jsonSchemaAuth;
	private JsonSchema jsonSchemaIngest;

	public Map<String, Object> marshal(String request) throws Exception {
		return objectMapper.readValue(request, mapTypeReference);
	}

	public Map<String, Object> validateAuthRequest(String request) throws Exception {
		return validate(jsonSchemaAuth, request);
	}

	public Map<String, Object> validateIngestRequest(String request) throws Exception {
		return validate(jsonSchemaIngest, request);
	}

	private Map<String, Object> validate(JsonSchema jsonSchema, String request) throws Exception {
		Map<String, Object> responseBody = null;

		Set<ValidationMessage> validationErrors = jsonSchema.validate(objectMapper.readTree(request));
		if (validationErrors.size() > 0) {
			List<String> errorCause = new ArrayList<String>();
			for (ValidationMessage validationError : validationErrors) {
				errorCause.add(validationError.getMessage().substring(2));
			}
			responseBody = Map.of("authenticated", false, "errorCode", "json_error", "errorMsg", "Your JSON request is malformed.", "errorCause", errorCause);
		}
		return responseBody;
	}
}