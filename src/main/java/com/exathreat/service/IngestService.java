package com.exathreat.service;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.exathreat.config.factory.ElasticsearchFactory;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IngestService {

	@Autowired
	private ElasticsearchFactory elasticsearchFactory;

	@SuppressWarnings("unchecked")
	public void process(Map<String, Object> requestBody) throws Exception {
		String esAlias = "org-" + (String) requestBody.get("orgCode") + "-v1-data";

		BulkRequest bulkRequest = new BulkRequest();

		List<Map<String, Object>> requestBodyEvents = (List<Map<String, Object>>) requestBody.get("events");
		requestBodyEvents.stream().forEach(item -> {
			item.put("@timestamp", DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.now(ZoneOffset.UTC)));
			item.put("apiKey", (String) requestBody.get("apiKey"));
			item.put("orgCode", (String) requestBody.get("orgCode"));
			item.put("orgName", (String) requestBody.get("orgName"));
			item.put("bytes", getMapSizeInBytes(item));
			bulkRequest.add(new IndexRequest(esAlias).source(item));
		});

		elasticsearchFactory.bulkDocuments(bulkRequest);
	}
	
	private long getMapSizeInBytes(Map<String, Object> requestBodyEvent) {
		long size = 0L;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(requestBodyEvent);
			oos.close();
			size = Long.valueOf(baos.size());
			baos.close();
		}
		catch (Exception exception) {
			log.error("[IngestService.getMapSizeInBytes] - exception: " + exception.getMessage() + ".", exception);
		}
		return size;
	}
}