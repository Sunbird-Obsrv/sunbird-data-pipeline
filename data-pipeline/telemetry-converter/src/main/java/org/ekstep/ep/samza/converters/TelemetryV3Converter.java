package org.ekstep.ep.samza.converters;

import java.util.HashMap;
import java.util.Map;

import org.ekstep.ep.samza.domain.TelemetryV3;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

public class TelemetryV3Converter {

	public static Map<String, String> EVENT_MAP = new HashMap<String, String>();
	public static Map<String, String> EDATA_TYPE_MAP = new HashMap<String, String>();

	private static String eventMappingStr = "OE_START:START,GE_START:START,GE_GENIE_START:START,GE_SESSION_START:START,CP_SESSION_START:START,CE_START:START,OE_END:END,GE_END:END,GE_SESSION_END:END,CE_END:END,OE_NAVIGATE:IMPRESSION,GE_INTERACT:IMPRESSION,CP_IMPRESSION:IMPRESSION,CE_START:IMPRESSION,OE_INTERACT:INTERACT,GE_INTERACT:INTERACT,CP_INTERACT:INTERACT,CE_INTERACT:INTERACT,CE_PLUGIN_LIFECYCLE:INTERACT,OE_ASSESS:ASSESS,OE_ITEM_RESPONSE:RESPONSE,OE_INTERRUPT:INTERRUPT,GE_RESUME:INTERRUPT,GE_GENIE_RESUME:INTERRUPT,GE_INTERRUPT:INTERRUPT,GE_FEEDBACK:FEEDBACK,GE_TRANSFER:SHARE,BE_OBJECT_LIFECYCLE:AUDIT,GE_ERROR:ERROR,CE_ERROR:ERROR,GE_INTERACT:LOG,GE_UPDATE:LOG,GE_API_CALL:LOG,GE_SERVICE_API_CALL:LOG,GE_GENIE_START:EXDATA,GE_PARTNER_DATA:EXDATA,GE_CREATE_USER:''";
	private static String typeMappingStr = "OE_START:player,GE_GENIE_START:app,GE_SESSION_START:session,CP_SESSION_START:session,CE_START:editor,GE_SESSION_END:session,CP_SESSION_END:session,OE_END:player,GE_END:app,CE_END:editor";

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

	private final Map<String, Object> source;
	private final Telemetry reader;

	public TelemetryV3Converter(Map<String, Object> source) {
		this.source = source;
		this.reader = new Telemetry(source);
	}

	public TelemetryV3 convert() throws TelemetryReaderException {
		TelemetryV3 v3 = new TelemetryV3(reader, source);
		v3.setEdata(new EdataConverter(reader).getEdata());
		v3.setTags(source);
		return v3;
	}
}
