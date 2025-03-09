package com.exathreat.service;

import java.util.Map;

import com.exathreat.dao.Organisation;
import com.exathreat.dao.OrganisationDao;

import org.elasticsearch.common.collect.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
	
	@Autowired
	private OrganisationDao organisationDao;
	
	public Map<String, Object> authenticate(Map<String, Object> requestBody, String action) throws Exception {
		Map<String, Object> responseBody = null;

		Organisation organisation = organisationDao.getOrganisationByKey((String) requestBody.get("apiKey"));
		if (organisation == null) {
			responseBody = Map.of("authenticated", false, "errorCode", "invalid_key", "errorMsg", "Your api key is invalid.", "errorCause", List.of("Key cannot be found."));
		}
		else if (!organisation.isKeyEnabled()) {
			responseBody = Map.of("authenticated", false, "errorCode", "invalid_key", "errorMsg", "Your api key is invalid.", "errorCause", List.of("Key is disabled."));
		}
		else if (!"Active".equals(organisation.getOrgStatus())) {
			responseBody = Map.of("authenticated", false, "errorCode", "invalid_org", "errorMsg", "Your organisation is invalid.", "errorCause", List.of("Key is not active."));
		}
		else if ("Auth".equals(action)) {
			responseBody = Map.of("authenticated", true, "orgCode", organisation.getOrgCode(), "orgName", organisation.getOrgName());
		}
		else if ("Ingest".equals(action)) {
			if (!organisation.getOrgCode().equals((String) requestBody.get("orgCode"))) {
				responseBody = Map.of("authenticated", false, "errorCode", "invalid_org", "errorMsg", "Your organisation is invalid.", "errorCause", List.of("orgCode does not match."));
			}
		}
		return responseBody;
	}
}