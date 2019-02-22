package com.testme.webservices.deserializers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.io.Resources;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MyTest {
	public static void main(String[] args) throws IOException {

		Map<String, Object> xmlMap;
		File testXmlFile = new File(Resources.getResource("deserializers/test-to-map.xml").getFile());
		XmlMapper xmlMapper = new XmlMapper();
		xmlMap = xmlMapper.readValue(new FileReader(testXmlFile), new TypeReference<Map<String, Object>>() {
		});
		System.out.println(((Map) xmlMap.get("glossary")).get("title"));

		ObjectMapper mapper = new ObjectMapper();
		TypeReference<HashMap<String, Object>> typeRef
				= new TypeReference<HashMap<String, Object>>() {
		};

		File from = new File(Resources.getResource("deserializers/test-to-map.json").getFile());

		HashMap<String, Object> jsonMap = mapper.readValue(from, typeRef);
		System.out.println(((Map) jsonMap.get("glossary")).get("title"));
	}
}
