package org.ekstep.ep.samza.converters;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.Map;

import com.google.gson.GsonBuilder;
import org.ekstep.ep.samza.domain.*;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.reader.TelemetryReaderException;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class TelemetryV3ConverterTest {

    @Test
    public void convertEnvelope() throws FileNotFoundException, TelemetryReaderException {
        Map<String, Object> oeStart = EventFixture.getEvent("OE_START");
        TelemetryV3Converter converter = new TelemetryV3Converter(oeStart);
        TelemetryV3 v3 = converter.convert();
        Map<String, Object> v3Map = v3.toMap();

        assertEquals(v3Map.get("eid"), "START");
        assertEquals(v3Map.get("ets"), 1510216719872L);
        assertEquals(v3Map.get("ver"), "3.0");
        assertEquals(v3Map.get("mid"), "3f34adf0-89d5-4884-8920-4fadbe9680cd");

        Actor actor = (Actor) v3Map.get("actor");
        assertEquals("516a4365-eb22-44c0-add1-0b9000d1d09f", actor.getId());
        assertEquals("User", actor.getType());

        Context context = (Context) v3Map.get("context");
        assertEquals("ContentPlayer", context.getEnv());
        assertEquals("a7a44f3c-b26c-4e21-8224-c504b92ec1b8", context.getSid());
        assertEquals("b24b368416113734693395a39a7c05d460c849dd", context.getDid());
        assertEquals("ekstep", context.getChannel());
        for(CData cdata : context.getCData()) {
            assertEquals("ContentSession", cdata.getType());
            assertEquals("9497e01e1bdc1ff77a65ff1773056b8d", cdata.getId());
        }

        PData pdata = context.getpData();
        assertEquals("in.ekstep", pdata.getId());
        assertEquals("test", pdata.getPid());
        assertEquals("1.0", pdata.getVer());
    }

	 @Test
	 public void convertOE_START() throws TelemetryReaderException, FileNotFoundException {
         Map<String, Object> oeStart = EventFixture.getEvent("OE_START");
         TelemetryV3Converter converter = new TelemetryV3Converter(oeStart);
         TelemetryV3 v3 = converter.convert();
         Map<String, Object> v3Map = v3.toMap();

         Map<String, String> eData = (Map<String, String>)v3Map.get("edata");
		 assertEquals(eData.get("mode"), "play");
		 assertEquals(eData.get("duration"), 0);
		 assertEquals(eData.get("type"), "player");
		 assertEquals(eData.get("pageid"), "");
	 }

    @Test
    public void convertCE_START() throws TelemetryReaderException, FileNotFoundException {
        Map<String, Object> oeStart = EventFixture.getEvent("CE_START");
        TelemetryV3Converter converter = new TelemetryV3Converter(oeStart);
        TelemetryV3 v3 = converter.convert();
        Map<String, Object> v3Map = v3.toMap();

        Map<String, String> eData = (Map<String, String>)v3Map.get("edata");
        assertEquals(eData.get("mode"), "");
        assertEquals(eData.get("duration"), 0);
        assertEquals(eData.get("type"), "editor");
        assertEquals(eData.get("pageid"), "");
    }
}