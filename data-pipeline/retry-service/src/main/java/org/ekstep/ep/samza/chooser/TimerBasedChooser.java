package org.ekstep.ep.samza.chooser;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.chooser.BaseMessageChooser;
import org.apache.samza.system.chooser.MessageChooser;
import org.joda.time.DateTime;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.util.SystemTimeProvider;
import org.ekstep.ep.samza.util.TimeProvider;

import java.util.ArrayDeque;
import java.util.Map;

public class TimerBasedChooser extends BaseMessageChooser implements MessageChooser {
    private static Logger LOGGER = new Logger(TimerBasedChooser.class);
    private ArrayDeque<IncomingMessageEnvelope> retryQue;
    private ArrayDeque<IncomingMessageEnvelope> preferredQue;
    private int delayInMilliSeconds;
    private int retryTimeInMilliSeconds;
    private String preferredSystemStream = "";
    private DateTime startTime = null;
    private boolean serveFromRetry = false;
    private TimeProvider timeProvider;
    private String firstEventInRetryWindow = "";

    public TimerBasedChooser(int delayInMilliSeconds, int retryTimeInMilliSeconds, String preferredSystemStream) {
        this(delayInMilliSeconds, retryTimeInMilliSeconds, preferredSystemStream, new SystemTimeProvider());
    }

    TimerBasedChooser(int delayInMilliSeconds, int retryTimeInMilliSeconds, String preferredSystemStream,
                      TimeProvider timeProvider) {
        this.preferredQue = new ArrayDeque<IncomingMessageEnvelope>(10);
        this.retryQue = new ArrayDeque<IncomingMessageEnvelope>(10);
        this.delayInMilliSeconds = delayInMilliSeconds;
        this.retryTimeInMilliSeconds = retryTimeInMilliSeconds;
        this.preferredSystemStream = preferredSystemStream;
        this.timeProvider = timeProvider;
        this.startTime = timeProvider.getCurrentTime();
    }

    @Override
    public void update(IncomingMessageEnvelope envelope) {
        if (envelope.getSystemStreamPartition().getSystemStream().getStream().toString().equals(preferredSystemStream)) {
            preferredQue.add(envelope);
        } else {
            retryQue.add(envelope);
        }
    }

    @Override
    public IncomingMessageEnvelope choose() {
        DateTime currentTime = timeProvider.getCurrentTime();

        if (shortCircuitRetryWindowIfItHasRecycled(currentTime)) {
            return preferredQue.poll();
        }
        ifStartOfRetryWindowSwitchToRetryQueue(currentTime);
        ifEndOfRetryWindowSwitchToPreferredQueue(currentTime);

        return !serveFromRetry || retryQue.isEmpty()
                ? preferredQue.poll()
                : retryQue.poll();
    }

    private boolean shortCircuitRetryWindowIfItHasRecycled(DateTime currentTime) {
        if (!serveFromRetry
                || retryQue.isEmpty()
                || !firstEventInRetryWindow.equals(getChecksumOf(retryQue.peek()))) {
            return false;
        }

        switchToPreferredQueue(currentTime);
        LOGGER.info(null, "RETRY HAS RECYCLED, FORCE FINISHED WINDOW");
        return true;
    }

    private void ifStartOfRetryWindowSwitchToRetryQueue(DateTime currentTime) {
        if (isStartOfRetryWindow(currentTime)) {
            switchToRetryQueue(currentTime);
            firstEventInRetryWindow = getChecksumOf(retryQue.peek());
            LOGGER.info(null, "START OF RETRY WINDOW, FIRST EVENT {}", firstEventInRetryWindow);
        }
    }

    private void ifEndOfRetryWindowSwitchToPreferredQueue(DateTime currentTime) {
        if (isEndOfRetryWindow(currentTime)) {
            LOGGER.info(null, "END OF RETRY WINDOW");
            switchToPreferredQueue(currentTime);
        }
    }

    private boolean isStartOfRetryWindow(DateTime currentTime) {
        return !serveFromRetry
                && !startTime.plusMillis(delayInMilliSeconds).isAfter(currentTime)
                && !retryQue.isEmpty();
    }

    private boolean isEndOfRetryWindow(DateTime currentTime) {
        return serveFromRetry && startTime.plusMillis(retryTimeInMilliSeconds).isBefore(currentTime);
    }

    private void switchToPreferredQueue(DateTime currentTime) {
        serveFromRetry = false;
        startTime = currentTime;
    }

    private void switchToRetryQueue(DateTime currentTime) {
        startTime = currentTime;
        serveFromRetry = true;
    }

    private String getChecksumOf(IncomingMessageEnvelope envelope) {
        return (String) ((Map<String, Object>) ((Map<String, Object>)
                envelope.getMessage()).get("metadata")).get("checksum");
    }
}
