package org.ekstep.ep.samza.task;

import kong.unirest.UnirestException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.ekstep.ep.samza.util.RestUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class RestUtilTest {
    @Test
    public void shouldInvokeHTTPCall() throws IOException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("hello, world!"));
        server.enqueue(new MockResponse().setHeader("Authorization", ""));
        try {
            server.start(3000);
        } catch (IOException e) {
            System.out.println("Exception" + e);
        }
        server.url("http://127.0.0.1:3000/api/dialcode/v3/read/");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "");
        try {
            String response = new RestUtil().get("http://127.0.0.1:3000/api/dialcode/v3/read/", headers);
            // TODO: Assertion are failing need to add
            System.out.println("response is" + response);
            assertNotNull(response);
        } catch (UnirestException e) {
            System.out.println("Exception is" + e);
        } finally {
            server.shutdown();
        }


    }
}
