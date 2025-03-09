package com.exathreat.config;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.exathreat.config.factory.CacheSettings;
import com.exathreat.config.factory.ElasticsearchSettings;
import com.exathreat.config.factory.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;

import org.apache.http.HttpHost;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ApplicationConfig {

	@Autowired
	private CacheSettings cacheSettings;

	@Autowired
	private ElasticsearchSettings elasticsearchSettings;

	@PostConstruct
	public void init() {
		log.info("cacheSettings: " + cacheSettings);
		log.info("elasticsearchSettings: " + elasticsearchSettings);
	}
	
	@Bean
	public CacheManager cacheManager() {
		Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
			.expireAfterWrite(cacheSettings.getTimeout(), TimeUnit.MILLISECONDS);
			
		CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager("orgCache");
    caffeineCacheManager.setCaffeine(caffeine);
    return caffeineCacheManager;
	}

	@Bean
	public RestHighLevelClient elasticsearchClient() {
		RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(
			elasticsearchSettings.getDomain(), 
			elasticsearchSettings.getPort(), 
			elasticsearchSettings.getScheme()));

		restClientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> 
			httpAsyncClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom()
				.setSoKeepAlive(true)
				.build()));

		restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> 
			requestConfigBuilder.setConnectTimeout(elasticsearchSettings.getConnectTimeout())
				.setSocketTimeout(elasticsearchSettings.getSocketTimeout()));
		
		return new RestHighLevelClient(restClientBuilder);
  }

	@Bean
	public JsonFactory jsonFactory() {
		JsonSchema jsonSchemaAuth = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(
			ApplicationConfig.class.getClassLoader().getResourceAsStream("json/schema-auth.json"));
		
		JsonSchema jsonSchemaIngest = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7).getSchema(
			ApplicationConfig.class.getClassLoader().getResourceAsStream("json/schema-ingest.json"));;

		return JsonFactory.builder()
			.jsonSchemaAuth(jsonSchemaAuth)
			.jsonSchemaIngest(jsonSchemaIngest)
			.objectMapper(new ObjectMapper())
			.build();
	}
}