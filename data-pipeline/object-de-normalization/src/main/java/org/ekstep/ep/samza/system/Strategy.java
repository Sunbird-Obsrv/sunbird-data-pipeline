package org.ekstep.ep.samza.system;

import org.ekstep.ep.samza.domain.Event;

import java.io.IOException;

public interface Strategy {
    void execute(Event event) throws IOException;
}
