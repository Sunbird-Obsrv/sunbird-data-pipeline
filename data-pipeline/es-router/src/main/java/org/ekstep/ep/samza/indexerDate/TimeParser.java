package org.ekstep.ep.samza.indexerDate;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.reader.NullableValue;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by aks on 01/08/17.
 */
public abstract class TimeParser {
  protected Event event;
  protected String timeField;
  protected String timeFormat;
  private String expectedTimeFormat;

  public TimeParser(Event event, String timeField, String timeFormat, String expectedTimeFormat) {
    this.event = event;
    this.timeField = timeField;
    this.timeFormat = timeFormat;
    this.expectedTimeFormat = expectedTimeFormat;
  }

  public abstract boolean canParse();

  public abstract Date parse() throws ParseException;

  public static TimeParser create(Event event, String timeField, String timeFormat){
    if(StringTimeParser.TIME_FORMAT.equalsIgnoreCase(timeFormat))
      return new StringTimeParser(event,timeField,timeFormat);
    return new EpochTimeParser(event,timeField,timeFormat);
  }

  public abstract void update(NullableValue<Date> secondaryDate);
}
