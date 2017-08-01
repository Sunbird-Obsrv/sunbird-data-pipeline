package org.ekstep.ep.samza;

import org.ekstep.ep.samza.reader.NullableValue;

import java.util.regex.Pattern;

/**
 * Created by aks on 01/08/17.
 */
public class Rule {
  private String idPath;
  private String idValue;

  public Rule(String idPath, String idValue) {
    this.idPath = idPath;
    this.idValue = idValue;
  }

  public boolean isApplicable(Event event) {
    NullableValue<String> eventId = event.read(idPath);
    if(eventId.isNull()) return false;
    String eventIdValue = eventId.value();
    Pattern eventIdPattern = Pattern.compile(idValue);
    if(eventIdPattern.matcher(eventIdValue).matches())
      return true;
    return false;
  }
}
