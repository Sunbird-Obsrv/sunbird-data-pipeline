package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.task.DruidProcessorConfig;
import org.ekstep.ep.samza.util.DeviceDataCache;

import java.util.Map;

import static java.text.MessageFormat.format;

public class DeviceDataUpdater extends IEventUpdater {
    static Logger LOGGER = new Logger(DeviceDataUpdater.class);
    private DeviceDataCache deviceCache;

    DeviceDataUpdater(DeviceDataCache deviceCache) {
        this.deviceCache = deviceCache;
    }

    public Event update(Event event) {

        Map device;
        try {
            String did = event.did();
            String channel = event.channel();
            if (did != null && !did.isEmpty()) {
                device = deviceCache.getDataForDeviceId(event.did(), channel);

                if (device != null && !device.isEmpty()) {
                    event.addDeviceData(device);
                }
                else {
                    event.setFlag(DruidProcessorConfig.getDeviceLocationJobFlag(), false);
                }
            }
            return event;
        } catch(Exception ex) {
            LOGGER.error(null,
                    format("EXCEPTION. EVENT: {0}, EXCEPTION:",
                            event),
                    ex);
            return event;
        }
    }
}
