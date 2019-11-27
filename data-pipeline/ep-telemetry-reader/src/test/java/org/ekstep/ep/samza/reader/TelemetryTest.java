package org.ekstep.ep.samza.reader;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TelemetryTest {
  @Test
  public void shouldGetFirstStringValue() {
    Map hashMap = new HashMap<String,String>();
    hashMap.put("key1","value1");
    Telemetry telemetry = new Telemetry(hashMap);
    NullableValue nullableValue = telemetry.read("key1");
    assertEquals("value1", nullableValue.value());
    assertFalse(nullableValue.isNull());
  }

  @Test
  public void shouldGetFirstMapValue() {
    Map hashMap = new HashMap<String,Object>();
    HashMap<String, String> nestedObject = new HashMap<String, String>();
    nestedObject.put("key2","get");
    hashMap.put("key1", nestedObject);
    Telemetry telemetry = new Telemetry(hashMap);
    NullableValue nullableValue = telemetry.read("key1");
    assertEquals(nestedObject, nullableValue.value());
    assertFalse(nullableValue.isNull());
  }

  @Test
  public void shouldGetOneNestedStringValue() {
    Map hashMap = new HashMap<String,Object>();
    HashMap<String, String> nestedObject = new HashMap<String, String>();
    nestedObject.put("key2","get");
    hashMap.put("key1", nestedObject);
    Telemetry telemetry = new Telemetry(hashMap);
    NullableValue nullableValue = telemetry.read("key1.key2");
    assertEquals("get", nullableValue.value());
    assertFalse(nullableValue.isNull());
  }

  @Test
  public void shouldGetOneNestedMapValue() {
    Map hashMap = new HashMap<String,Object>();
    HashMap<String, Object> nestedObject = new HashMap<String, Object>();
    HashMap<String, String> otherNestedObject = new HashMap<String, String>();
    otherNestedObject.put("key3","get");
    nestedObject.put("key2",otherNestedObject);
    hashMap.put("key1", nestedObject);
    Telemetry telemetry = new Telemetry(hashMap);
    NullableValue nullableValue = telemetry.read("key1.key2");
    assertEquals(otherNestedObject, nullableValue.value());
    assertFalse(nullableValue.isNull());
  }

  @Test
  public void shouldGetTwoNestedValue() {
    Map hashMap = new HashMap<String,Object>();
    HashMap<String, Object> nestedObject = new HashMap<String, Object>();
    HashMap<String, String> otherNestedObject = new HashMap<String, String>();
    otherNestedObject.put("key3","get");
    nestedObject.put("key2",otherNestedObject);
    hashMap.put("key1", nestedObject);
    Telemetry telemetry = new Telemetry(hashMap);
    NullableValue nullableValue = telemetry.read("key1.key2.key3");
    assertEquals("get", nullableValue.value());
    assertFalse(nullableValue.isNull());
  }

  @Test
  public void shouldGetNullValueForMissingKey() {
    Map hashMap = new HashMap<String,Object>();
    hashMap.put("key1","get");
    Telemetry telemetry = new Telemetry(hashMap);
    NullableValue nullableValue = telemetry.read("invalidKey");
    assertEquals(null, nullableValue.value());
    assertTrue(nullableValue.isNull());
  }

  @Test
  public void shouldNotFailWhenReadingWrongNesting() {
    Map hashMap = new HashMap<String,Object>();
    hashMap.put("key1","get");
    Telemetry telemetry = new Telemetry(hashMap);
    NullableValue nullableValue = telemetry.read("key1.invalidKey");
    assertEquals(null, nullableValue.value());
    assertTrue(nullableValue.isNull());
  }

  @Test
  public void shouldGetNullValueForBothMissingNestedKey() {
    Map hashMap = new HashMap<String,Object>();
    hashMap.put("key1","get");
    Telemetry telemetry = new Telemetry(hashMap);
    NullableValue nullableValue = telemetry.read("invalidKey1.invalidKey2");
    assertEquals(null, nullableValue.value());
    assertTrue(nullableValue.isNull());
  }

  @Test
  public void shouldAddValue() {
    Map hashMap = new HashMap<String,Object>();
    HashMap<String, String> nestedMap = new HashMap<String, String>();
    Telemetry telemetry = new Telemetry(hashMap);
    telemetry.add("key",nestedMap);
    assertEquals(nestedMap, telemetry.getMap().get("key"));
  }

  @Test
  public void shouldOverrideValue() {
    Map hashMap = new HashMap<String,Object>();
    HashMap<String, String> nestedMap = new HashMap<String, String>();
    HashMap<String, String> overrideNestedMap = new HashMap<String, String>();
    hashMap.put("key",nestedMap);
    Telemetry telemetry = new Telemetry(hashMap);
    telemetry.add("key",overrideNestedMap);
    assertEquals(overrideNestedMap, telemetry.getMap().get("key"));
  }

  @Test
  public void shouldAddNestedValue() {
    Map hashMap = new HashMap<String,Object>();
    HashMap<String, String> nestedMap = new HashMap<String, String>();
    hashMap.put("key",nestedMap);
    HashMap<String, String> otherNestedMap = new HashMap<String, String>();
    Telemetry telemetry = new Telemetry(hashMap);
    telemetry.add("key.nested",otherNestedMap);
    assertEquals(otherNestedMap, telemetry.read("key.nested").value());
  }

  @Test
  public void shouldNotAddWhenKeysDoesNotExists() {
    Map hashMap = new HashMap<String,Object>();
    HashMap<String, String> nestedMap = new HashMap<String, String>();
    Telemetry telemetry = new Telemetry(hashMap);
    telemetry.add("invalidKey.nested",nestedMap);
    assertFalse(telemetry.getMap().containsKey("invalidKey"));
  }


}