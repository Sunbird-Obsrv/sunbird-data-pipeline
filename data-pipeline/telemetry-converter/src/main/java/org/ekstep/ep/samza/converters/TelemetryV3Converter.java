package org.ekstep.ep.samza.converters;

import java.util.HashMap;
import java.util.Map;

import org.ekstep.ep.samza.domain.TelemetryV3;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

public class TelemetryV3Converter {

	public static Map<String, String> EDATA_TYPE_MAP = new HashMap<String, String>();
	private static String typeMappingStr = "OE_START:player,GE_GENIE_START:app,GE_SESSION_START:session,CP_SESSION_START:session,CE_START:editor,GE_SESSION_END:session,CP_SESSION_END:session,OE_END:player,GE_END:app,CE_END:editor";

	static {
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

		String v3Eid = v3.getEid();
		String eid = reader.<String> mustReadValue("eid");

		v3.setEdata(new EdataConverter(reader).getEdata(v3Eid, eid));
		v3.setTags(source);
		return v3;
	}
}
