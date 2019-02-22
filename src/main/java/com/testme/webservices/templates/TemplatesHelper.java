package com.testme.webservices.templates;

import com.google.common.io.Resources;
import freemarker.template.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public class TemplatesHelper {
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private static Configuration configuration;

	public static void init() {
		final Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
		final DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_28);
		final DefaultObjectWrapper defaultObjectWrapper = builder.build();
		configuration.setObjectWrapper(defaultObjectWrapper);
	}

	public static String createStringFromTemplate(String templateFile, Map<String, Object> model) {

		String transformedTemplate = "";
		try {
			final FileReader fileReader = new FileReader(Resources.getResource(templateFile).getFile());
			final String templateName = templateFile;
			final Template template = new Template(templateName, fileReader, configuration);
			final Writer out = new StringWriter();
			template.process(model, out);
			transformedTemplate = out.toString();
		} catch (IOException ex) {
			log.error("TemplatesHelper IOException", ex);
		} catch (TemplateException ex) {
			log.error("TemplatesHelper TemplateException", ex);
		}
		return transformedTemplate;
	}

	public static void main(String[] args) throws IOException, TemplateException {
		final Map<String, Object> model = new HashMap<String, Object>() {{
			put("value", "8989");
		}};

		System.out.println(createStringFromTemplate("templates/simple.vm", model));
	}

}
