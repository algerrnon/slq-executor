package com.testme.webservices;

import com.testme.webservices.config.AppConfig;
import com.testme.webservices.config.Query;
import com.testme.webservices.exception.ParameterValueValidationException;
import com.testme.webservices.messages.ErrorMessage;
import com.testme.webservices.templates.TemplatesHelper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2oException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@WebServlet(value = "/sql")
public class SqlServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public void init() {
		DataBase.init();
	}

	private DataBase dataBase = DataBase.getInstance();
	private XmlMapper xmlMapper = new XmlMapper();
	private ObjectMapper jsonMapper = new ObjectMapper();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		setDefaultHeadersAndStatus(response);
		Map<String, Object> parameterMap = ParamsHelper.convertHttpRequestParametersToParmeterMap(request);
		String sqlQueryName = String.valueOf(parameterMap.get("name"));
		parameterMap.remove("name"); //todo
		processRequest(sqlQueryName, parameterMap, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		setDefaultHeadersAndStatus(response);
		Map<String, Object> parameterMap = convertBodyDataByContentTypeToParameterMap(request);
		String sqlQueryName = request.getParameter("name");
		processRequest(sqlQueryName, parameterMap, response);
	}

	private void setDefaultHeadersAndStatus(HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
	}

	private void processRequest(String sqlQueryName, Map<String, Object> parameterMap, HttpServletResponse response) {

		PrintWriter writer = null;
		try {
			writer = response.getWriter();

			Query query = findQueryByName(sqlQueryName);
			if (query != null) {
				Map<String, Object> typedParamMap;
				typedParamMap = ParamsHelper.convertParamValuesToJavaTypedValues(query, parameterMap);
				String queryString = query.getSql();
				List<Map<String, Object>> resultList = dataBase.executeSqlQuery(queryString, typedParamMap);
				writeResponse(resultList, query.getTemplateFile(), writer);
			} else {
				String message = MessageFormat.format("Query not found. name = {0}", sqlQueryName);
				log.trace(message);
				writeRespErrMessage(writer, message);
			}

		} catch (ParameterValueValidationException ex) {
			writeRespErrMessage(writer, ex.getMessage());
		} catch (Sql2oException ex) {
			writeRespErrMessage(writer, ex.getMessage());
		} catch (IOException ex) {
			writeRespErrMessage(writer, ex.getMessage());
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	private Map<String, Object> convertBodyDataByContentTypeToParameterMap(HttpServletRequest request) {
		Map<String, Object> params = new HashMap<>();
		try {
			String data = readStringFromRequestBody(request);
			TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
			};
			String contentType = request.getContentType();
			if (contentType != null) {
				if (contentType.equals(MediaType.JSON_UTF_8.toString())) {
					params = jsonMapper.readValue(data, typeRef);
				} else if (contentType.equals(MediaType.XML_UTF_8.toString())) {
					params = xmlMapper.readValue(data, typeRef);
				} else {
					log.warn("Unsupported ContentType. Body not parsed");
				}
			} else {
				log.warn("ContentType == null. Body not parsed");
			}

		} catch (JsonParseException ex) {
			log.error("Request body not parsed", ex);
		} catch (JsonMappingException ex) {
			log.error("Request body not parsed, mapping error", ex);
		} catch (IOException ex) {
			log.error("Request body not parsed", ex);
		}
		return params;
	}

	private String readStringFromRequestBody(HttpServletRequest request) {
		String data = "";
		try {
			StringBuilder buffer = new StringBuilder();
			BufferedReader reader = request.getReader();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			data = buffer.toString();
		} catch (IOException ex) {
			log.error("Error reading String from request body", ex);
		}
		return data;
	}


	public void writeRespErrMessage(PrintWriter writer, String message) {
		try {
			writer.write(this.jsonMapper.writeValueAsString(new ErrorMessage(message)));
		} catch (JsonProcessingException ex) {
			log.error("Converting ErrorMessage object to Json error", ex);
		}

	}

	private Query findQueryByName(String queryName) {
		return AppConfig.getInstance().getQueries().stream().filter(x -> x.getQueryName().equals(queryName)).findAny().orElse(null);
	}

	private void writeResponse(List<Map<String, Object>> list, Optional<String> templateFile, PrintWriter writer) {
		if (list.size() > 0) {
			if (templateFile.isPresent()) {
				String value = list.get(0).toString();
				final Map<String, Object> model = new HashMap<String, Object>() {{
					put("value", value);
				}};
				writer.write(TemplatesHelper.createStringFromTemplate(templateFile.get(), model));
			} else {
				try {
					writer.write(jsonMapper.writeValueAsString(list.toArray()));
				} catch (JsonProcessingException ex) {
					String message = "Error converting response value to JSON";
					log.warn(message, ex);
					writeRespErrMessage(writer, message);
				}
			}
		}
	}

	@Override
	public void destroy() {
		DataBase.close();
	}
}
