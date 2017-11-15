package org.ekstep.ep.samza.converters;

import org.ekstep.ep.samza.domain.Actor;
import org.ekstep.ep.samza.domain.Context;
import org.ekstep.ep.samza.domain.TObject;
import org.ekstep.ep.samza.domain.TelemetryV3;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TelemetryV3Converter {
	private final Map<String, Object> source;
	private final Telemetry reader;

	public TelemetryV3Converter(Map<String, Object> source) {
		this.source = source;
		this.reader = new Telemetry(source);
	}

	public TelemetryV3 convert() throws TelemetryReaderException {
		TelemetryV3 v3 = convertEnvelope();
		return v3;
	}

	private TelemetryV3 convertEnvelope() throws TelemetryReaderException {
		TelemetryV3 v3 = new TelemetryV3();

		String v3Eid = V3Util.EVENT_MAP.get(reader.<String> mustReadValue("eid"));
		v3.setEid(v3Eid);
		v3.setEts(reader.<Long> mustReadValue("ets"));
		v3.setMid(reader.<String> mustReadValue("mid"));
		v3.setActor(new Actor(source));
		v3.setContext(new Context(reader));
		v3.setObject(new TObject(reader));
		v3.setEdata(V3Util.fetchEdata(reader));
		v3.setTags(source);
		return v3;
	}

	public static void main(String[] args) throws Exception {

		File file = new File("src/test/resources/OE_START.json");
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();

		Map<String, Object> oeStart = new Gson().fromJson(new FileReader(file),
				type);
		Iterator<String> it = oeStart.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (oeStart.get(key) instanceof String) {
				System.out.println((String) oeStart.get(key));
			} else if (oeStart.get(key) instanceof Double) {
				System.out.println((Double) oeStart.get(key));
			} else if (oeStart.get(key) instanceof Long) {
				System.out.println((Long) oeStart.get(key));
			} else if (oeStart.get(key) instanceof List) {
				System.out.println(((List) oeStart.get(key)).toString());
			}
		}

		TelemetryV3Converter conv = new TelemetryV3Converter(oeStart);
		TelemetryV3 v3 = conv.convertEnvelope();
		System.out.println(v3.getEid());
	}
}
