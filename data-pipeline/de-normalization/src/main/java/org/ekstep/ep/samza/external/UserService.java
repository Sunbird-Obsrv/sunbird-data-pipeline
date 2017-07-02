package org.ekstep.ep.samza.external;

import org.ekstep.ep.samza.Child;

import java.io.IOException;
import java.util.Date;

public interface UserService {
    Child getUserFor(Child child, Date timeOfEvent, String eventId, String channelId) throws IOException;
}
