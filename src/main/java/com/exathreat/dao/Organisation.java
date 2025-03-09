package com.exathreat.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Organisation {
	private long orgId;
	private String orgCode;
	private String orgName;
	private String orgStatus;
	private boolean keyEnabled;
}