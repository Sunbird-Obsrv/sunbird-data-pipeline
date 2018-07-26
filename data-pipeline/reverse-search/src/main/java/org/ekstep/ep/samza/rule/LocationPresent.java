package org.ekstep.ep.samza.rule;


import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.service.DeviceService;
import org.ekstep.ep.samza.service.LocationService;
import org.ekstep.ep.samza.system.Event;
import org.ekstep.ep.samza.system.Location;

public class LocationPresent implements Rule {

    static Logger LOGGER = new Logger(LocationPresent.class);

    @Override
    public boolean isApplicableTo(Event event) {
        return event.isLocationPresent();
    }

    @Override
    public void apply(Event event, LocationService locationService, DeviceService deviceService) {
        String coordinates = event.getGPSCoordinates();
        Location location = locationService.getLocation(coordinates, event.id());
        LOGGER.info(event.id(), "UPDATING DEVISE STORE CACHE", event);
        deviceService.updateLocation(event.getDid(), location);
    }
}
