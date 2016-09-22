package org.ekstep.ep.samza.rule;


import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.service.DeviceService;
import org.ekstep.ep.samza.service.LocationService;
import org.ekstep.ep.samza.system.Event;

public class LocationAbsent implements Rule {

    static Logger LOGGER = new Logger(LocationAbsent.class);

    @Override
    public boolean isApplicableTo(Event event) {
        return event.isLocationAbsent();
    }

    @Override
    public void apply(Event event, LocationService locationService, DeviceService deviceService) {
        LOGGER.info(event.id(), "TRYING TO PICK FROM DEVICE STORE CACHE {}", event);
    }
}
