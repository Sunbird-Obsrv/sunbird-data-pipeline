package org.ekstep.ep.samza.validators;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UidValidatorTest {

    @Test
    public void TestShouldBeInvalidWhenMapIsNull() {
        UidValidator uidValidator = new UidValidator(null);

        assertTrue(uidValidator.isInvalid());
    }

    @Test
    public void ShouldBeInvalidWhenMapIsEmpty() {
        UidValidator uidValidator = new UidValidator(new HashMap<String, Object>());

        assertTrue(uidValidator.isInvalid());
    }

    @Test
    public void ShouldBeInvalidWhenMapDoesNotHaveUidKey() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("some key", "some value");
        UidValidator uidValidator = new UidValidator(map);

        assertTrue(uidValidator.isInvalid());
    }
    @Test
    public void ShouldBeInvalidWhenUidKeyInMapIsEmpty() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("uid", "");
        UidValidator uidValidator = new UidValidator(map);

        assertTrue(uidValidator.isInvalid());
    }

    @Test
    public void ShouldBeValidWhenThereIsUidKeyInMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("uid", "1234321");
        UidValidator uidValidator = new UidValidator(map);

        assertFalse(uidValidator.isInvalid());
    }

    @Test
    public void ShouldPopulateProperErrorMessage() {
        assertEquals("No uid in the event, skipping the event", new UidValidator(null).getErrorMessage());
    }

}