package org.ekstep.ep.samza.service;


import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.system.Device;
import org.ekstep.ep.samza.system.Location;

public class DeviceService {

    private final KeyValueStore deviceStore;
    private Device device;
    static Logger LOGGER = new Logger(LocationService.class);

    public DeviceService(KeyValueStore deviceStore) {
        this.deviceStore = deviceStore;
    }

    public void updateLocation(String did, Location location){
        if (did != null && !did.isEmpty()){
            device = new Device(did);
            device.setLocation(location);
            String djson = JsonWriter.objectToJson(device);
            deviceStore.put(did, djson);
        }
    }

    public Location getLocation(String did, String id){
        if(did != null && !did.isEmpty()){
            String storedDevice = (String) deviceStore.get(did);
            if (storedDevice != null) {
                LOGGER.info(id, "PICKING FROM DEVICE STORE CACHE: {}", storedDevice);
                device = (Device) JsonReader.jsonToJava(storedDevice);
                return device.getLocation();
            }
        }
        return null;
    }

    public void deleteLocation(String did, String id) {
        if(did != null && !did.isEmpty()){
            String storedDevice = (String) deviceStore.get(did);
            if (storedDevice != null) {
                LOGGER.info(id, "DELETING DEVICE STORE CACHE ENTRY: {}", storedDevice);
                deviceStore.delete(did);
            }
        }
    }
}
