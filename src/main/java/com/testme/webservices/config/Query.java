package com.testme.webservices.config;

import com.typesafe.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Query {
	private String sql;
	private String queryName;
	private Optional<String> template;
	private List<QueryParam> params;

	Query(Config config) {

		sql = config.getString("sql").toLowerCase();
		queryName = config.getString("queryName").toLowerCase();
		template = Optional.of("template").filter(config::hasPath).map(config::getString);
		params = Optional.of("params").filter(config::hasPath).map(config::getConfigList).orElse(new ArrayList<>())
				.stream().map(QueryParam::new).collect(Collectors.toList());
	}

	public String getSql() {
		return sql;
	}

	public String getQueryName() {
		return queryName;
	}

	public List<QueryParam> getParams() {
		return params;
	}

	public Optional<String> getTemplateFile() {
		return template;
	}

}
