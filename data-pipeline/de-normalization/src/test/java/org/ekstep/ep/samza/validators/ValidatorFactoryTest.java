package org.ekstep.ep.samza.validators;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class ValidatorFactoryTest{
    @Test
    public void ShouldHaveAllTheValidators() {
        ArrayList<IValidator> validators = ValidatorFactory.validators(new HashMap<String, Object>());

        Assert.assertEquals(2,validators.size());
        Assert.assertEquals(TimestampValidator.class,validators.get(0).getClass());
        Assert.assertEquals(UidValidator.class,validators.get(1).getClass());
    }

}