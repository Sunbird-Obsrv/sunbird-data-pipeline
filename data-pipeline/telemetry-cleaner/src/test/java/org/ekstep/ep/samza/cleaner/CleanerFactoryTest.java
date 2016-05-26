package org.ekstep.ep.samza.cleaner;

import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class CleanerFactoryTest {
    @Test
    public void shouldReturnAllTheCleaners() throws Exception {
        ArrayList<Cleaner> cleaners = CleanerFactory.cleaners();
        assertThat(cleaners, contains(
                instanceOf(ChildDataCleaner.class),
                instanceOf(LocationDataCleaner.class),
                instanceOf(DeviceDataCleaner.class)));
    }
}