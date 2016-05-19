package org.ekstep.ep.samza.external;

import org.ekstep.ep.samza.Child;

import java.io.IOException;

public interface UserService {
    Child getUserFor(Child child, java.util.Date timeOfEvent) throws IOException;
}
