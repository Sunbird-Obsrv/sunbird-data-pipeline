package org.ekstep.ep.samza.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public TelemetryV3() {
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

		Map<String, List<String>> etags = (Map<String, List<String>>) event.get("etags");
		
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
}
