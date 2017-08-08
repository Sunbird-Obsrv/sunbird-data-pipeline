package org.ekstep.ep.es_router.config;

import org.ekstep.ep.es_router.domain.Event;
import org.ekstep.ep.es_router.indexerDate.TimeParser;
import org.ekstep.ep.samza.reader.NullableValue;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by aks on 01/08/17.
 */
public class EsIndexDateConfig {
  private String primary;
  private String primaryFormat;
  private String secondary;
  private String secondaryFormat;
  private boolean updatePrimary;

  public EsIndexDateConfig(String primary, String primaryFormat, String secondary, String secondaryFormat, boolean updatePrimary) {
    this.primary = primary;
    this.primaryFormat = primaryFormat;
    this.secondary = secondary;
    this.secondaryFormat = secondaryFormat;
    this.updatePrimary = updatePrimary;
  }

  public String getIndex(String prefix, Event event) throws ParseException {
    NullableValue<Date> date = parseTime(event, primary, primaryFormat);
    if(date.isNull()){
      date = parseTime(event, secondary, secondaryFormat);
      if(updatePrimary)
        update(event,primary,primaryFormat,date);
      }
    return getIndex(prefix,date).value();
    }

  private void update(Event event, String primary, String primaryFormat, NullableValue<Date> secondaryDate) {
    TimeParser timeParser = TimeParser.create(event, primary, primaryFormat);
    timeParser.update(secondaryDate);
  }

  private NullableValue<Date> parseTime(Event event,String dateField, String dateFormat) throws ParseException{
    TimeParser timeParser = TimeParser.create(event, dateField, dateFormat);
    if(timeParser.canParse())
      return new NullableValue<Date>(timeParser.parse());
    return new NullableValue<Date>(null);
  }

  private NullableValue<String> getIndex(String indexPrefix, NullableValue<Date> date){
    Calendar calendar = Calendar.getInstance();
    if(!date.isNull())
      calendar.setTime(date.value());
    return new NullableValue<String>(String.format("%s-%d.%02d",indexPrefix,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1));
  }
}
