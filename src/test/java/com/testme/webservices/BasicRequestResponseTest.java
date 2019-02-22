package com.testme.webservices;


import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.mockrunner.mock.web.WebMockObjectFactory;
import com.mockrunner.servlet.BasicServletTestCaseAdapter;
import com.mockrunner.servlet.ServletTestModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicRequestResponseTest extends BasicServletTestCaseAdapter {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private ServletTestModule testModule;
	private WebMockObjectFactory factory;

	@BeforeEach
	public void setUp() throws Exception {
		factory = new WebMockObjectFactory();
		testModule = new ServletTestModule(factory);
	}

	@DisplayName("endpoint is working for GET method")
	@Test
	public void testStatutCodeGET() {
		int expectedCode = 200;
		testModule.createServlet(SqlServlet.class);
		testModule.doGet();
		assertEquals(expectedCode, factory.getMockResponse().getStatusCode());
	}

	@DisplayName("endpoint is working for POST method, JSON content")
	@Test
	public void testStatutCodePostJson() throws IOException {
		int expectedCode = 200;
		testModule.createServlet(SqlServlet.class);
		//factory.getMockRequest().addHeader("Content-Type", "applicaton/json");
		factory.getMockRequest().setContentType("application/json; charset=utf-8");
		File bodyFile = new File(Resources.getResource("deserializers/test-to-map.json").getFile());
		factory.getMockRequest().setBodyContent(Files.asCharSource(bodyFile, Charsets.UTF_8).read());
		testModule.doPost();
		assertEquals(expectedCode, factory.getMockResponse().getStatusCode());
	}

	@DisplayName("endpoint is working for POST method, XML content")
	@Test
	public void testStatutCodePostXml() throws IOException {
		int expectedCode = 200;
		testModule.createServlet(SqlServlet.class);
		//factory.getMockRequest().addHeader("Content-Type", "applicaton/json");
		factory.getMockRequest().setContentType("text/xml; charset=utf-8");
		File bodyFile = new File(Resources.getResource("deserializers/test-to-map.xml").getFile());
		factory.getMockRequest().setBodyContent(Files.asCharSource(bodyFile, Charsets.UTF_8).read());
		testModule.doPost();
		System.out.println(factory.getMockResponse().getOutputStreamContent());
		assertEquals(expectedCode, factory.getMockResponse().getStatusCode());
	}
}
