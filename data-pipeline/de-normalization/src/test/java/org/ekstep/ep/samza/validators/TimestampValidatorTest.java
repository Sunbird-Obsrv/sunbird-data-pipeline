package org.ekstep.ep.samza.validators;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class TimestampValidatorTest{

    @Test
    public void TestShouldBeInvalidWhenMapIsNull() {
        TimestampValidator timestampValidator = new TimestampValidator(null);

        assertTrue(timestampValidator.isInvalid());
    }

    @Test
    public void ShouldBeInvalidWhenMapIsEmpty() {
        TimestampValidator timestampValidator = new TimestampValidator(new HashMap<String, Object>());

        assertTrue(timestampValidator.isInvalid());
    }

    @Test
    public void ShouldBeInvalidWhenMapDoesNotHaveTsKey() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("some key", "some value");
        TimestampValidator timestampValidator = new TimestampValidator(map);

        assertTrue(timestampValidator.isInvalid());
    }
    @Test
    public void ShouldBeInvalidWhenTsKeyInMapIsEmpty() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ts", "");
        TimestampValidator timestampValidator = new TimestampValidator(map);

        assertTrue(timestampValidator.isInvalid());
    }

    @Test
    public void ShouldBeValidWhenThereIsTsKeyInMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ts", "2008-06-16T00:00:00 +0530");
        TimestampValidator timestampValidator = new TimestampValidator(map);

        assertFalse(timestampValidator.isInvalid());
    }

    @Test
    public void ShouldPopulateCorrectErrorMessage() {
        assertEquals("No ts in the event, skipping the event", new TimestampValidator(null).getErrorMessage());
    }
}