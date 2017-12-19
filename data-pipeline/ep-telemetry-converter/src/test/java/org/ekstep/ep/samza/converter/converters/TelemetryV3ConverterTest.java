package org.ekstep.ep.samza.converter.converters;

import com.google.gson.Gson;
import org.ekstep.ep.samza.converter.domain.*;
import org.ekstep.ep.samza.converter.exceptions.TelemetryConversionException;
import org.ekstep.ep.samza.converter.fixtures.EventFixture;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;

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
        for (CData cdata : context.getCData()) {
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

        Map<String, String> eData = (Map<String, String>) v3Map.get("edata");
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
        assert (start.getEdata().containsKey("uaspec"));

        assert (impression.getEdata().containsKey("visits"));
        assertEquals("", impression.getEdata().get("subtype"));
        assertEquals("edit", impression.getEdata().get("type"));
        assertEquals("contenteditor", impression.getEdata().get("pageid"));
    }

    @Test
    public void convertCE_PLUGIN_LIFECYCLE() throws Exception {
        Map<String, Object> oeStart = EventFixture.getEvent("CE_PLUGIN_LIFECYCLE");
        TelemetryV3Converter converter = new TelemetryV3Converter(oeStart);
        TelemetryV3[] v3Events = converter.convert();

        assertEquals(1, v3Events.length);

        TelemetryV3 lifeCycle = v3Events[0];

        assertEquals("INTERACT", lifeCycle.getEid());
        assertEquals("ContentEditor", lifeCycle.getContext().getEnv());
        assertEquals("load", lifeCycle.getEdata().get("type"));

        Plugin plugin = (Plugin) lifeCycle.getEdata().get("plugin");
        assertEquals("org.ekstep.plugins.funtoot.variationunitary", plugin.getId());
        assertEquals("1.0", plugin.getVer());

        assertEquals("", lifeCycle.getEdata().get("id"));
        assertEquals("", lifeCycle.getEdata().get("pageid"));

        assertNotNull("", lifeCycle.getEdata().get("target"));
        Target target = (Target) lifeCycle.getEdata().get("target");
        assertEquals("", target.getId());
        assertEquals("load", target.getType());
    }

    @Test
    public void convertCP_IMPRESSION() throws Exception {
        Map<String, Object> cpImpression = EventFixture.getEvent("CP_IMPRESSION");
        TelemetryV3Converter converter = new TelemetryV3Converter(cpImpression);
        TelemetryV3[] v3 = converter.convert();
        Map<String, Object> v3Map = v3[0].toMap();

        assertEquals(v3Map.get("eid"), "IMPRESSION");

        Map<String, Object> eData = (Map<String, Object>) v3Map.get("edata");
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

        Map<String, Object> eData = (Map<String, Object>) v3Map.get("edata");
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
        System.out.println("Converted" + gson.toJson(v3Map));

        assertEquals(v3Map.get("eid"), "INTERACT");

        Map<String, Object> eData = (Map<String, Object>) v3Map.get("edata");
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
        for (TelemetryV3 v3 : v3Events) {
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

        Rollup rollup = (Rollup) v3Event.getObject().getRollUp();
        assertEquals("do_2121925679111454721253", rollup.getL1());
        assertEquals("do_30019820", rollup.getL2());
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
        assertEquals(true, audit.toMap().containsKey("@timestamp"));
    }

    @Test
    public void convertGE_RESUME() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("GE_RESUME");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        assertEquals(1, v3Events.length);

        TelemetryV3 resume = v3Events[0];
        assertEquals("resume", resume.getEdata().get("type"));
    }

    @Test
    public void convertGE_PARTNER_DATA() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("GE_PARTNER_DATA");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        assertEquals(1, v3Events.length);

        TelemetryV3 exdata = v3Events[0];
        assertEquals("EXDATA", exdata.getEid());
        assertEquals("partnerdata", exdata.getEdata().get("type"));
    }

    @Test
    public void convertOE_END() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("OE_END");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        assertEquals(1, v3Events.length);

        TelemetryV3 end = v3Events[0];
        assertEquals("END", end.getEid());
        assertEquals("player", end.getEdata().get("type"));
        assertEquals("play", end.getEdata().get("mode"));
        assertEquals(64L, end.getEdata().get("duration"));
        assertEquals("ContentApp-Renderer", end.getEdata().get("pageid"));
        assert (end.getEdata().containsKey("summary"));
        Map<String, Object> summary = (Map<String, Object>) end.getEdata().get("summary");
        assertEquals(100.0, summary.get("progress"));
    }

    @Test
    public void convertOE_INTERACT() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("OE_INTERACT");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        assertEquals(1, v3Events.length);

        TelemetryV3 interact = v3Events[0];
        assertEquals("INTERACT", interact.getEid());
        assertEquals("next", interact.getEdata().get("id"));
        assertEquals("TOUCH", interact.getEdata().get("type"));
        assertEquals("62c379c8-7046-45ec-8a83-78782ba0031c", interact.getEdata().get("pageid"));
        assertEquals("", interact.getEdata().get("subtype"));
        assert (interact.getEdata().containsKey("extra"));

        Map<String, Object> extra = (Map<String, Object>) interact.getEdata().get("extra");
        assertNotNull(extra);
        assertNotNull(extra.get("pos"));
        assertNotNull(extra.get("values"));
        assertEquals("", extra.get("uri"));

        Target target = (Target) interact.getEdata().get("target");
        assertNotNull(target);
        assertEquals("", target.getId());

        Rollup rollup = (Rollup) interact.getObject().getRollUp();
        assertEquals("do_2121925679111454721253", rollup.getL1());
        assertEquals("do_30019820", rollup.getL2());
    }

    @Test
    public void convertOE_INTERRUPT() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("OE_INTERRUPT");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        assertEquals(1, v3Events.length);

        TelemetryV3 interrupt = v3Events[0];
        assertEquals("INTERRUPT", interrupt.getEid());
        assertEquals("eb2691e6-d297-4e57-98c7-746c575bdc2b", interrupt.getEdata().get("pageid"));
        assertEquals("OTHER", interrupt.getEdata().get("type"));
    }

    @Test
    public void convertOE_NAVIGATE() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("OE_NAVIGATE");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        assertEquals(1, v3Events.length);

        TelemetryV3 navigate = v3Events[0];
        assertEquals("IMPRESSION", navigate.getEid());
        assertEquals("", navigate.getEdata().get("type"));
        assertEquals("", navigate.getEdata().get("subtype"));
        assertEquals("55a2dc33-94ed-4212-a5aa-a80ad5b3892d", navigate.getEdata().get("pageid"));
    }

    @Test
    public void convertOE_ASSESS() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("OE_ASSESS");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        assertEquals(1, v3Events.length);


        TelemetryV3 assess = v3Events[0];
        assertEquals("ASSESS", assess.getEid());
        assertEquals("Yes", assess.getEdata().get("pass"));
        assertEquals(35.0, assess.getEdata().get("duration"));
        assertEquals(1.0, assess.getEdata().get("index"));


        assertNotNull(assess.getEdata().get("resvalues"));
        ArrayList<Object> res_values = (ArrayList<Object>) assess.getEdata().get("resvalues");
        assertEquals(4, res_values.size());

        assertNotNull(assess.getEdata().get("item"));
        Question item = (Question) assess.getEdata().get("item");

        assertEquals("do_3123177778625740801346", item.getId());
        assertEquals(0.0, item.getExlength(), 0);
        assertNotNull(item.getParams());
        assertEquals("", item.getUri());
        assertEquals("படத்தைப் பார்த்து அதன் முதல் எழுத்தைக் கண்டுபிடித்துப் பொருத்திக் காட்டுக", item.getDesc());
        assertEquals("முதல் எழுத்தைக் கண்டுபிடி", item.getTitle());
        assertNotNull(item.getMmc());
        assertNotNull(item.getMc());
    }

    @Test
    public void convertOE_ITEM_RESPONSE() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("OE_ITEM_RESPONSE");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        assertEquals(1, v3Events.length);


        TelemetryV3 response = v3Events[0];
        assertEquals("RESPONSE", response.getEid());

        assertNotNull(response.getEdata().get("target"));
        Target target = (Target) response.getEdata().get("target");
        assertEquals("do_31226008849936384013357", target.getId());
        assertEquals("CHOOSE", target.getType());

        assertNotNull(response.getEdata().get("values"));
        Map<String, Object> values = (Map<String, Object>) response.getEdata().get("values");
        assertEquals("SELECTED", values.get("state"));
        assertNotNull(values.get("resvalues"));
        ArrayList<Object> res_values = (ArrayList<Object>) values.get("resvalues");
        assertEquals(1, res_values.size());
    }

    @Test
    public void defaultChannelShouldGetAddedIfChannelFieldIsMissing() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("CE_START_MISSING_CHANNEL");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        TelemetryV3 start = Arrays.stream(v3Events).filter(e -> "START".equals(e.getEid())).findFirst().get();
        TelemetryV3 impression = Arrays.stream(v3Events).filter(e -> "IMPRESSION".equals(e.getEid())).findFirst().get();

        assertEquals("in.ekstep", start.getContext().getChannel());
        assertEquals("in.ekstep", impression.getContext().getChannel());
    }

    @Test
    public void defaultChannelShouldGetAddedIfChannelIsEmpty() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("CE_START_EMPTY_CHANNEL");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        TelemetryV3[] v3Events = converter.convert();
        TelemetryV3 start = Arrays.stream(v3Events).filter(e -> "START".equals(e.getEid())).findFirst().get();
        TelemetryV3 impression = Arrays.stream(v3Events).filter(e -> "IMPRESSION".equals(e.getEid())).findFirst().get();

        assertEquals("in.ekstep", start.getContext().getChannel());
        assertEquals("in.ekstep", impression.getContext().getChannel());
    }

    @Test
    public void eventsWithoutAnyMappingShouldNotGetConverted() throws Exception {
        Map<String, Object> event = EventFixture.getEvent("GE_UPDATE_PROFILE");
        TelemetryV3Converter converter = new TelemetryV3Converter(event);

        try {
            converter.convert();
            fail("converters is converting events even without a mapping!");
        } catch (TelemetryConversionException e) {
            assertEquals("Cannot convert 'GE_UPDATE_PROFILE' to V3 telemetry. No mapping found", e.getMessage());
        }
    }
}
