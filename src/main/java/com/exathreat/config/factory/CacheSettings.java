package com.exathreat.config.factory;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Component
@ConfigurationProperties(prefix = "cache")
@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class CacheSettings {
	private long timeout;
}