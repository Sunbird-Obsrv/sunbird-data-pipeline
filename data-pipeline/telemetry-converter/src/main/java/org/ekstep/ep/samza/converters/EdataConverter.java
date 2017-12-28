package org.ekstep.ep.samza.converters;

import org.ekstep.ep.samza.domain.Plugin;
import org.ekstep.ep.samza.domain.Question;
import org.ekstep.ep.samza.domain.Target;
import org.ekstep.ep.samza.domain.Visit;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdataConverter {

    private Telemetry reader;
    private HashMap<String, Object> v3Edata;

    public EdataConverter(Telemetry reader) {
        this.reader = reader;
    }

    public HashMap<String, Object> getEdata(String v3Eid, String eid) throws TelemetryConversionException {

        v3Edata = new HashMap<>();

        try {

            Map<String, Object> edata = reader.getEdata();
            switch (v3Eid) {
                case "START":
                    v3Edata.put("type", TelemetryV3Converter.EDATA_TYPE_MAP.get(eid));

                    updateStartEdata(edata);
                    break;
                case "END":
                    v3Edata.put("type", TelemetryV3Converter.EDATA_TYPE_MAP.get(eid));
                    updateEndEdata(edata, eid);
                    break;
                case "IMPRESSION":
                    updateImpressionEdata(edata, eid);
                    break;
                case "INTERACT":
                    updateInteractEdata(edata);
                    break;
                case "ASSESS":
                    updateAssessEdata(edata);
                    break;
                case "RESPONSE":
                    updateResponseEdata(edata);
                    break;
                case "INTERRUPT":
                    updateInterruptEdata(edata, eid);
                    break;
                case "FEEDBACK":
                    updateFeedbackEdata(edata);
                    break;
                case "SHARE":
                    updateShareEdata(edata);
                    break;
                case "AUDIT":
                    updateAuditEdata(edata, eid);
                    break;
                case "ERROR":
                    updateErrorEdata(edata);
                    break;
                case "HEARTBEAT":
                    break;
                case "LOG":
                    updateLogEdata(edata, eid);
                    break;
                case "SEARCH":
                    break;
                case "EXDATA":
                    updateExDataEdata(edata);
                default:
                    break;
            }
        } catch (Exception e) {
            throw new TelemetryConversionException("Failed to convert " + eid + ". " + e.getMessage(), null);
        }
        return v3Edata;
    }

    private void updateStartEdata(Map<String, Object> edata) {
        v3Edata.put("dspec", edata.get("dspec"));
        v3Edata.put("uaspec", edata.getOrDefault("uaspec", edata.get("client")));
        v3Edata.put("loc", edata.get("loc"));
        v3Edata.put("mode", edata.getOrDefault("mode", ""));
        v3Edata.put("duration", edata.getOrDefault("load_time", 0));
        v3Edata.put("pageid", edata.getOrDefault("stageid", ""));
    }

    private void updateEndEdata(Map<String, Object> edata, String eid) {
        if ("OE_END".equals(eid)) {
            v3Edata.put("mode", "play");
        } else {
            v3Edata.put("mode", edata.getOrDefault("mode", ""));
        }
        v3Edata.put("duration", ((Number) edata.getOrDefault("length", edata.getOrDefault("duration", 0))).longValue());
        v3Edata.put("pageid", edata.get("stageid"));
        HashMap<String, Object> summary = new HashMap<>();
        Object progress = edata.get("progress");
        summary.put("progress", progress);
        v3Edata.put("summary", summary);
    }

    private void updateImpressionEdata(Map<String, Object> edata, String eid) {
        v3Edata.put("type", edata.getOrDefault("type", ""));
        v3Edata.put("subtype", edata.getOrDefault("itype", ""));
        String pageid = "";
        if ("CP_IMPRESSION".equals(eid)) {
            pageid = (String) edata.get("pageid");
        } else if ("GE_INTERACT".equals(eid)) {
            pageid = (String) edata.get("stageid");
        } else if ("OE_NAVIGATE".equals(eid)) {
            pageid = (String) edata.get("stageto");
        } else if ("CE_START".equals(eid)) {
            pageid = "contenteditor";
            v3Edata.put("type", "edit");
        }
        v3Edata.put("pageid", pageid);
        v3Edata.put("uri", edata.get("uri"));
        ArrayList<Visit> visits = new ArrayList<>();
        visits.add(new Visit(reader));
        v3Edata.put("visits", visits);
    }

    private void updateInteractEdata(Map<String, Object> edata) {

        v3Edata.put("pageid", edata.getOrDefault("stageid", edata.getOrDefault("stage", "")));
        v3Edata.put("type", edata.getOrDefault("type", ""));
        v3Edata.put("subtype", edata.getOrDefault("subtype", ""));
        v3Edata.put("id", edata.getOrDefault("id", edata.getOrDefault("objectid", "")));
        HashMap<String, Object> extra = new HashMap<>();
        extra.put("pos", edata.get("pos"));
        extra.put("values", edata.get("values"));
        extra.put("uri", edata.getOrDefault("uri", ""));
        v3Edata.put("extra", extra);
        v3Edata.put("target", new Target(edata));
        v3Edata.put("plugin", new Plugin(edata));
    }

    private void updateAssessEdata(Map<String, Object> edata) {
        v3Edata.put("item", new Question(edata));
        v3Edata.put("index", edata.get("qindex"));
        v3Edata.put("pass", edata.get("pass"));
        v3Edata.put("score", edata.get("score"));
        v3Edata.put("resvalues", edata.get("resvalues"));
        v3Edata.put("duration", edata.get("length"));
    }

    private void updateResponseEdata(Map<String, Object> edata) {

        v3Edata.put("target", new Target(edata));
        v3Edata.put("type", "");
        HashMap<String, Object> values = new HashMap<>();
        values.put("state", (String) edata.get("state"));
        values.put("resvalues", edata.get("resvalues"));
        v3Edata.put("values", values);
    }

    private void updateInterruptEdata(Map<String, Object> edata, String eid) {
        v3Edata.put("pageid", edata.getOrDefault("stageid", ""));
        if ("GE_RESUME".equals(eid)) {
            v3Edata.put("type", "resume");
        } else {
            v3Edata.put("type", edata.getOrDefault("type", ""));
        }
    }

    private void updateFeedbackEdata(Map<String, Object> edata) {
        v3Edata.put("rating", edata.getOrDefault("rating", 0));
        v3Edata.put("comments", edata.getOrDefault("comments", ""));
    }

    private void updateAuditEdata(Map<String, Object> edata, String eid) {
        if ("BE_OBJECT_LIFECYCLE".equals(eid)) {
            v3Edata.put("state", edata.getOrDefault("state", ""));
            v3Edata.put("prevstate", edata.getOrDefault("prevstate", ""));
        } else {
            v3Edata.put("state", edata);
        }
    }

    private void updateErrorEdata(Map<String, Object> edata) {
        v3Edata.put("err", edata.getOrDefault("err", ""));
        v3Edata.put("errtype", edata.getOrDefault("type", "MOBILEAPP"));
        v3Edata.put("data", edata.get("stacktrace"));
        v3Edata.put("pageid", edata.get("stage"));
        v3Edata.put("plugin", new Plugin(edata));
    }

    private void updateShareEdata(Map<String, Object> edata) {
        final String in = "In";
        final String out = "Out";

        Object eDir = edata.get("direction");
        if (eDir != null) {
            String dir = (String) eDir;
            if ("import".equalsIgnoreCase(dir)) {
                v3Edata.put("dir", in);
            } else if ("export".equalsIgnoreCase(dir)) {
                v3Edata.put("dir", out);
            }
        }

        v3Edata.put("type", edata.getOrDefault("type", "File"));
        v3Edata.put("items", getShareItem(edata));
    }

    private List getShareItem(Map<String, Object> edata) {

        List items = new ArrayList();

        String dataType = (String) edata.getOrDefault("datatype", "");

        List<Map<String, Object>> contents = (List<Map<String, Object>>) edata
                .getOrDefault("contents", new ArrayList<Map<String, Object>>());

        Map<String, String> origin = new HashMap<>();
        Map<String, String> to = new HashMap<>();

        List<Map<String, String>> params = new ArrayList<>();
        Map<String, String> paramsMap = null;

        for (Map<String, Object> content : contents) {

            paramsMap = new HashMap<>();
            paramsMap.put("transfers", Double.toString((Double) content.getOrDefault("transferCount", 0)));
            paramsMap.put("count", Integer.toString((Integer) content.getOrDefault("count", 0)));
            params.add(paramsMap);

            origin.put("id", (String) content.get("origin"));
            origin.put("type", "device");

            to.put("id", "");
            to.put("type", "");

            Map<String, Object> item = new HashMap<>();

            item.put("id", (String) content.get("identifier"));
            item.put("type", dataType);
            Double version = (Double) content.get("pkgVersion");
            String ver = "";
            if (null != version) {
                ver = Double.toString(version);
            }
            item.put("ver", ver);

            item.put("params", params);
            item.put("origin", origin);
            item.put("to", to);

            items.add(item);
        }
        return items;
    }

    private void updateLogEdata(Map<String, Object> edata, String eid) {
        if ("GE_UPDATE".equals(eid)) {
            v3Edata.put("type", "app_update");
            v3Edata.put("level", "info");
        } else if (eid.endsWith("API_CALL")) {
            v3Edata.put("type", "api_call");
            v3Edata.put("level", "trace");
        } else {
            v3Edata.put("type", "");
            v3Edata.put("level", "");
        }
        v3Edata.put("message", edata.get("message"));
        v3Edata.put("pageid", edata.get("id"));

        HashMap<String, String> params = new HashMap<>();
        params.put("mode", (String) edata.get("mode"));
        params.put("ver", (String) edata.get("ver"));
        params.put("size", (String) edata.get("size"));
        params.put("err", (String) edata.get("err"));
        params.put("action", (String) edata.get("referrer.action"));
        params.put("utmsource", (String) edata.get("referrer.utmsource"));
        params.put("utmmedium", (String) edata.get("referrer.utmmedium"));
        params.put("utmcontent", (String) edata.get("referrer.utmcontent"));
        params.put("utmcampaign", (String) edata.get("referrer.utmcampaign"));

        v3Edata.put("params", edata.getOrDefault("values", params));
    }

    private void updateExDataEdata(Map<String, Object> edata) {
        v3Edata.put("type", edata.getOrDefault("dspec.mdata.type", "partnerdata"));
        v3Edata.put("data", edata.getOrDefault("dspec.mdata.id", edata));
    }
}
