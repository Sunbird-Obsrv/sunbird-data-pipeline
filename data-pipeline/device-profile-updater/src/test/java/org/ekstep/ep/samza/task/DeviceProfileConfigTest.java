package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class DeviceProfileConfigTest {
    private Config configMock;

    @Test
    public void shouldGetTheConfigs() {
        configMock = mock(Config.class);
        stub(configMock.get("output.malformed.topic.name", "telemetry.malformed")).toReturn("telemetry.malformed");
        DeviceProfileUpdaterConfig config = new DeviceProfileUpdaterConfig(configMock);
        assertTrue(config.jobName() == "DeviceProfileUpdater");
        assertTrue(config.malformedTopic() == "telemetry.malformed");
    }

}
