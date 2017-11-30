package org.ekstep.ep.samza.converters;

import com.google.gson.Gson;
import org.ekstep.ep.samza.domain.*;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TelemetryV3ConverterTest {

    @Test
    public void convertEnvelope() throws Exception {
        Map<String, Object> oeStart = EventFixture.getEvent("OE_START");
        TelemetryV3Converter converter = new TelemetryV3Converter(oeStart);
        TelemetryV3[] v3 = converter.convert();
        Map<String, Object> v3Map = v3[0].toMap();

        assertEquals(v3Map.get("eid"), "START");
        assertEquals(v3Map.get("ets"), 1510216719872L);
        assertEquals(v3Map.get("ver"), "3.0");
        assertEquals(v3Map.get("mid"), "START:3f34adf0-89d5-4884-8920-4fadbe9680cd");

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
	 public void convertOE_START() throws Exception {
         Map<String, Object> oeStart = EventFixture.getEvent("OE_START");
         TelemetryV3Converter converter = new TelemetryV3Converter(oeStart);
         TelemetryV3[] v3 = converter.convert();
         Map<String, Object> v3Map = v3[0].toMap();

         Map<String, String> eData = (Map<String, String>)v3Map.get("edata");
		 assertEquals(eData.get("mode"), "play");
		 assertEquals(eData.get("duration"), 0);
		 assertEquals(eData.get("type"), "player");
		 assertEquals(eData.get("pageid"), "");
	 }

    @Test
    public void convertCE_START() throws Exception {
        Map<String, Object> oeStart = EventFixture.getEvent("CE_START");
        TelemetryV3Converter converter = new TelemetryV3Converter(oeStart);
        TelemetryV3[] v3Events = converter.convert();

        assertEquals(2, v3Events.length);

        TelemetryV3 start = Arrays.stream(v3Events).filter(e -> "START".equals(e.getEid())).findFirst().get();
        TelemetryV3 impression = Arrays.stream(v3Events).filter(e -> "IMPRESSION".equals(e.getEid())).findFirst().get();

        assertNotEquals(start.getMid(), impression.getMid());

        assertEquals("", start.getEdata().get("mode"));
        assertEquals(0, start.getEdata().get("duration"));
        assertEquals("editor", start.getEdata().get("type"));
        assertEquals("", start.getEdata().get("pageid"));
        assert(start.getEdata().containsKey("uaspec"));

        assert(impression.getEdata().containsKey("visits"));
        assertEquals("", impression.getEdata().get("subtype"));
        assertEquals("edit", impression.getEdata().get("type"));
        assertEquals("contenteditor", impression.getEdata().get("pageid"));
    }

    @Test
    public void convertCP_IMPRESSION() throws Exception {
        Map<String, Object> cpImpression = EventFixture.getEvent("CP_IMPRESSION");
        TelemetryV3Converter converter = new TelemetryV3Converter(cpImpression);
        TelemetryV3[] v3 = converter.convert();
        Map<String, Object> v3Map = v3[0].toMap();

        assertEquals(v3Map.get("eid"), "IMPRESSION");

        Map<String, Object> eData = (Map<String, Object>)v3Map.get("edata");
        assertEquals(eData.get("pageid"), "com_ekcontent.content");
        assertEquals(eData.get("type"), "view");
        ArrayList<Visit> visits = (ArrayList<Visit>) eData.get("visits");
        assertEquals(visits.get(0).getObjid(), "domain_4083");
    }

    @Test
    public void convertCP_INTERACT() throws Exception {
        Map<String, Object> cpInteraction = EventFixture.getEvent("CP_INTERACT");
        TelemetryV3Converter converter = new TelemetryV3Converter(cpInteraction);
        TelemetryV3[] v3 = converter.convert();
        Map<String, Object> v3Map = v3[0].toMap();

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
    public void convertCE_INTERACT() throws Exception {
        Map<String, Object> ceInteract = EventFixture.getEvent("CE_INTERACT");
        TelemetryV3Converter converter = new TelemetryV3Converter(ceInteract);
        TelemetryV3[] v3 = converter.convert();
        Map<String, Object> v3Map = v3[0].toMap();

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

    @Test
    public void convertGE_SESSION_START() throws Exception {
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
    public void convertGE_SESSION_END() throws Exception {
        Map<String, Object> oeStart = EventFixture.getEvent("GE_SESSION_END");
        TelemetryV3Converter converter = new TelemetryV3Converter(oeStart);
        TelemetryV3[] v3 = converter.convert();
        Map<String, Object> v3Map = v3[0].toMap();

        Map<String, String> eData = (Map<String, String>) v3Map.get("edata");
        assertEquals("", eData.get("mode"));
        assertEquals(5438L, eData.get("duration"));
        assertEquals("session", eData.get("type"));
    }

    @Test
    public void convertGE_INTERRUPT() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("GE_INTERRUPT");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);
        TelemetryV3[] v3 = converter.convert();
        Map<String, Object> v3Map = v3[0].toMap();

        Map<String, String> eData = (Map<String, String>) v3Map.get("edata");
        assertEquals("", eData.get("pageid"));
        assertEquals("BACKGROUND", eData.get("type"));
    }

    @Test
    public void convertGE_INTERACT() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("GE_INTERACT");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);
        TelemetryV3[] v3 = converter.convert();
        Map<String, Object> v3Map = v3[0].toMap();

        assertEquals(1, v3.length);
        assertEquals("INTERACT", v3[0].getEid());
    }

    @Test
    public void convertGE_INTERACT_SUBTYPE_SHOW() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("GE_INTERACT_SUBTYPE_SHOW");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);
        TelemetryV3[] v3 = converter.convert();

        assertEquals(3, v3.length);
        assertEquals(1, Arrays.stream(v3).filter(e -> "IMPRESSION".equals(e.getEid())).count());
        assertEquals(1, Arrays.stream(v3).filter(e -> "LOG".equals(e.getEid())).count());
        assertEquals(1, Arrays.stream(v3).filter(e -> "INTERACT".equals(e.getEid())).count());

        TelemetryV3 impression = Arrays.stream(v3).filter(e -> "IMPRESSION".equals(e.getEid())).findFirst().get();
        assertEquals(true, impression.getEdata().containsKey("visits"));
        assertEquals("OTHER", impression.getEdata().get("type"));
        assertEquals("Genie-TelemetrySync", impression.getEdata().get("pageid"));

        TelemetryV3 log = Arrays.stream(v3).filter(e -> "LOG".equals(e.getEid())).findFirst().get();
        assertEquals(true, log.getEdata().containsKey("params"));

        TelemetryV3 interact = Arrays.stream(v3).filter(e -> "INTERACT".equals(e.getEid())).findFirst().get();
        assertEquals(true, interact.getEdata().containsKey("plugin"));
        assertEquals("show", interact.getEdata().get("subtype"));
        assertEquals("Genie-TelemetrySync", interact.getEdata().get("pageid"));
        assertEquals("OTHER", interact.getEdata().get("type"));
    }

    @Test
    public void convertGE_INTERACT_SUBTYPE_SHOW_MID_Should_Be_Different() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("GE_INTERACT_SUBTYPE_SHOW");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);
        TelemetryV3[] v3Events = converter.convert();
        HashSet<String> uniqueMIDs = new HashSet<>();
        for(TelemetryV3 v3 : v3Events) {
            uniqueMIDs.add(v3.getMid());
        }

        assertEquals(3, uniqueMIDs.size());
    }

    @Test
    public void convertCE_END() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("CE_END");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        assertEquals(1, v3Events.length);

        TelemetryV3 end = v3Events[0];
        assertEquals("END", end.getEid());
        assertEquals(15808638L, end.getEdata().get("duration"));
        assertEquals("editor", end.getEdata().get("type"));
    }

    @Test
    public void convertGE_INTERACT_Object_Type_ShouldNotBeContent() throws Exception {
    	Map<String, Object> event = EventFixture.getEvent("GE_INTERACT");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);
        TelemetryV3 v3Event = (converter.convert())[0];
        String objType = v3Event.getObject().getType();
        assertNotEquals("Content", objType);
        assertEquals("", objType);
    }
    
    @Test
    public void convertGE_ERROR() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("GE_ERROR");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        assertEquals(1, v3Events.length);

        TelemetryV3 error = v3Events[0];
        assertEquals("ERROR", error.getEid());
        assertEquals("INVALID_USER", error.getEdata().get("err"));
        assertEquals("GENIESDK", error.getEdata().get("errtype"));
    }

    @Test
    public void convertCE_ERROR() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("CE_ERROR");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        assertEquals(1, v3Events.length);

        TelemetryV3 error = v3Events[0];
        assertEquals("ERROR", error.getEid());
        assertEquals("content", error.getContext().getEnv());
        assertEquals("06b6c11c-743a-4a30-a5c9-b1e7644ded12", error.getEdata().get("pageid"));
        assertEquals("org.ekstep.text", error.getObject().getId());
        assertEquals("plugin", error.getObject().getType());
        assertEquals("", error.getEdata().get("err"));
        assertEquals("", error.getEdata().get("errtype"));

    }

    @Test
    public void convertBE_OBJECT_LIFECYCLE() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("BE_OBJECT_LIFECYCLE");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        assertEquals(1, v3Events.length);

        TelemetryV3 audit = v3Events[0];
        assertEquals("AUDIT", audit.getEid());
        assertEquals("Asset", audit.getObject().getType());
        assertEquals("do_31238594379452416022722", audit.getObject().getId());
        assertEquals("audio", audit.getObject().getSubType());
        assertEquals("", audit.getObject().getParent().get("id"));
        assertEquals("", audit.getObject().getParent().get("type"));
        assertEquals("Live", audit.getEdata().get("state"));
        assertEquals("Draft", audit.getEdata().get("prevstate"));
    }
}