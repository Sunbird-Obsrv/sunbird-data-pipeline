package org.ekstep.ep.samza.rule;


import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.service.DeviceService;
import org.ekstep.ep.samza.service.LocationService;
import org.ekstep.ep.samza.system.Event;

public class LocationEmpty implements Rule {

    static Logger LOGGER = new Logger(LocationEmpty.class);

    @Override
    public boolean isApplicableTo(Event event) {
        return event.isLocationEmpty();
    }

    @Override
    public void apply(Event event, LocationService locationService, DeviceService deviceService) {
        LOGGER.info(event.id(), "LOCATION EMPTY ", event);
        deviceService.deleteLocation(event.getDid(), event.id());
    }
}
