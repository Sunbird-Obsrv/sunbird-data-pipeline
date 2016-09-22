package org.ekstep.ep.samza.rule;


import org.ekstep.ep.samza.service.DeviceService;
import org.ekstep.ep.samza.service.LocationService;
import org.ekstep.ep.samza.system.Event;

public interface Rule {
    public boolean isApplicableTo(Event event);
    public void apply(Event event, LocationService locationService, DeviceService deviceService);
}
