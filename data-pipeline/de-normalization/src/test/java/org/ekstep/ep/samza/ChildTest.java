package org.ekstep.ep.samza;

import org.junit.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class ChildTest {
    @Test
    public void ShouldInitializeChildWithData() {
        HashMap<String, Object> udata = new HashMap<String, Object>();
        udata.put("age_completed_years", 7);
        udata.put("gender", "male");
        udata.put("handle", "user@twitter.com");
        udata.put("standard", 2);

        Child child = new Child("1234567abcd", true, udata);
        HashMap<String, Object> childData = child.getData();

        assertEquals(7, childData.get("age_completed_years"));
        assertEquals("male", childData.get("gender"));
        assertEquals("user@twitter.com", childData.get("handle"));
        assertEquals(2, childData.get("standard"));
        assertTrue(child.isProcessed());

    }

    @Test
    public void ShouldCreateEmptyChildWhenDataNotPresent() {
        Child child = new Child("1234abcd", false, null);

        HashMap<String, Object> childData = child.getData();

        assertFalse(child.isProcessed());
        assertEquals(0, childData.get("age_completed_years"));
        assertEquals(null, childData.get("gender"));
        assertEquals(null, childData.get("handle"));
        assertEquals(null, childData.get("standard"));
    }

    @Test
    public void ShouldNotBeProcessedIfAlreadyProcessed() {
        Child child = new Child("1234abcd", true,  null);

        assertFalse(child.needsToBeProcessed());
    }

    @Test
    public void ShouldNotBeProcessIfUidIsNull() {
        Child child = new Child(null, false, null);

        assertFalse(child.needsToBeProcessed());
    }

    @Test
    public void ShouldNotBeProcessIfUidIsEmpty() {
        Child child = new Child("", false, null);

        assertFalse(child.needsToBeProcessed());
    }

    @Test
    public void ShouldPopulateChildData() {
        Child child = new Child("1123abcd", false, null);

        HashMap<String, Object> childData = new HashMap<String, Object>();
        childData.put("gender", "male");
        childData.put("handle", "user@twitter.com");
        childData.put("year_of_birth",2010);
        childData.put("standard", 2);

        child.populate(childData);

        HashMap<String, Object> calculatedData = child.getData();

        assertTrue(child.isProcessed());
        assertEquals(5, calculatedData.get("age_completed_years"));
        assertEquals("male", calculatedData.get("gender"));
        assertEquals("user@twitter.com", calculatedData.get("handle"));
        assertEquals(2, calculatedData.get("standard"));
    }


    @Test
    public void ShouldNotTryToPopulateWhenChildDataIsEmpty() {
        Child child = new Child("1123abcd", false, null);

        child.populate(new HashMap<String, Object>());

        assertFalse(child.isProcessed());
    }

    @Test
    public void ShouldPopulateOtherFieldsIfNotAllFieldsArePresent() {
        Child child = new Child("1123abcd", false, null);

        HashMap<String, Object> childData = new HashMap<String, Object>();
        childData.put("gender", "male");
        childData.put("handle", "user@twitter.com");
        child.populate(childData);

        assertTrue(child.isProcessed());
    }

    @Test
    public void ShouldNotPopulateAgeIfAgeIsMissing() {
        Child child = new Child("1123abcd", false, null);

        HashMap<String, Object> childData = new HashMap<String, Object>();
        childData.put("gender", "male");
        childData.put("handle", "user@twitter.com");
        child.populate(childData);

        HashMap<String, Object> calculatedData = child.getData();

        assertTrue(child.isProcessed());
        assertEquals(0, calculatedData.get("age_completed_years"));

    }
}