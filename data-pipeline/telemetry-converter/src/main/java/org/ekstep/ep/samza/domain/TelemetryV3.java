package org.ekstep.ep.samza.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ekstep.ep.samza.converters.TelemetryV3Converter;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;

public class TelemetryV3 {

	private String eid;
	private long ets;
	private final String ver = "3.0";
	private String mid;
	private Actor actor;
	private Context context;
	private TObject object;
	private HashMap<String, Object> edata;
	private ArrayList<String> tags;
	private HashMap<String, String> metadata;

	private Telemetry reader;
	
	public TelemetryV3(Telemetry reader, Map<String, Object> source) throws TelemetryReaderException {
		
		String v3Eid = TelemetryV3Converter.EVENT_MAP.get(reader
				.<String> mustReadValue("eid"));
		this.eid = v3Eid;
		this.ets = reader.<Long> mustReadValue("ets");
		this.mid = reader.<String> mustReadValue("mid");
		
		HashMap<String, String> metadata = new HashMap<String, String>();
		String checksum = (String) reader.id();
		metadata.put("checksum", checksum);
		this.metadata = metadata;
		this.actor = new Actor(source);
		this.context = new Context(reader);
		this.object = new TObject(reader);
		
		this.reader = reader;
	}
	
	public TelemetryV3() throws TelemetryReaderException {
		
	}

	public HashMap<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(HashMap<String, String> metadata) {
		this.metadata = metadata;
	}

	public long getEts() {
		return ets;
	}

	public void setEts(long ets) {
		this.ets = ets;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getVer() {
		return ver;
	}

	public Actor getActor() {
		return actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	public TObject getObject() {
		return object;
	}

	public void setObject(TObject object) {
		this.object = object;
	}

	public HashMap<String, Object> getEdata() {
		return edata;
	}

	public void setEdata(HashMap<String, Object> edata) {
		this.edata = edata;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public void setTags(Map<String, Object> event) {

		Map<String, List<String>> etags = (Map<String, List<String>>) event
				.get("etags");

		if (!etags.isEmpty()) {
			Set<String> keys = etags.keySet();
			Iterator<String> it = keys.iterator();
			while (it.hasNext()) {
				List<String> tags = etags.get(it.next());
				this.tags.addAll(tags);
			}
		}
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public Map<String, Object> toMap() {
		
		Map<String, Object> v3map = new HashMap<String, Object>();
		
		v3map.put("eid", eid);
		v3map.put("ets", ets);
		v3map.put("ver", ver);
		v3map.put("mid", mid);
		v3map.put("actor", actor);
		v3map.put("context", context);
		v3map.put("object", object);
		v3map.put("metadata", metadata);
		v3map.put("edata", edata);
		v3map.put("tags", tags);
		v3map.put("`@timestamp`", reader.getAtTimestamp());
		
		return v3map;
	}
}
