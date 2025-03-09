package com.exathreat.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrganisationDao {
	private String sql;
	private BeanPropertyRowMapper<Organisation> rowMapper;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Cacheable(value = "orgCache", sync = true)
	public Organisation getOrganisationByKey(String apiKey) {
		Organisation organisation = null;
		try {
			organisation = jdbcTemplate.queryForObject(sql(), rowMapper(), apiKey, "api");
		}
		catch (DataAccessException exception) {
			if (!EmptyResultDataAccessException.class.isInstance(exception)) {
				log.error("[OrganisationDao.getOrganisationByKey] - exception: " + exception.getMessage() + ".", exception);
			}
		}
		return organisation;
	}

	private String sql() {
		if (sql == null) {
			StringBuilder builder = new StringBuilder();
			builder.append("SELECT o.id AS orgId, o.org_code AS orgCode, o.org_name AS orgName, o.status AS orgStatus, ok.enabled AS keyEnabled ");
			builder.append("FROM organisation_key ok ");
			builder.append("JOIN organisation o ON (ok.organisation_id = o.id) ");
			builder.append("WHERE ok.key_code = ? ");
			builder.append("AND ok.key_type = ? ");
			sql = builder.toString();
		}
		return sql;
	}

	private BeanPropertyRowMapper<Organisation> rowMapper() {
		if (rowMapper == null) {
			rowMapper = new BeanPropertyRowMapper<Organisation>(Organisation.class);
		}
		return rowMapper;
	}
}