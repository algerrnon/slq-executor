package com.testme.webservices;

import com.testme.webservices.config.AppConfig;
import com.typesafe.config.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

public class DataBase {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private HikariDataSource dataSource;
	private Sql2o sql2o;

	private HikariDataSource getDataSourceFromConfig(HikariConfig hikariConfig) {
		try {
			return new HikariDataSource(hikariConfig);
		} catch (Exception ex) {
			log.error("Exception in init dataSource", ex);
			throw ex;
		}
	}

	private HikariConfig makeHikariConfig(Config conf) {
		HikariConfig jdbcConfig = new HikariConfig();
		jdbcConfig.setMaximumPoolSize(conf.getInt("maximumPoolSize"));
		jdbcConfig.setMinimumIdle(conf.getInt("minimumIdle"));
		jdbcConfig.setJdbcUrl(conf.getString("jdbcUrl"));
		jdbcConfig.setUsername(conf.getString("username"));
		jdbcConfig.setPassword(conf.getString("password"));

		jdbcConfig.addDataSourceProperty("cachePrepStmts", conf.getBoolean("cachePrepStmts"));
		jdbcConfig.addDataSourceProperty("prepStmtCacheSize", conf.getInt("prepStmtCacheSize"));
		jdbcConfig.addDataSourceProperty("prepStmtCacheSqlLimit", conf.getInt("prepStmtCacheSqlLimit"));
		jdbcConfig.addDataSourceProperty("useServerPrepStmts", conf.getBoolean("useServerPrepStmts"));
		return jdbcConfig;
	}

	public static DataBase getInstance() {
		if (instance == null) {
			init();
		}
		return instance;
	}

	private static DataBase instance;

	public static void init() {
		instance = new DataBase();
	}

	private DataBase() {
		Config dbConfig = AppConfig.getInstance().getConfigDb();
		HikariConfig hikariConfig = makeHikariConfig(dbConfig);
		dataSource = getDataSourceFromConfig(hikariConfig);
		sql2o = new Sql2o(dataSource);
	}


	public static void close() {
		if (instance != null && instance.dataSource != null) {
			instance.dataSource.close();
		}
	}

	public List<Map<String, Object>> executeSqlQuery(String queryString, Map<String, Object> typedParamMap) throws Sql2oException {
		try (org.sql2o.Connection con = sql2o.open()) {
			List<Map<String, Object>> list;
			org.sql2o.Query sql2oQuery = con.createQuery(queryString).throwOnMappingFailure(false);

			for (Map.Entry<String, Object> entry : typedParamMap.entrySet()) {
				sql2oQuery.addParameter(entry.getKey(), entry.getValue());
			}
			return sql2oQuery.executeAndFetchTable().asList();
		} catch (Sql2oException ex) {
			log.trace("Sql exception", ex);
			throw ex;
		}
	}
}
