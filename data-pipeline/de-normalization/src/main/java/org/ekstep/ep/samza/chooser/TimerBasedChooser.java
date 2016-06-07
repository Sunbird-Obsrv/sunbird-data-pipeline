package org.ekstep.ep.samza.chooser;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.chooser.BaseMessageChooser;
import org.apache.samza.system.chooser.MessageChooser;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.util.SystemTimeProvider;
import org.ekstep.ep.samza.util.TimeProvider;
import org.joda.time.DateTime;

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
        IncomingMessageEnvelope envelope = null;
        DateTime currentTime = timeProvider.getCurrentTime();

        updateWhichQueueToReadFrom(currentTime);

        if (!serveFromRetry) {
            return preferredQue.poll();
        }

        envelope = retryQue.poll();
        if (envelope == null) {
            return preferredQue.poll();
        }

        shortCircuitRetryWindowIfRecycled(envelope, currentTime);
        ifThisIsFirstEventInRetryWindowThenRememberIt(envelope);
        return envelope;
    }

    private void updateWhichQueueToReadFrom(DateTime currentTime) {
        if (isStartOfRetryWindow(currentTime)) {
            LOGGER.info(null, "START OF RETRY WINDOW, FIRST EVENT");
            startTime = currentTime;
            serveFromRetry = true;
            firstEventInRetryWindow = "";
        }
        if (isEndOfRetryWindow(currentTime)) {
            LOGGER.info(null, "END OF RETRY WINDOW");
            resetServeFromRetry(currentTime);
        }
    }

    private boolean isStartOfRetryWindow(DateTime currentTime) {
        return !serveFromRetry && !startTime.plusMillis(delayInMilliSeconds).isAfter(currentTime);
    }

    private boolean isEndOfRetryWindow(DateTime currentTime) {
        return serveFromRetry && startTime.plusMillis(retryTimeInMilliSeconds).isBefore(currentTime);
    }

    private void shortCircuitRetryWindowIfRecycled(IncomingMessageEnvelope envelope, DateTime currentTime) {
        if (firstEventInRetryWindow.equals(getChecksum(envelope))) {
            resetServeFromRetry(currentTime);
            LOGGER.info(null, "RETRY HAS RECYCLED, FORCE FINISHED WINDOW");
        }
    }

    private void ifThisIsFirstEventInRetryWindowThenRememberIt(IncomingMessageEnvelope envelope) {
        if ("".equals(firstEventInRetryWindow)) {
            firstEventInRetryWindow = getChecksum(envelope);
        }
    }

    private String getChecksum(IncomingMessageEnvelope envelope) {
        return (String) ((Map<String, Object>) ((Map<String, Object>)
                envelope.getMessage()).get("metadata")).get("checksum");
    }

    private void resetServeFromRetry(DateTime currentTime) {
        serveFromRetry = false;
        startTime = currentTime;
    }

}
