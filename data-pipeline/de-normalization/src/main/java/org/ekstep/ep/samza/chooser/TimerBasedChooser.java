package org.ekstep.ep.samza.chooser;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.chooser.BaseMessageChooser;
import org.apache.samza.system.chooser.MessageChooser;
import org.ekstep.ep.samza.util.SystemTimeProvider;
import org.ekstep.ep.samza.util.TimeProvider;
import org.joda.time.DateTime;

import java.util.ArrayDeque;

public class TimerBasedChooser extends BaseMessageChooser implements MessageChooser {
    private ArrayDeque<IncomingMessageEnvelope> retryQue;
    private ArrayDeque<IncomingMessageEnvelope> preferredQue;
    private int delayInMilliSeconds;
    private int retryTimeInMilliSeconds;
    private String preferredSystemStream = "";
    private DateTime startTime = null;
    private boolean serveFromRetry = false;
    private TimeProvider timeProvider;

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
        IncomingMessageEnvelope envelope;
        DateTime currentTime = timeProvider.getCurrentTime();

        envelope = chooseFromRetry(currentTime);
        if (envelope != null) {
            return envelope;
        }

        if (!startTime.plusMillis(delayInMilliSeconds).isAfter(currentTime)) {
            startTime = currentTime;
            envelope = retryQue.poll();
            if (envelope != null) {
                serveFromRetry = true;
            } else {
                envelope = preferredQue.poll();
            }
        } else {
            envelope = preferredQue.poll();
        }
        return envelope;
    }

    private IncomingMessageEnvelope chooseFromRetry(DateTime currentTime) {
        IncomingMessageEnvelope envelope = null;

        if (serveFromRetry && (startTime.plusMillis(retryTimeInMilliSeconds).isAfter(currentTime))) {
            envelope = retryQue.poll();
        } else if (serveFromRetry) {
            resetServeFromRetry(currentTime);
        }
        return envelope;
    }

    private void resetServeFromRetry(DateTime currentTime) {
        serveFromRetry = false;
        startTime = currentTime;
    }

}
