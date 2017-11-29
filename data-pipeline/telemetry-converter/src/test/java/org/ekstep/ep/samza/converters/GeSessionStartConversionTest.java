package org.ekstep.ep.samza.converters;

import org.ekstep.ep.samza.domain.*;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.reader.TelemetryReaderException;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class GeSessionStartConversionTest {
    @Test
    public void convertGE_SESSION_START() throws FileNotFoundException, TelemetryReaderException {
        Map<String, Object> oeStart = EventFixture.getEvent("GE_SESSION_START");
        TelemetryV3Converter converter = new TelemetryV3Converter(oeStart);
        TelemetryV3[] v3 = converter.convert();
        Map<String, Object> v3Map = v3[0].toMap();

        Map<String, String> eData = (Map<String, String>) v3Map.get("edata");
        assertEquals("", eData.get("loc"));
        assertEquals(0, eData.get("duration"));
        assertEquals("session", eData.get("type"));
        assertEquals("", eData.get("pageid"));
    }

    @Test
    public void convertGE_SESSION_END() throws FileNotFoundException, TelemetryReaderException {
        Map<String, Object> oeStart = EventFixture.getEvent("GE_SESSION_END");
        TelemetryV3Converter converter = new TelemetryV3Converter(oeStart);
        TelemetryV3[] v3 = converter.convert();
        Map<String, Object> v3Map = v3[0].toMap();

        Map<String, String> eData = (Map<String, String>) v3Map.get("edata");
        assertEquals("", eData.get("mode"));
        assertEquals(5438.0, eData.get("duration"));
        assertEquals("session", eData.get("type"));
    }

    @Test
    public void convertGE_INTERRUPT() throws FileNotFoundException, TelemetryReaderException {
        Map<String, Object> event = EventFixture.getEvent("GE_INTERRUPT");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);
        TelemetryV3[] v3 = converter.convert();
        Map<String, Object> v3Map = v3[0].toMap();

        Map<String, String> eData = (Map<String, String>) v3Map.get("edata");
        assertEquals("", eData.get("pageid"));
        assertEquals("BACKGROUND", eData.get("type"));
    }
}