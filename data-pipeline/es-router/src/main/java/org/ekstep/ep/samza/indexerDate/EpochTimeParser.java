package org.ekstep.ep.samza.indexerDate;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by aks on 01/08/17.
 */
public class EpochTimeParser extends TimeParser {
  public static final String TIME_FORMAT = "epoch";
  public EpochTimeParser(Event event, String timeField, String timeFormat) {
    super(event,timeField, timeFormat, TIME_FORMAT);
  }


  @Override
  public boolean canParse() {
    boolean canHandleParsing = TIME_FORMAT.equals(timeFormat);
    boolean valueIsParsable = false;
    if(canHandleParsing){
      Double time = safelyParse(event.getTelemetry());
      valueIsParsable = !(time == null || time.isNaN());
    }

    return canHandleParsing && valueIsParsable;
  }

  private Double safelyParse(Telemetry telemetry){
    try {
      NullableValue<Double> time = telemetry.read(timeField);
      return time.value();
    } catch (ClassCastException e) {
      NullableValue<Long> timeInLong = telemetry.read(timeField);
      return Double.valueOf(timeInLong.value());
    }
  }

  @Override
  public Date parse() throws ParseException {
    if(!canParse()) return new Date();
    Double time = safelyParse(event.getTelemetry());
    return new Date(time.longValue());
  }

  @Override
  public void update(NullableValue<Date> date) {
    if(!date.isNull())
      event.getTelemetry().add(timeField, (double) date.value().getTime());
  }
}
