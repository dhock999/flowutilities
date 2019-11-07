package com.boomi.services.flowutilityservice.util;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

class UtilTest {

	@Test
	void testFileUploadTypeFromXML() throws Exception {
		String XML = Util.readResource("resources/fileuploadschema.xml", this.getClass());
		JSONObject type = Util.fileUploadTypeFromXML(XML);
		assertTrue(type.length()>0);
		System.out.println(type.toString(2));
	}

}
