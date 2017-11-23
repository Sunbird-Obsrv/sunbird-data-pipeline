package org.ekstep.ep.samza.converters;

import com.google.gson.Gson;
import org.ekstep.ep.samza.domain.*;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.reader.TelemetryReaderException;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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

        assertEquals(v3Map.get("eid"), "START");

        Map<String, String> eData = (Map<String, String>)v3Map.get("edata");
        assertEquals(eData.get("mode"), "");
        assertEquals(eData.get("duration"), 0);
        assertEquals(eData.get("type"), "editor");
        assertEquals(eData.get("pageid"), "");
    }


    @Test
    public void convertCP_IMPRESSION() throws TelemetryReaderException, FileNotFoundException {
        Map<String, Object> cpImpression = EventFixture.getEvent("CP_IMPRESSION");
        TelemetryV3Converter converter = new TelemetryV3Converter(cpImpression);
        TelemetryV3 v3 = converter.convert();
        Map<String, Object> v3Map = v3.toMap();

        assertEquals(v3Map.get("eid"), "IMPRESSION");

        Map<String, Object> eData = (Map<String, Object>)v3Map.get("edata");
        assertEquals(eData.get("pageid"), "com_ekcontent.content");
        assertEquals(eData.get("type"), "view");
        ArrayList<Visit> visits = (ArrayList<Visit>) eData.get("visits");
        assertEquals(visits.get(0).getObjid(), "domain_4083");
    }

    @Test
    public void convertCP_INTERACT() throws TelemetryReaderException, FileNotFoundException {
        Map<String, Object> cpInteraction = EventFixture.getEvent("CP_INTERACT");
        TelemetryV3Converter converter = new TelemetryV3Converter(cpInteraction);
        TelemetryV3 v3 = converter.convert();
        Map<String, Object> v3Map = v3.toMap();

        assertEquals(v3Map.get("eid"), "INTERACT");

        Map<String, Object> eData = (Map<String, Object>)v3Map.get("edata");
        assertEquals(eData.get("subtype"), "create");
        assertEquals(eData.get("type"), "click");

        Context context = (Context) v3Map.get("context");
        assertEquals(context.getEnv(), "textbook");

        Target target = (Target) eData.get("target");
        assertEquals(target.getType(), "click");
    }

    @Test
    public void convertCE_INTERACT() throws TelemetryReaderException, FileNotFoundException {
        Map<String, Object> ceInteract = EventFixture.getEvent("CE_INTERACT");
        TelemetryV3Converter converter = new TelemetryV3Converter(ceInteract);
        TelemetryV3 v3 = converter.convert();
        Map<String, Object> v3Map = v3.toMap();

        Gson gson = new Gson();
        System.out.println("Converted"+gson.toJson(v3Map));

        assertEquals(v3Map.get("eid"), "INTERACT");

        Map<String, Object> eData = (Map<String, Object>)v3Map.get("edata");
        assertEquals(eData.get("type"), "click");
        assertEquals(eData.get("subtype"), "menu");

        Target target = (Target) eData.get("target");
        assertEquals(target.getId(), "previewButton");

        Plugin plugin = (Plugin) eData.get("plugin");
        assertEquals(plugin.getId(), "org.ekstep.ceheader");
        assertEquals(plugin.getVer(), "1.0");
    }
}