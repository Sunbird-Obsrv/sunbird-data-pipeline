package org.ekstep.ep.samza.converters;

import java.util.HashMap;
import java.util.Map;

import org.ekstep.ep.samza.domain.EdataObject;
import org.ekstep.ep.samza.domain.Plugin;
import org.ekstep.ep.samza.domain.Question;
import org.ekstep.ep.samza.domain.Target;
import org.ekstep.ep.samza.domain.Visits;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

public class V3Util {

	public static Map<String, String> EVENT_MAP = new HashMap<String, String>();
	private static Map<String, String> EDATA_TYPE_MAP = new HashMap<String, String>();

	private static String eventMappingStr = "OE_START:START,GE_START:START,GE_GENIE_START:START,GE_SESSION_START:START,CP_SESSION_START:START,CE_START:START,OE_END:END,GE_END:END,GE_SESSION_END:END,CE_END:END,OE_NAVIGATE:IMPRESSION,GE_INTERACT:IMPRESSION,CP_IMPRESSION:IMPRESSION,CE_START:IMPRESSION,OE_INTERACT:INTERACT,GE_INTERACT:INTERACT,CP_INTERACT:INTERACT,CE_INTERACT:INTERACT,CE_PLUGIN_LIFECYCLE:INTERACT,OE_ASSESS:ASSESS,OE_ITEM_RESPONSE:RESPONSE,OE_INTERRUPT:INTERRUPT,GE_RESUME:INTERRUPT,GE_INTERRUPT:INTERRUPT,GE_FEEDBACK:FEEDBACK,GE_TRANSFER:SHARE,BE_OBJECT_LIFECYCLE:AUDIT,GE_ERROR:ERROR,CE_ERROR:ERROR,GE_INTERACT:LOG,GE_UPDATE:LOG,GE_API_CALL:LOG,GE_GENIE_START:EXDATA,GE_PARTNER_DATA:EXDATA";
	private static String typeMappingStr = "OE_START:player,GE_GENIE_START:app,GE_SESSION_START:session,CP_SESSION_START:session,CE_START:editor,GE_SESSION_END:session,CP_SESSION_END:session,OE_END:player,GE_END:app,CE_END:editor";

	private static HashMap<String, Object> v3Edata = new HashMap<String, Object>();

	static {
		String[] pairEventArr = eventMappingStr.split(",");
		for (String pair : pairEventArr) {
			String[] eachEventArr = pair.split(":");
			EVENT_MAP.put(eachEventArr[0], eachEventArr[1]);
		}

		String[] pairTypeArr = typeMappingStr.split(",");
		for (String pair : pairTypeArr) {
			String[] eachTypeArr = pair.split(":");
			EDATA_TYPE_MAP.put(eachTypeArr[0], eachTypeArr[1]);
		}
	}

	public static HashMap<String, Object> fetchEdata(Telemetry event) {

		try {
			String eid = event.<String> mustReadValue("eid");
			String v3Eid = EVENT_MAP.get(eid);

			Map<String, Object> edata = event.getEdata();

			switch (v3Eid) {
			case "START":
				v3Edata.put("type", EDATA_TYPE_MAP.get(eid));
				updateStartEdata(edata);
				break;
			case "END":
				v3Edata.put("type", EDATA_TYPE_MAP.get(eid));
				updateEndEdata(edata);
				break;
			case "IMPRESSION":
				updateImpressionEdata(edata);
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
				updateInterruptEdata(edata);
				break;
			case "FEEDBACK":
				updateFeedbackEdata(edata);
				break;
			case "SHARE":
				break;
			case "AUDIT":
				updateAuditEdata(edata);
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
		} catch (TelemetryReaderException e) {

		}
		return null;
	}

	private static void updateStartEdata(Map<String, Object> edata) {
		v3Edata.put("dspec", edata.get("dspec"));
		v3Edata.put("uaspec", edata.get("uaspec"));
		v3Edata.put("loc", edata.get("loc"));
		v3Edata.put("mode", edata.getOrDefault("mode", ""));
		v3Edata.put("duration", edata.getOrDefault("load_time", 0));
		v3Edata.put("pageid", edata.getOrDefault("stageid", ""));
	}

	private static void updateEndEdata(Map<String, Object> edata) {
		v3Edata.put("mode", edata.getOrDefault("mode", ""));
		v3Edata.put("duration", edata.getOrDefault("length", 0));
		v3Edata.put("pageid", edata.get("stageid"));
		HashMap<String, String> summary = new HashMap<>();
		String progress = (String) edata.get("progress");
		summary.put("progress", progress);
		v3Edata.put("summary", summary);
	}

	private static void updateImpressionEdata(Map<String, Object> edata) {
		v3Edata.put("type", edata.getOrDefault("type", ""));
		v3Edata.put("subtype", edata.getOrDefault("itype", ""));
		v3Edata.put("pageid", edata.get("stageid"));
		v3Edata.put("uri", edata.get("uri"));
		v3Edata.put("visits", new Visits());
	}

	private static void updateInteractEdata(Map<String, Object> edata) {

		v3Edata.put("pageid",
				edata.getOrDefault("stageid", edata.getOrDefault("stage", "")));
		v3Edata.put("type", edata.getOrDefault("type", ""));
		v3Edata.put("subtype", edata.getOrDefault("subtype", ""));
		v3Edata.put("id",
				edata.getOrDefault("id", edata.getOrDefault("objectid", "")));
		HashMap<String, Object> extra = new HashMap<String, Object>();
		extra.put("pos", edata.get("pos"));
		extra.put("values", edata.get("values"));
		v3Edata.put("extra", extra);
		v3Edata.put("target", new Target(edata));
		v3Edata.put("plugin", new Plugin(edata));
	}

	private static void updateAssessEdata(Map<String, Object> edata) {
		v3Edata.put("item", new Question(edata));
		v3Edata.put("index", edata.get("qindex"));
		v3Edata.put("pass", edata.get("pass"));
		v3Edata.put("score", edata.get("score"));
		v3Edata.put("resvalues", edata.get("resvalues"));
		v3Edata.put("duration", edata.get("length"));
	}

	private static void updateResponseEdata(Map<String, Object> edata) {

		v3Edata.put("target", new Target(edata));
		v3Edata.put("type", "");
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("state", (String) edata.get("state"));
		values.put("resvalues", (String) edata.get("resvalues"));
		v3Edata.put("values", values);
	}

	private static void updateInterruptEdata(Map<String, Object> edata) {
		v3Edata.put("pageid", edata.getOrDefault("stageid", ""));
		v3Edata.put("type", edata.getOrDefault("type", ""));
	}

	private static void updateFeedbackEdata(Map<String, Object> edata) {
		v3Edata.put("rating", edata.getOrDefault("rating", 0));
		v3Edata.put("comments", edata.getOrDefault("comments", ""));
	}

	private static void updateAuditEdata(Map<String, Object> edata) {
		v3Edata.put("state", edata.getOrDefault("state", ""));
		v3Edata.put("prevstate", edata.getOrDefault("prevstate", ""));
	}

	private static void updateErrorEdata(Map<String, Object> edata) {
		v3Edata.put("err", edata.getOrDefault("err", ""));
		v3Edata.put("errtype", edata.getOrDefault("type", ""));
		v3Edata.put("data", edata.get("stacktrace"));
		v3Edata.put("pageid", edata.get("stage"));
		v3Edata.put("object", new EdataObject(edata));
		v3Edata.put("plugin", new Plugin(edata));
	}

	private static void updateLogEdata(Map<String, Object> edata, String eid) {
		if ("GE_UPDATE".equals(eid)) {
			v3Edata.put("type", "app_update");
			v3Edata.put("level", "info");
		} else if ("GE_API_CALL".equals(eid)) {
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

	private static void updateExDataEdata(Map<String, Object> edata) {
		v3Edata.put("type", "partnerdata");
		v3Edata.put("data", edata);
	}
}
