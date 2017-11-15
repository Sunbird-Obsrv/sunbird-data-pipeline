package org.ekstep.ep.samza.converters;

import java.util.HashMap;
import java.util.Map;

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
				break;
			case "ASSESS":
				break;
			case "RESPONSE":
				break;
			case "INTERRUPT":
				break;
			case "FEEDBACK":
				break;
			case "SHARE":
				break;
			case "AUDIT":
				break;
			case "ERROR":
				break;
			case "HEARTBEAT":
				break;
			case "LOG":
				break;
			case "SEARCH":
				break;
			case "EXDATA":

			default:
				break;
			}
		} catch (TelemetryReaderException e) {

		}
		return null;
	}

	private static void updateStartEdata(Map<String, Object> edata) {
		v3Edata.put("dspec", edata.get("edata.eks.dspec"));
		v3Edata.put("uaspec", edata.get("edata.eks.uaspec"));
		v3Edata.put("loc", edata.get("edata.eks.loc"));
		v3Edata.put("mode", edata.getOrDefault("edata.eks.mode", ""));
		v3Edata.put("duration", edata.getOrDefault("edata.eks.load_time", 0));
		v3Edata.put("pageid", edata.getOrDefault("edata.eks.stageid", ""));
	}

	private static void updateEndEdata(Map<String, Object> edata) {
		v3Edata.put("mode", edata.getOrDefault("edata.eks.mode", ""));
		v3Edata.put("duration", edata.getOrDefault("edata.eks.length", 0));
		v3Edata.put("pageid", edata.get("edata.eks.stageid"));
		HashMap<String, String> summary = new HashMap<>();
		String progress = (String) edata.get("edata.eks.progress");
		summary.put("progress", progress);
		v3Edata.put("summary", summary);
	}

	private static void updateImpressionEdata(Map<String, Object> edata) {
		v3Edata.put("type", edata.getOrDefault("edata.eks.type", ""));
		v3Edata.put("subtype", edata.getOrDefault("edata.eks.itype", ""));
		v3Edata.put("pageid", edata.get("edata.eks.stageid"));
		v3Edata.put("uri", edata.get("edata.eks.uri"));
		v3Edata.put("visits", new Visits());
	}
}
