package com.testme.webservices.config;

import com.testme.webservices.ParamsHelper;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Types;
import java.text.MessageFormat;

public class QueryParam {

	private static final Logger log = LoggerFactory.getLogger(QueryParam.class);

	private String name;
	private int type;

	QueryParam(Config config) {
		name = config.getString("name").toLowerCase();
		String paramTypeString = config.getString("type").trim().toLowerCase();
		type = ParamsHelper.getSupportedParamTypeByName(paramTypeString);
		if (type == Types.NULL) {
			String message = MessageFormat.format("тип параметра не распознан. name: {0}, type: {1}",
					name, paramTypeString);
			log.error(message);
			throw new IllegalArgumentException(message);
		}
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}
}
