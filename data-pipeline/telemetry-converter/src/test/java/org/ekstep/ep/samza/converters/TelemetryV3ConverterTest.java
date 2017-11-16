package org.ekstep.ep.samza.converters;

import java.io.FileNotFoundException;
import java.util.Map;

import org.ekstep.ep.samza.domain.TelemetryV3;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.reader.TelemetryReaderException;
import org.junit.Before;
import org.junit.Test;

public class TelemetryV3ConverterTest {

	private Map<String, Object> oeStart;
	
	 @Before
	    public void setup() throws FileNotFoundException {
	        oeStart = EventFixture.getEvent("OE_START");
	 }
	 
	 @Test
	 public void convertV3() throws TelemetryReaderException {
		 
		 TelemetryV3Converter v3convertor = new TelemetryV3Converter(oeStart);
		 TelemetryV3 v3 = v3convertor.convert();
	 }
}
