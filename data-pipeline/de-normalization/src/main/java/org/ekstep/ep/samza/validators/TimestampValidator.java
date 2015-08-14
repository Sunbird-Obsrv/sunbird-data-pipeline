package org.ekstep.ep.samza.validators;

import java.util.Map;

public class TimestampValidator implements IValidator{
    private Map<String, Object> map;

    public TimestampValidator(Map<String, Object> map){
        this.map = map;
    }
    @Override
    public Boolean isInvalid() {
        if(map == null || map.isEmpty())
            return true;
        String timeOfEvent = (String) map.get("ts");
        if(timeOfEvent == null || timeOfEvent.isEmpty())
            return true;
        return false;
    }

    @Override
    public String getErrorMessage() {
        return "No ts in the event, skipping the event";
    }

}
