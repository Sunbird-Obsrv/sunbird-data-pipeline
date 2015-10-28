package org.ekstep.ep.samza.chooser;

import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.chooser.BaseMessageChooser;
import org.apache.samza.system.chooser.MessageChooser;
import org.ekstep.ep.samza.util.SystemTimeProvider;
import org.ekstep.ep.samza.util.TimeProvider;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.util.ArrayDeque;

public class TimerBasedChooser extends BaseMessageChooser implements MessageChooser {
    private ArrayDeque<IncomingMessageEnvelope> retryQue;
    private ArrayDeque<IncomingMessageEnvelope> preferredQue;
    private int delayInMinutes;
    private int retryTimeInMinutes;
    private String preferredSystemStream = "";
    private DateTime startTime = null;
    private boolean serveFromRetry=false;
    private TimeProvider timeProvider;

    public TimerBasedChooser(int delayInMinutes, int retryTimeInMinutes, String preferredSystemStream) {
        this(delayInMinutes,retryTimeInMinutes,preferredSystemStream,new SystemTimeProvider());
    }

    TimerBasedChooser(int delayInMinutes, int retryTimeInMinutes, String preferredSystemStream, TimeProvider timeProvider) {
        this.preferredQue = new ArrayDeque<IncomingMessageEnvelope>(10);
        this.retryQue = new ArrayDeque<IncomingMessageEnvelope>(10);
        this.delayInMinutes = delayInMinutes;
        this.retryTimeInMinutes = retryTimeInMinutes;
        this.preferredSystemStream = preferredSystemStream;
        this.timeProvider = timeProvider;
        this.startTime=timeProvider.getCurrentTime();
    }

    @Override
    public void update(IncomingMessageEnvelope envelope) {

        if (envelope.getSystemStreamPartition().getSystemStream().getStream().toString().equals(preferredSystemStream)) {
            preferredQue.add(envelope);
            System.out.println("Setting preferredEnvelope");
        } else {
            retryQue.add(envelope);
            System.out.println("Setting wrappedEnvelope");
        }
    }

    @Override
    public IncomingMessageEnvelope choose() {
        IncomingMessageEnvelope envelope = null;
        DateTime currentTime = timeProvider.getCurrentTime();

        envelope = chooseFromRetry(currentTime);
        if(envelope!=null){
            return envelope;
        }

        Minutes minutes = Minutes.minutesBetween(startTime, currentTime);
        if (minutes.isGreaterThan(Minutes.minutes(delayInMinutes))) {
            System.out.println("Starting from retry topic");
            startTime = currentTime;
            envelope = retryQue.poll();
            if(envelope!=null){
                serveFromRetry=true;
            }
        } else {
            envelope = preferredQue.poll();
        }
        return envelope;
    }

    private IncomingMessageEnvelope chooseFromRetry(DateTime currentTime){
        Minutes minutes = Minutes.minutesBetween(startTime, currentTime);
        IncomingMessageEnvelope envelope = null;

        if(serveFromRetry && minutes.isLessThan(Minutes.minutes(retryTimeInMinutes))){
            envelope = retryQue.poll();
        }else if(serveFromRetry){
            resetServeFromRetry(currentTime);
        }
        return envelope;
    }

    private void resetServeFromRetry(DateTime currentTime) {
        serveFromRetry=false;
        startTime=currentTime;
    }

}
