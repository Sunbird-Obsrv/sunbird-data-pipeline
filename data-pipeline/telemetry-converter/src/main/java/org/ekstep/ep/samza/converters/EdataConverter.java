package org.ekstep.ep.samza.converters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ekstep.ep.samza.domain.EdataObject;
import org.ekstep.ep.samza.domain.Plugin;
import org.ekstep.ep.samza.domain.Question;
import org.ekstep.ep.samza.domain.Target;
import org.ekstep.ep.samza.domain.Visits;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

import com.google.gson.Gson;

public class EdataConverter {

	private Telemetry event;
	private HashMap<String, Object> v3Edata;

	public EdataConverter(Telemetry reader) {
		this.event = reader;
	}

	public HashMap<String, Object> getEdata() {

		v3Edata = new HashMap<String, Object>();

		try {
			String eid = event.<String> mustReadValue("eid");
			// String v3Eid = TelemetryV3Converter.EVENT_MAP.getOrDefault(eid,
			// "");
			String v3Eid = TelemetryV3Converter.EVENT_MAP.get(eid);
			// System.out.println(eid + " -> "+v3Eid);

			// System.out.println(new Gson().toJson(event));

			Map<String, Object> edata = event.getEdata();

			switch (v3Eid) {
			case "START":
				v3Edata.put("type",
						TelemetryV3Converter.EDATA_TYPE_MAP.get(eid));
				updateStartEdata(edata);
				break;
			case "END":
				v3Edata.put("type",
						TelemetryV3Converter.EDATA_TYPE_MAP.get(eid));
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
				updateShareEdata(edata);
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
		return v3Edata;
	}

	private void updateStartEdata(Map<String, Object> edata) {
		v3Edata.put("dspec", edata.get("dspec"));
		v3Edata.put("uaspec", edata.get("uaspec"));
		v3Edata.put("loc", edata.get("loc"));
		v3Edata.put("mode", edata.getOrDefault("mode", ""));
		v3Edata.put("duration", edata.getOrDefault("load_time", 0));
		v3Edata.put("pageid", edata.getOrDefault("stageid", ""));
	}

	private void updateEndEdata(Map<String, Object> edata) {
		v3Edata.put("mode", edata.getOrDefault("mode", ""));
		v3Edata.put("duration", edata.getOrDefault("length", 0));
		v3Edata.put("pageid", edata.get("stageid"));
		HashMap<String, String> summary = new HashMap<>();
		String progress = (String) edata.get("progress");
		summary.put("progress", progress);
		v3Edata.put("summary", summary);
	}

	private void updateImpressionEdata(Map<String, Object> edata) {
		v3Edata.put("type", edata.getOrDefault("type", ""));
		v3Edata.put("subtype", edata.getOrDefault("itype", ""));
		v3Edata.put("pageid", edata.get("stageid"));
		v3Edata.put("uri", edata.get("uri"));
		v3Edata.put("visits", new Visits());
	}

	private void updateInteractEdata(Map<String, Object> edata) {

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
		HashMap<String, String> values = new HashMap<String, String>();
		values.put("state", (String) edata.get("state"));
		values.put("resvalues", (String) edata.get("resvalues"));
		v3Edata.put("values", values);
	}

	private void updateInterruptEdata(Map<String, Object> edata) {
		v3Edata.put("pageid", edata.getOrDefault("stageid", ""));
		v3Edata.put("type", edata.getOrDefault("type", ""));
	}

	private void updateFeedbackEdata(Map<String, Object> edata) {
		v3Edata.put("rating", edata.getOrDefault("rating", 0));
		v3Edata.put("comments", edata.getOrDefault("comments", ""));
	}

	private void updateAuditEdata(Map<String, Object> edata) {
		v3Edata.put("state", edata.getOrDefault("state", ""));
		v3Edata.put("prevstate", edata.getOrDefault("prevstate", ""));
	}

	private void updateErrorEdata(Map<String, Object> edata) {
		v3Edata.put("err", edata.getOrDefault("err", ""));
		v3Edata.put("errtype", edata.getOrDefault("type", ""));
		v3Edata.put("data", edata.get("stacktrace"));
		v3Edata.put("pageid", edata.get("stage"));
		v3Edata.put("object", new EdataObject(edata));
		v3Edata.put("plugin", new Plugin(edata));
	}

	private void updateShareEdata(Map<String, Object> edata) {
		v3Edata.put("dir", edata.get("direction"));
		v3Edata.put("type", edata.getOrDefault("type", "File"));
		v3Edata.put("items", getShareItem(edata));
	}

	private List getShareItem(Map<String, Object> edata) {
		String dataType = (String)edata.getOrDefault("datatype","");
		
		List<Map<String, Object>> contents = (List<Map<String, Object>>) edata
				.getOrDefault("contents", new ArrayList<Map<String, Object>>());

		Map<String, String> obj = new HashMap<String, String>();
		Map<String, String> origin = new HashMap<String, String>();
		Map<String, String> to = new HashMap<String, String>();

		List<Map<String, String>> params = new ArrayList<Map<String, String>>();
		Map<String, String> paramsMap = null;
		 
				
		for (Map<String, Object> content : contents) {
			
			paramsMap = new HashMap<String, String>();
			paramsMap.put("transfers",Integer.toString((Integer)content.get("transferCount")));
			paramsMap.put("count",Integer.toString((Integer)content.get("count")));
			params.add(paramsMap);
			
			
			obj.put("id", (String)content.get("identifier"));
			obj.put("type",dataType);
			obj.put("ver", (String)content.get("pkgVersion"));
			
			origin.put("id", (String)content.get("origin"));
			origin.put("type","device");
			
			to.put("id", "");
			to.put("type", "");
		}

		List items =  new ArrayList();
		items.add(obj);
		items.add(params);
		items.add(origin);
		items.add(to);
		
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
		v3Edata.put("type", "partnerdata");
		v3Edata.put("data", edata);
	}
}
