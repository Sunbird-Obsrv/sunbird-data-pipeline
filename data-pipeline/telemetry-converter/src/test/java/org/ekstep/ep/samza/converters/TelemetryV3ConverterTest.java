package org.ekstep.ep.samza.converters;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.Map;

import org.ekstep.ep.samza.domain.Actor;
import org.ekstep.ep.samza.domain.Context;
import org.ekstep.ep.samza.domain.TelemetryV3;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.reader.TelemetryReaderException;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class TelemetryV3ConverterTest {

	private Map<String, Object> oeStart;
	
	 @Before
	    public void setup() throws FileNotFoundException {
	        oeStart = EventFixture.getEvent("OE_START");
	 }
	 
	 @Test
	 public void convertV3() throws TelemetryReaderException {
		 TelemetryV3Converter converter = new TelemetryV3Converter(oeStart);
		 TelemetryV3 v3 = converter.convert();
		 Map<String, Object> v3Map = v3.toMap();
		 assertEquals(v3Map.get("eid"), "START");
		 assertEquals(v3Map.get("ets"), 1510216719872L);
		 assertEquals(v3Map.get("ver"), "3.0");
		 assertEquals(v3Map.get("mid"), "3f34adf0-89d5-4884-8920-4fadbe9680cd");
		 assertEquals((v3Map.get("actor") instanceof Actor), true);
		 assertEquals((v3Map.get("context") instanceof Context), true);
		 assertEquals((v3Map.get("object") instanceof Object), true);
		 assertEquals(((Map<String, String>)v3Map.get("metadata")).get("checksum"), "3f34adf0-89d5-4884-8920-4fadbe9680cd");

		 Map<String, String> edataMap = (Map<String, String>)v3Map.get("edata");

		 assertEquals(edataMap.get("mode"), "play");
		 assertEquals(edataMap.get("duration"), 0);
		 assertEquals(edataMap.get("type"), "player");
		 assertEquals(edataMap.get("pageid"), "");

		 System.out.println(new Gson().toJson(v3Map));

	 }
}