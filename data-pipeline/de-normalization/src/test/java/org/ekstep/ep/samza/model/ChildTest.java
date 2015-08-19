package org.ekstep.ep.samza.model;

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
        udata.put("age", 112233445);
        udata.put("dob", "2008-06-16 00:00:00 +0530");
        udata.put("age_completed_years", 7);
        udata.put("gender", "male");
        udata.put("uname", "batman");
        udata.put("uekstep_id", "dark_knight");

        Child child = new Child("1234567abcd", true, 123456789, udata);
        HashMap<String, Object> childData = child.getData();

        assertEquals(112233445L, childData.get("age"));
        assertEquals("2008-06-16 00:00:00 +0530", childData.get("dob"));
        assertEquals(7, childData.get("age_completed_years"));
        assertEquals("male", childData.get("gender"));
        assertEquals("batman", childData.get("uname"));
        assertEquals("dark_knight", childData.get("uekstep_id"));
        assertTrue(child.isProcessed());

    }

    @Test
    public void ShouldCreateEmptyChildWhenDataNotPresent() {
        Child child = new Child("1234abcd", false, 123456789, null);

        HashMap<String, Object> childData = child.getData();

        assertFalse(child.isProcessed());
        assertEquals(0l, childData.get("age"));
        assertEquals(null, childData.get("dob"));
        assertEquals(0, childData.get("age_completed_years"));
        assertEquals(null, childData.get("gender"));
        assertEquals(null, childData.get("uname"));
        assertEquals(null, childData.get("uekstep_id"));
    }

    @Test
    public void ShouldNotBeProcessedIfAlreadyProcessed() {
        Child child = new Child("1234abcd", true, 123456789, null);

        assertFalse(child.needsToBeProcessed());
    }

    @Test
    public void ShouldNotBeProcessIfUidIsNull() {
        Child child = new Child(null, false, 123456789, null);

        assertFalse(child.needsToBeProcessed());
    }

    @Test
    public void ShouldNotBeProcessIfUidIsEmpty() {
        Child child = new Child("", false, 123456789, null);

        assertFalse(child.needsToBeProcessed());
    }

    @Test
    public void ShouldPopulateChildData() {
        long timeOfReferenceInMiliSeconds = (31556952 * 3 + 123456789) * 1000L;
        Child child = new Child("1123abcd", false, timeOfReferenceInMiliSeconds, null);
        Timestamp dob = new Timestamp(123456789000L);

        HashMap<String, Object> childData = new HashMap<String, Object>();
        childData.put("name", "Clark Kent");
        childData.put("gender", "male");
        childData.put("ekstep_id", "superman");
        childData.put("dob", dob);

        child.populate(childData);

        HashMap<String, Object> calculatedData = child.getData();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));


        assertTrue(child.isProcessed());
        assertEquals(31556952L*3, calculatedData.get("age"));
        assertEquals(dateFormat.format(dob), calculatedData.get("dob"));
        assertEquals(3, calculatedData.get("age_completed_years"));
        assertEquals("male", calculatedData.get("gender"));
        assertEquals("Clark Kent", calculatedData.get("uname"));
        assertEquals("superman", calculatedData.get("uekstep_id"));
    }

    @Test
    public void ShouldNotTryToPopulateWhenChildDataIsNull() {
        long timeOfReferenceInMiliSeconds = (31556952 * 3 + 123456789) * 1000L;
        Child child = new Child("1123abcd", false, timeOfReferenceInMiliSeconds, null);

        child.populate(null);

        assertFalse(child.isProcessed());
    }

    @Test
    public void ShouldNotTryToPopulateWhenChildDataIsEmpty() {
        long timeOfReferenceInMiliSeconds = (31556952 * 3 + 123456789) * 1000L;
        Child child = new Child("1123abcd", false, timeOfReferenceInMiliSeconds, null);

        child.populate(new HashMap<String, Object>());

        assertFalse(child.isProcessed());
    }

    @Test
    public void ShouldPopulateOtherFieldsIfNotAllFieldsArePresent() {
        long timeOfReferenceInMiliSeconds = (31556952 * 3 + 123456789) * 1000L;
        Child child = new Child("1123abcd", false, timeOfReferenceInMiliSeconds, null);

        HashMap<String, Object> childData = new HashMap<String, Object>();
        childData.put("gender", "male");
        childData.put("name", "some");
        child.populate(childData);

        assertTrue(child.isProcessed());
    }
}