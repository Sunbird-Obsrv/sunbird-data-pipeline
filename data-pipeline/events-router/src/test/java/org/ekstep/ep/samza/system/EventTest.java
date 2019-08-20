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
	public void shouldReturnMid() {

		Event event = new Event(EventFixture.getMap(EventFixture.ERROR_EVENT));
		Assert.assertEquals("43288930-e54a-230b-b56e-876gnm8712ok", event.mid());
	}

	@Test
	public void shouldReturnEid() {

		Event event = new Event(EventFixture.getMap(EventFixture.WORKFLOW_SUMMARY_EVENT));
		Assert.assertEquals("ME_WORKFLOW_SUMMARY", event.eid());
	}

	@Test(expected = JsonSyntaxException.class)
	public void shouldThrowException() {
		new Gson().fromJson(EventFixture.UNPARSABLE_START_EVENT,
				new TypeToken<Map<String, Object>>() {
				}.getType());
	}
}
