package org.ekstep.ep.samza.validators;

import java.util.Map;

public class UidValidator implements IValidator{
    private final Map<String, Object> map;

    public UidValidator(Map<String, Object> map){
        this.map = map;
    }
    @Override
    public Boolean isInvalid() {
        if(map == null || map.isEmpty())
            return true;
        String uid = (String) map.get("uid");
        if(uid == null || uid.isEmpty())
            return true;
        return false;
    }

    @Override
    public String getErrorMessage() {
        return "No uid in the event, skipping the event";
    }

}
