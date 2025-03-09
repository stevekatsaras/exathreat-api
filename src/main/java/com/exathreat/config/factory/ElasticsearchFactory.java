package com.exathreat.config.factory;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ElasticsearchFactory {
	private ActionListener<BulkResponse> bulkListener;

	@Autowired
	private RestHighLevelClient elasticsearchClient;

	public void bulkDocuments(BulkRequest bulkRequest) throws Exception {
		elasticsearchClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, bulkListener());
	}

	private ActionListener<BulkResponse> bulkListener() {
		if (bulkListener == null) {
			bulkListener = new ActionListener<BulkResponse>() {
				@Override
				public void onResponse(BulkResponse response) {

				}

				@Override
				public void onFailure(Exception exception) {
					log.error("[ElasticsearchFactory.bulkListener.onFailure] - exception: " + exception.getMessage() + ".", exception);
				}
			};
		}
		return bulkListener;
	}

}