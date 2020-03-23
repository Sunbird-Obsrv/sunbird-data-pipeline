package org.ekstep.ep.samza.reader;

import org.ekstep.ep.samza.domain.EventFixture;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TelemetryTest {


    @Test
    public void shouldReadTheDefaultValue() {
        Telemetry telemetry = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT_MISSING_FIELDS));
        Assert.assertEquals(telemetry.readOrDefault("context.channel", "in.sunbird").value(), "in.sunbird");
    }

    @Test
    public void shouldReadTheActualValue() {
        Telemetry telemetry = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertEquals(telemetry.readOrDefault("context.channel", "in.sunbird").value(), "505c7c48ac6dc1edc9b08f21db5a571d");
    }


    @Test
    public void shouldGetFirstStringValue() {
        Map hashMap = new HashMap<String, String>();
        hashMap.put("key1", "value1");
        Telemetry telemetry = new Telemetry(hashMap);
        NullableValue nullableValue = telemetry.read("key1");
        assertEquals("value1", nullableValue.value());
        assertFalse(nullableValue.isNull());
    }

    @Test
    public void shouldGetFirstMapValue() {
        Map hashMap = new HashMap<String, Object>();
        HashMap<String, String> nestedObject = new HashMap<String, String>();
        nestedObject.put("key2", "get");
        hashMap.put("key1", nestedObject);
        Telemetry telemetry = new Telemetry(hashMap);
        NullableValue nullableValue = telemetry.read("key1");
        assertEquals(nestedObject, nullableValue.value());
        assertFalse(nullableValue.isNull());
    }

    @Test
    public void shouldGetOneNestedStringValue() {
        Map hashMap = new HashMap<String, Object>();
        HashMap<String, String> nestedObject = new HashMap<String, String>();
        nestedObject.put("key2", "get");
        hashMap.put("key1", nestedObject);
        Telemetry telemetry = new Telemetry(hashMap);
        NullableValue nullableValue = telemetry.read("key1.key2");
        assertEquals("get", nullableValue.value());
        assertFalse(nullableValue.isNull());
    }

    @Test
    public void shouldGetOneNestedMapValue() {
        Map hashMap = new HashMap<String, Object>();
        HashMap<String, Object> nestedObject = new HashMap<String, Object>();
        HashMap<String, String> otherNestedObject = new HashMap<String, String>();
        otherNestedObject.put("key3", "get");
        nestedObject.put("key2", otherNestedObject);
        hashMap.put("key1", nestedObject);
        Telemetry telemetry = new Telemetry(hashMap);
        NullableValue nullableValue = telemetry.read("key1.key2");
        assertEquals(otherNestedObject, nullableValue.value());
        assertFalse(nullableValue.isNull());
    }

    @Test
    public void shouldGetTwoNestedValue() {
        Map hashMap = new HashMap<String, Object>();
        HashMap<String, Object> nestedObject = new HashMap<String, Object>();
        HashMap<String, String> otherNestedObject = new HashMap<String, String>();
        otherNestedObject.put("key3", "get");
        nestedObject.put("key2", otherNestedObject);
        hashMap.put("key1", nestedObject);
        Telemetry telemetry = new Telemetry(hashMap);
        NullableValue nullableValue = telemetry.read("key1.key2.key3");
        assertEquals("get", nullableValue.value());
        assertFalse(nullableValue.isNull());
    }

    @Test
    public void shouldGetNullValueForMissingKey() {
        Map hashMap = new HashMap<String, Object>();
        hashMap.put("key1", "get");
        Telemetry telemetry = new Telemetry(hashMap);
        NullableValue nullableValue = telemetry.read("invalidKey");
        assertEquals(null, nullableValue.value());
        assertTrue(nullableValue.isNull());
    }

    @Test
    public void shouldNotFailWhenReadingWrongNesting() {
        Map hashMap = new HashMap<String, Object>();
        hashMap.put("key1", "get");
        Telemetry telemetry = new Telemetry(hashMap);
        NullableValue nullableValue = telemetry.read("key1.invalidKey");
        assertEquals(null, nullableValue.value());
        assertTrue(nullableValue.isNull());
    }

    @Test
    public void shouldGetNullValueForBothMissingNestedKey() {
        Map hashMap = new HashMap<String, Object>();
        hashMap.put("key1", "get");
        Telemetry telemetry = new Telemetry(hashMap);
        NullableValue nullableValue = telemetry.read("invalidKey1.invalidKey2");
        assertEquals(null, nullableValue.value());
        assertTrue(nullableValue.isNull());
    }

    @Test
    public void shouldAddValue() {
        Map hashMap = new HashMap<String, Object>();
        HashMap<String, String> nestedMap = new HashMap<String, String>();
        Telemetry telemetry = new Telemetry(hashMap);
        telemetry.add("key", nestedMap);
        assertEquals(nestedMap, telemetry.getMap().get("key"));
    }

    @Test
    public void shouldOverrideValue() {
        Map hashMap = new HashMap<String, Object>();
        HashMap<String, String> nestedMap = new HashMap<String, String>();
        HashMap<String, String> overrideNestedMap = new HashMap<String, String>();
        hashMap.put("key", nestedMap);
        Telemetry telemetry = new Telemetry(hashMap);
        telemetry.add("key", overrideNestedMap);
        assertEquals(overrideNestedMap, telemetry.getMap().get("key"));
    }

    @Test
    public void shouldAddNestedValue() {
        Map hashMap = new HashMap<String, Object>();
        HashMap<String, String> nestedMap = new HashMap<String, String>();
        hashMap.put("key", nestedMap);
        HashMap<String, String> otherNestedMap = new HashMap<String, String>();
        Telemetry telemetry = new Telemetry(hashMap);
        telemetry.add("key.nested", otherNestedMap);
        assertEquals(otherNestedMap, telemetry.read("key.nested").value());
    }

    @Test
    public void shouldNotAddWhenKeysDoesNotExists() {
        Map hashMap = new HashMap<String, Object>();
        HashMap<String, String> nestedMap = new HashMap<String, String>();
        Telemetry telemetry = new Telemetry(hashMap);
        telemetry.add("invalidKey.nested", nestedMap);
        assertFalse(telemetry.getMap().containsKey("invalidKey"));
    }

    @Test
    public void shouldGetTheEts() {
        double ets = 12334534;
        Map hashMap = new HashMap<String, Object>();
        hashMap.put("ets", ets);
        HashMap<String, String> nestedMap = new HashMap<String, String>();
        Telemetry telemetry = new Telemetry(hashMap);
        try {
            assertEquals(12334534, telemetry.getEts());
        } catch (TelemetryReaderException e) {

        }

        assertFalse(telemetry.getMap().containsKey("invalidKey"));
    }

    @Test
    public void shouldGetTheTimeStamp() {
        Telemetry telemetry = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertNotNull(telemetry.getAtTimestamp());
    }

    @Test
    public void shouldGetTheSyncTS() {
        Telemetry telemetry = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertNotNull(telemetry.getSyncts());
    }

    @Test
    public void shouldGetHashCodeValue() {
        Telemetry telemetry = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertNotNull(telemetry.hashCode());
    }

    @Test
    public void shouldGetStringObject() {
        Telemetry telemetry = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertNotNull(telemetry.toString());
    }

    @Test
    public void shouldGetTheMetaDataValue() {
        Telemetry telemetry = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertNotNull(telemetry.id());
    }


    @Test
    public void CheckObjectsAreEqualOrNot() {
        Telemetry telemetry1 = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Telemetry telemetry2 = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        String stringObj = new String();
        Assert.assertTrue(telemetry1.equals(telemetry2));
        Assert.assertFalse(telemetry1.equals(stringObj));
    }

    @Test
    public void ShouldAddFieldIfNotPresent() {
        Telemetry telemetry = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        telemetry.addFieldIfAbsent("error", "Invalid Key");
        Assert.assertEquals(telemetry.read("error").value(), "Invalid Key");
    }

    @Test
    public void ShouldReturnNullIfTheKeyIsNotPresent() {
        Telemetry telemetry = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Assert.assertNull(telemetry.read("invalidKey").value());
    }

    @Test
    public void shouldThrowError() {
        Telemetry telemetry = new Telemetry(null);

        try {
            Assert.assertNull(telemetry.mustReadValue("invalidKey"));
        } catch (Exception e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldValidateTheNullableObject() {
        Telemetry telemetry1 = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        Telemetry telemetry2 = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        NullableValue<Object> eid1 = telemetry1.read("eid");
        NullableValue<Object> eid2 = telemetry2.read("eid");
        Assert.assertTrue(eid1.equals(eid2));
        System.out.println(eid1.equals(eid2));
        Assert.assertNotNull(eid1.hashCode());
        Assert.assertNotNull(eid1.toString());
    }

    @Test
    public void shouldGetTheDefaultValue() {
        Telemetry telemetry = new Telemetry(null);
        NullableValue<Object> eid1 = telemetry.read("eid");
        Assert.assertEquals(eid1.valueOrDefault("START"), "START");
    }

    @Test
    public void shouldGetTheActualValue() {
        Telemetry telemetry = new Telemetry(EventFixture.getMap(EventFixture.IMPRESSION_EVENT));
        NullableValue<Object> eid1 = telemetry.read("eid");
        Assert.assertEquals(eid1.valueOrDefault("START"), "IMPRESSION");
    }

}