package org.ekstep.ep.samza.fixtures;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Map;

public class EventFixture {
    public static Map<String, Object> getEvent(String eventId) throws FileNotFoundException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(eventId + ".json");
        File file = new File(url.getPath());
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        return new Gson().fromJson(new FileReader(file), type);
    }
}
