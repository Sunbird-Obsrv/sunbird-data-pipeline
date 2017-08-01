package org.ekstep.ep.samza.indexerDate;

import org.ekstep.ep.samza.Event;
import org.ekstep.ep.samza.reader.NullableValue;

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
      NullableValue<Double> time = event.getTelemetry().read(timeField);
      valueIsParsable = !(time.isNull() || time.value().isNaN());
    }

    return canHandleParsing && valueIsParsable;
  }

  @Override
  public Date parse() throws ParseException {
    if(!canParse()) return new Date();
    NullableValue<Double> time = event.read(timeField);
    return new Date(time.value().longValue());
  }

  @Override
  public void update(NullableValue<Date> date) {
    if(!date.isNull())
      event.getTelemetry().add(timeField, (double) date.value().getTime());
  }
}
