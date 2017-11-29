package org.ekstep.ep.samza.domain;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.google.gson.Gson;
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
	private ArrayList<String> tags = new ArrayList<>();
	private HashMap<String, String> metadata;

	private Telemetry reader;

	public TelemetryV3(Telemetry reader, Map<String, Object> source)
            throws TelemetryReaderException, NoSuchAlgorithmException {
		this.reader = reader;
		this.eid = getEid(reader.<String> mustReadValue("eid"), reader);
		this.ets = reader.getEts();
		this.mid = computeMid(this.eid, reader.<String> mustReadValue("mid"));
		HashMap<String, String> metadata = new HashMap<String, String>();
		String checksum = reader.id();
		metadata.put("checksum", checksum);
		this.metadata = metadata;
		this.actor = new Actor(source);
		this.context = new Context(reader);
		this.object = new TObject(reader);
	}

	public TelemetryV3() throws TelemetryReaderException {

	}

	private String computeMid(String eid, String oldMid) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        Charset utf8 = Charset.forName("UTF-8");
        byte[] input = (eid + "-" + oldMid).getBytes(utf8);
        byte[] computedMid = digest.digest(input);
        final StringBuilder builder = new StringBuilder();
        for(byte b : computedMid) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

	private String getEid(String eid, Telemetry event) {

		String v3Eid = "";
		
		switch (eid) {
		case "OE_START":
			v3Eid = "START";
			break;
		case "GE_START":
			v3Eid = "START";
			break;
		case "GE_LAUNCH_GAME":
			v3Eid = "START";
			break;
		case "GE_GENIE_START":
			Map<String, Object> dspec = (Map<String, Object>)event.getEdata().get("dspec");
			if(dspec!=null && dspec.containsKey("mdata")){
				v3Eid = "EXDATA";
			}else {
				v3Eid = "START";
			}
			break;
		case "GE_SESSION_START":
			v3Eid = "START";
			break;
		case "CP_SESSION_START":
			v3Eid = "START";
			break;
		case "CE_START":
			v3Eid = "START";
			break;
		case "OE_END":
			v3Eid = "END";
			break;
		case "GE_END":
			v3Eid = "END";
			break;
		case "GE_GENIE_END":
			v3Eid = "END";
			break;
		case "GE_GAME_END":
			v3Eid = "END";
			break;
		case "GE_SESSION_END":
			v3Eid = "END";
			break;
		case "CE_END":
			v3Eid = "END";
			break;
		case "OE_NAVIGATE":
			v3Eid = "IMPRESSION";
			break;
		case "GE_INTERACT":
			
			if(((String)event.getEdata().get("subtype")).equalsIgnoreCase("SHOW")){
				v3Eid = "IMPRESSION";
				//v3Eid = "LOG"; // For IMPRESSION & LOG the condition is same so I commented LOG
			}else {
				v3Eid = "INTERACT";
			}
			break;
		case "CP_IMPRESSION":
			v3Eid = "IMPRESSION";
			break;
		case "OE_INTERACT":
			v3Eid = "INTERACT";
			break;
		case "CP_INTERACT":
			v3Eid = "INTERACT";
			break;
		case "CE_INTERACT":
			v3Eid = "INTERACT";
			break;
		case "CE_PLUGIN_LIFECYCLE":
			v3Eid = "INTERACT";
			break;
		case "OE_ASSESS":
			v3Eid = "ASSESS";
			break;
		case "OE_ITEM_RESPONSE":
			v3Eid = "RESPONSE";
			break;
		case "OE_INTERRUPT":
			v3Eid = "INTERRUPT";
			break;
		case "GE_RESUME":
			v3Eid = "INTERRUPT";
			break;
		case "GE_GENIE_RESUME":
			v3Eid = "INTERRUPT";
			break;
		case "GE_INTERRUPT":
			v3Eid = "INTERRUPT";
			break;
		case "GE_FEEDBACK":
			v3Eid = "FEEDBACK";
			break;
		case "GE_TRANSFER":
			v3Eid = "SHARE";
			break;
		case "BE_OBJECT_LIFECYCLE":
			v3Eid = "AUDIT";
			break;
		case "GE_ERROR":
			v3Eid = "ERROR";
			break;
		case "CE_ERROR":
			v3Eid = "ERROR";
			break;
		case "GE_UPDATE":
			v3Eid = "LOG";
			break;
		case "GE_API_CALL":
			v3Eid = "LOG";
			break;
		case "GE_SERVICE_API_CALL":
			v3Eid = "LOG";
			break;
		case "GE_PARTNER_DATA":
			v3Eid = "EXDATA";
			break;
		case "GE_CREATE_USER":
		    v3Eid = "AUDIT";
			break;
		case "GE_CREATE_PROFILE":
			v3Eid = "AUDIT";
			break;
		default:
			break;

		}
		return v3Eid;
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

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) throws TelemetryReaderException, NoSuchAlgorithmException {
	    this.eid = eid;
        String oldMid = reader.<String>mustReadValue("mid");
	    this.mid = computeMid(this.eid, oldMid);
	}

	public String getVer() {
		return ver;
	}

	public Actor getActor() {
		return actor;
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

	public void setTags(Map<String, Object> event) {

		Map<String, List<String>> etags = (Map<String, List<String>>) event.get("etags");
		if (null != etags && !etags.isEmpty()) {
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
		v3map.put("@timestamp", reader.getAtTimestamp());
		return v3map;
	}

	public String toJson() {
		Map<String, Object> map = toMap();
		return new Gson().toJson(map);
	}
}
