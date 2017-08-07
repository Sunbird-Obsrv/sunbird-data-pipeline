package org.ekstep.ep.samza.indexerDate;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.reader.NullableValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by aks on 01/08/17.
 */
public class StringTimeParser extends TimeParser {
  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  public static final String TIME_FORMAT = "string";

  public StringTimeParser(Event event, String timeField, String timeFormat) {
    super(event,timeField, timeFormat, TIME_FORMAT);
  }


  public boolean canParse(){
    boolean canHandleParsing = TIME_FORMAT.equals(timeFormat);
    boolean valueIsParsable = false;
    if(canHandleParsing){
      NullableValue<String> time = event.getTelemetry().read(timeField);
      valueIsParsable = !(time.isNull() || time.value().isEmpty());
    }
    return canHandleParsing && valueIsParsable;
  }
  public Date parse() throws ParseException {
    if(!canParse()) return new Date();
    return event.getTelemetry().getTime(timeField,DATE_FORMAT);
  }

  @Override
  public void update(NullableValue<Date> date) {
    if(!date.isNull()){
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
      event.getTelemetry().add(timeField,simpleDateFormat.format(date.value()));
    }
  }


}
