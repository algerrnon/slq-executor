package com.testme.webservices.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AppConfig {
	private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

	private static AppConfig instance;

	private Config config;
	private List<Query> queries;
	private Config configDb;

	public static void init() {
		instance = new AppConfig();
	}

	private AppConfig() {
		this.config = ConfigFactory.load();
		this.configDb = config.getConfig("db");
		this.queries = createQueries();
	}

	private List<Query> createQueries() {
		return Optional.of("queries").filter(config::hasPath).map(config::getConfigList).orElse(new ArrayList<>())
				.stream().map(Query::new).collect(Collectors.toList());
	}

	public static AppConfig getInstance() {
		if (instance == null) {
			init();
		}
		return instance;
	}

	public List<Query> getQueries() {
		return queries;
	}

	public Config getConfigDb() {
		return configDb;
	}
}
