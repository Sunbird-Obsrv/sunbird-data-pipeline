package org.ekstep.ep.samza.chooser;

import org.apache.samza.Partition;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.ekstep.ep.samza.util.TimeProvider;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TimerBasedChooserTest {

    public static final String PREFERRED = "preferred";
    public static final String RETRY = "retry";
    private IncomingMessageEnvelope preferredEnvelope;
    private IncomingMessageEnvelope retryEnvelope;

    @Before
    public void setup() {
        preferredEnvelope = new IncomingMessageEnvelope(new SystemStreamPartition("kafka", PREFERRED, new Partition(0)), null, null, 1);
        retryEnvelope = new IncomingMessageEnvelope(new SystemStreamPartition("kafka", RETRY, new Partition(0)), null, null, 1);
    }

    @Test
    public void shouldGetEnvelopeFromPreferredStreamWhenCurrentTimeIsLessThanInitialDelay() {
        DateTime currentTime = DateTime.now();
        MockTimeProvider mockTimeProvider = new MockTimeProvider();
        mockTimeProvider.setCurrentTime(currentTime);
        TimerBasedChooser timerBasedChooser = new TimerBasedChooser(1, 1, PREFERRED, mockTimeProvider);
        registerChooser(timerBasedChooser);

        timerBasedChooser.update(retryEnvelope);
        timerBasedChooser.update(preferredEnvelope);

        IncomingMessageEnvelope envelope = timerBasedChooser.choose();

        assertEquals(preferredEnvelope, envelope);
        assertNull(timerBasedChooser.choose());
    }

    @Test
    public void shouldGetRetryEnvelopeWhenCurrentTimeIsAfterInitialDelay() {
        DateTime currentTime = DateTime.now();
        MockTimeProvider mockTimeProvider = new MockTimeProvider();
        mockTimeProvider.setCurrentTime(currentTime);
        TimerBasedChooser timerBasedChooser = new TimerBasedChooser(2, 3, PREFERRED, mockTimeProvider);
        registerChooser(timerBasedChooser);

        timerBasedChooser.update(retryEnvelope);

        mockTimeProvider.setCurrentTime(currentTime.plusMillis(3));
        assertEquals(retryEnvelope, timerBasedChooser.choose());
    }

    @Test
    public void shouldGetRetryEnvelopeWhenCurrentTimeIsBetweenInitialDelayAndRetryInterval() {
        DateTime currentTime = DateTime.now();
        MockTimeProvider mockTimeProvider = new MockTimeProvider();
        mockTimeProvider.setCurrentTime(currentTime);
        TimerBasedChooser timerBasedChooser = new TimerBasedChooser(5, 5, PREFERRED, mockTimeProvider);
        registerChooser(timerBasedChooser);

        timerBasedChooser.update(retryEnvelope);
        currentTime = currentTime.plusMillis(6);
        mockTimeProvider.setCurrentTime(currentTime);
        assertEquals(retryEnvelope, timerBasedChooser.choose());

        // Current time is between initial delay and retry interval
        timerBasedChooser.update(preferredEnvelope);
        timerBasedChooser.update(retryEnvelope);
        assertEquals(retryEnvelope, timerBasedChooser.choose());

        // Current time is after retry interval
        mockTimeProvider.setCurrentTime(currentTime.plusMillis(5));
        assertEquals(preferredEnvelope, timerBasedChooser.choose());
    }

    @Test
    public void shouldGetPreferredEnvelopeWhenCurrentTimeIsBetweenInitialDelayAndRetryIntervalAndThereIsNotRetryEnvelope() {
        DateTime currentTime = DateTime.now();
        MockTimeProvider mockTimeProvider = new MockTimeProvider();
        mockTimeProvider.setCurrentTime(currentTime);
        TimerBasedChooser timerBasedChooser = new TimerBasedChooser(5, 5, PREFERRED, mockTimeProvider);
        registerChooser(timerBasedChooser);

        // There is only preferred envelope
        timerBasedChooser.update(preferredEnvelope);
        currentTime = currentTime.plusMillis(6);
        mockTimeProvider.setCurrentTime(currentTime);

        // Current time is between initial delay and retry interval
        assertEquals(preferredEnvelope, timerBasedChooser.choose());

        // Current time is after retry interval
        timerBasedChooser.update(retryEnvelope);
        currentTime = currentTime.plusMillis(6);
        mockTimeProvider.setCurrentTime(currentTime);
        assertEquals(retryEnvelope, timerBasedChooser.choose());
    }

    @Test
    public void shouldGetRetryEnvelopeWhenInitialDelayIsZero() {
        DateTime currentTime = DateTime.now();
        MockTimeProvider mockTimeProvider = new MockTimeProvider();
        mockTimeProvider.setCurrentTime(currentTime);
        TimerBasedChooser timerBasedChooser = new TimerBasedChooser(0, 0, PREFERRED, mockTimeProvider);
        registerChooser(timerBasedChooser);

        timerBasedChooser.update(retryEnvelope);

        assertEquals(retryEnvelope, timerBasedChooser.choose());
    }

    private void registerChooser(TimerBasedChooser timerBasedChooser) {
        timerBasedChooser.register(preferredEnvelope.getSystemStreamPartition(), null);
        timerBasedChooser.register(retryEnvelope.getSystemStreamPartition(), null);
        timerBasedChooser.start();
    }

    class MockTimeProvider implements TimeProvider {
        DateTime currentTime;

        @Override
        public DateTime getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(DateTime currentTime) {
            this.currentTime = currentTime;
        }
    }
}

