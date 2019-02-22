package com.testme.webservices;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;

public class ConvertRegisterHelper {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public static void registerConverters(String... datePatterns) {
		ConvertUtils.register(new StringConverter(), String.class);
		//date
		ConvertUtils.register(ConvertRegisterHelper.setPatterns(new DateConverter(null), datePatterns), java.util.Date.class);
		ConvertUtils.register(ConvertRegisterHelper.setPatterns(new SqlDateConverter(null), datePatterns), java.sql.Date.class);
		ConvertUtils.register(ConvertRegisterHelper.setPatterns(new SqlTimeConverter(null), datePatterns), Time.class);
		ConvertUtils.register(ConvertRegisterHelper.setPatterns(new SqlTimestampConverter(null), datePatterns), Timestamp.class);
		//number
		ConvertUtils.register(new BooleanConverter(null), Boolean.class);
		ConvertUtils.register(new ShortConverter(null), Short.class);
		ConvertUtils.register(new IntegerConverter(null), Integer.class);
		ConvertUtils.register(new LongConverter(null), Long.class);
		ConvertUtils.register(new FloatConverter(null), Float.class);
		ConvertUtils.register(new DoubleConverter(null), Double.class);
		ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
		ConvertUtils.register(new BigIntegerConverter(null), BigInteger.class);
	}

	public static void registerConverters() {
		//todo config
		registerConverters("yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS", "HH:mm:ss");
	}

	private static <T extends DateTimeConverter> T setPatterns(T converter, String... patterns) {
		converter.setPatterns(patterns);
		return converter;
	}
}
