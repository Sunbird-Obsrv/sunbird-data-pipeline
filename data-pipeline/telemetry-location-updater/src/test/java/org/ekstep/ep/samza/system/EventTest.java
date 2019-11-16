package org.ekstep.ep.samza.system;

import java.util.Map;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.task.TelemetryLocationUpdaterConfig;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.mockito.Mockito;

public class EventTest {
	private TelemetryLocationUpdaterConfig configMock;
	@Test
	public void shouldReturnMid() {

		Event event = new Event(EventFixture.getMap(EventFixture.ERROR_EVENT));
		Assert.assertEquals("43288930-e54a-230b-b56e-876gnm8712ok", event.mid());
	}

	@Test
	public void shouldReturnEid() {

		Event event = new Event(EventFixture.getMap(EventFixture.LOG_EVENT));
		Assert.assertEquals("LOG", event.eid());
	}

	@Test(expected = JsonSyntaxException.class)
	public void shouldThrowException() {
		new Gson().fromJson(EventFixture.UNPARSABLE_START_EVENT,
				new TypeToken<Map<String, Object>>() {
				}.getType());
	}

	@Test
	public void shouldMarkFailure() {
		configMock = Mockito.mock(TelemetryLocationUpdaterConfig.class);
		String RAW_EVENT = "{\"eid\":\"INTERACT\",\"ets\":1573811794043,\"ver\":\"3.0\",\"mid\":\"INTERACT:dfdb7f3e3e5854a9a4b01d20e2ade835\",\"actor\":{\"id\":\"0b96635f-fe2b-4ab0-a511-05cfce8faa3f\",\"type\":\"User\"},\"context\":{\"channel\":\"0126825293972439041\",\"pdata\":{\"id\":\"preprod.diksha.portal\",\"ver\":\"2.5.0\",\"pid\":\"sunbird-portal.contentplayer\"},\"env\":\"contentplayer\",\"sid\":\"0ITT0p3ZqwkxREhxTmCiQatUSWGisRpw\",\"did\":\"a3cf6d00e1b7af06a61300b4a50853fb\",\"cdata\":[{\"type\":\"Feature\",\"id\":\"video:resolutionChange\"},{\"type\":\"Task\",\"id\":\"SB-13358\"},{\"type\":\"Resolution\",\"id\":\"large\"},{\"type\":\"ResolutionChange\",\"id\":\"Auto\"},{\"id\":\"9d9c3e9aa3eb33090b61ca8db196f8e6\",\"type\":\"ContentSession\"}],\"rollup\":{\"l1\":\"0126825293972439041\"}},\"object\":{\"id\":\"do_312579855868370944110877\",\"type\":\"Content\",\"ver\":\"1\",\"rollup\":{}},\"tags\":[\"0126825293972439041\"],\"edata\":{\"type\":\"TOUCH\",\"subtype\":\"CHANGE\",\"id\":\"\",\"pageid\":\"videostage\"}}";
		Event event = new Event(new Gson().fromJson(RAW_EVENT, Map.class));
		event.markFailure("Invalid Event", configMock);
		Map<String, String> flagData = new Gson().fromJson(new Gson().toJson(event.getMap().get("flags")), Map.class);
		Assert.assertNotNull(flagData);
		Assert.assertEquals(flagData.get("tr_processed"), false);
		Object metaData = event.getMap().get("metadata");
		Assert.assertNotNull(metaData);
	}
}
