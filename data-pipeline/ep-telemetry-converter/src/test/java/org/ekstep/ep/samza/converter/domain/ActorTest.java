package org.ekstep.ep.samza.converter.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.reader.TelemetryReaderException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ActorTest {

    private Map<String, Object> oeStart;

    @Before
    public void setup() throws FileNotFoundException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("OE_START.json");
        File file = new File(url.getPath());
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        oeStart = new Gson().fromJson(new FileReader(file), type);
    }

    @Test
    public void actorFromSource() throws TelemetryReaderException {
        Telemetry reader = new Telemetry(oeStart);
        Actor actor = new Actor(oeStart);
        assertEquals(reader.mustReadValue("uid"), actor.getId());
        assertEquals("User", actor.getType());
    }
}