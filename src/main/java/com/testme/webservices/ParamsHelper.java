package com.testme.webservices;

import com.testme.webservices.config.Query;
import com.testme.webservices.config.QueryParam;
import com.testme.webservices.exception.ParameterValueValidationException;
import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.invoke.MethodHandles;
import java.sql.Types;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParamsHelper {

	static {
		ConvertRegisterHelper.registerConverters();
	}

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


	/**
	 * Возвращает для каждого параметра одно значение.
	 * Если в параметре передано несколько значений, разделённых запятыми, то "склеиваем" их в одну строку.
	 * Все значения приводятся к нижему регистру.
	 *
	 * @param request входящий запрос сервлета
	 * @return преобразованные к нижнему регистру одиночные значения параметров запроса
	 */
	static Map<String, Object> convertHttpRequestParametersToParmeterMap(HttpServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		Map<String, Object> stringParameterMap = parameterMap
				.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> String.join(",", entry.getValue()).trim().toLowerCase()));
		return stringParameterMap;
	}

	public static Map<String, Object> convertParamValuesToJavaTypedValues(Query query, Map<String, Object> parameterMap) throws ParameterValueValidationException {
		Map<String, Object> javaTypedParameterMap = new HashMap<>();
		List<QueryParam> paramsDesctriptions = query.getParams();
		for (QueryParam paramDescr : paramsDesctriptions) {
			String paramName = paramDescr.getName();
			int paramType = paramDescr.getType();
			if (paramType == Types.NUMERIC
					|| paramType == Types.DECIMAL
					|| paramType == Types.DOUBLE
					|| paramType == Types.FLOAT) {
				String source = String.valueOf(parameterMap.get(paramName));
				Double value = (Double) ConvertUtils.convert(source, Double.class);
				if (value != null) {
					javaTypedParameterMap.put(paramName, value);
				} else {
					generateException(paramName, source, paramType);
				}
			} else if (paramType == Types.BOOLEAN) {
				String source = String.valueOf(parameterMap.get(paramName));
				Boolean value = (Boolean) ConvertUtils.convert(source, Boolean.class);
				if (value != null) {
					javaTypedParameterMap.put(paramName, value);
				} else {
					generateException(paramName, source, paramType);
				}
			} else if (paramType == Types.BIGINT || paramType == Types.INTEGER || paramType == Types.SMALLINT
					|| paramType == Types.TINYINT) {
				String source = String.valueOf(parameterMap.get(paramName));
				Integer value = (Integer) ConvertUtils.convert(source, Integer.class);
				if (value != null) {
					javaTypedParameterMap.put(paramName, value);
				} else {
					generateException(paramName, source, paramType);
				}
			} else {
				String source = String.valueOf(parameterMap.get(paramName));
				String value = (String) ConvertUtils.convert(source, String.class);
				if (value != null) {
					javaTypedParameterMap.put(paramName, value);
				}
			}
		}
		return javaTypedParameterMap;
	}

	public static void generateException(String paramName, String source, int javaSqlTypesCode) throws ParameterValueValidationException {
		String message = MessageFormat.format("Параметр {0} имеет значение {1} не соответствующее типу java.sql.Types {2}", paramName, source, javaSqlTypesCode);
		log.warn(message);
		throw new ParameterValueValidationException(message);
	}

	public static int getSupportedParamTypeByName(String type) {
		int result;
		if (type.equals("numeric")) {
			result = Types.NUMERIC;
		} else if (type.equals("bigint")) {
			result = java.sql.Types.BIGINT;
		} else if (type.equals("boolean")) {
			result = Types.BOOLEAN;
		} else if (type.equals("decimal")) {
			result = Types.DECIMAL;
		} else if (type.equals("double")) {
			result = Types.DOUBLE;
		} else if (type.equals("float")) {
			result = Types.FLOAT;
		} else if (type.equals("integer")) {
			result = Types.INTEGER;
		} else if (type.equals("nvarchar")) {
			result = Types.NVARCHAR;
		} else if (type.equals("varchar")) {
			result = Types.VARCHAR;
		} else if (type.equals("tinyint")) {
			result = Types.TINYINT;
		} else if (type.equals("smallint")) {
			result = Types.SMALLINT;
		} else if (type.equals("date")) {
			result = Types.DATE;
		} else if (type.equals("timestamp")) {
			result = Types.TIMESTAMP;
		} else {
			result = Types.NULL;
			//неподдерживаемый тип
		}
		return result;
	}
}
