package org.ekstep.ep.samza.system;

import java.util.Map;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class EventTest {
	@Test
	public void shouldReturnSchemaFileFromEid() {

		Event event = new Event(EventFixture.validGeErrorEventMap());
		Assert.assertEquals("ge_error.json", (String) event.schemaName());
	}

	@Test
	public void shouldReturnVerIfPresent() {

		Event event = new Event(EventFixture.validGeErrorEventMap());
		Assert.assertEquals("2.2", (String) event.version());
	}

	@Test
	public void shouldReturnNullForEdata() {

		Map<String, Object> event = EventFixture.invalidGeErrorEventMap();
		Assert.assertEquals(null, (Map<String, Object>) event.get("edata"));
	}

	@Test(expected = JsonSyntaxException.class)
	public void shouldThrowException() {
		new Gson().fromJson(EventFixture.UNPARSABLE_GE_GENIE_UPDATE_EVENT,
				new TypeToken<Map<String, Object>>() {
				}.getType());
	}
}
