package org.ekstep.ep.samza.validators;

import java.util.ArrayList;
import java.util.Map;

public class ValidatorFactory {
    public static ArrayList<IValidator> validators(Map<String, Object> map){
        ArrayList<IValidator> validators = new ArrayList<IValidator>();
        validators.add(new TimestampValidator(map));
        validators.add(new UidValidator(map));
        return validators;
    }
}
