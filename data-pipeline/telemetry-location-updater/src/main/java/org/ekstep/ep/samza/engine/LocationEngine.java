package org.ekstep.ep.samza.engine;

import org.ekstep.ep.samza.domain.Location;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.util.DeviceLocationCache;
import org.ekstep.ep.samza.util.UserLocationCache;

public class LocationEngine {
    private final DeviceLocationCache deviceDeviceLocationCache;
    private final UserLocationCache userLocationCache;
    private static Logger LOGGER = new Logger(LocationEngine.class);

    public LocationEngine(DeviceLocationCache deviceDeviceLocationCache, UserLocationCache userLocationCache) {
        this.deviceDeviceLocationCache = deviceDeviceLocationCache;
        this.userLocationCache = userLocationCache;
    }

    public Location getDeviceLocation(String deviceId) {
        return deviceDeviceLocationCache.getLocationForDeviceId(deviceId);
    }

    public Location getLocationByUser(String userId) {
        return userLocationCache.getLocationByUser(userId);
    }

}
