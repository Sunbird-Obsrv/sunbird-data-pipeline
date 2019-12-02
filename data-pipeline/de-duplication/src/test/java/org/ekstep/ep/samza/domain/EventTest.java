package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class EventTest {

    @Test
    public void shouldMarkTheRedisFailure() {
        String RAW_EVENT = "{\"eid\":\"INTERACT\",\"ets\":1573811794043,\"ver\":\"3.0\",\"mid\":\"INTERACT:dfdb7f3e3e5854a9a4b01d20e2ade835\",\"actor\":{\"id\":\"0b96635f-fe2b-4ab0-a511-05cfce8faa3f\",\"type\":\"User\"},\"context\":{\"channel\":\"0126825293972439041\",\"pdata\":{\"id\":\"preprod.diksha.portal\",\"ver\":\"2.5.0\",\"pid\":\"sunbird-portal.contentplayer\"},\"env\":\"contentplayer\",\"sid\":\"0ITT0p3ZqwkxREhxTmCiQatUSWGisRpw\",\"did\":\"a3cf6d00e1b7af06a61300b4a50853fb\",\"cdata\":[{\"type\":\"Feature\",\"id\":\"video:resolutionChange\"},{\"type\":\"Task\",\"id\":\"SB-13358\"},{\"type\":\"Resolution\",\"id\":\"large\"},{\"type\":\"ResolutionChange\",\"id\":\"Auto\"},{\"id\":\"9d9c3e9aa3eb33090b61ca8db196f8e6\",\"type\":\"ContentSession\"}],\"rollup\":{\"l1\":\"0126825293972439041\"}},\"object\":{\"id\":\"do_312579855868370944110877\",\"type\":\"Content\",\"ver\":\"1\",\"rollup\":{}},\"tags\":[\"0126825293972439041\"],\"edata\":{\"type\":\"TOUCH\",\"subtype\":\"CHANGE\",\"id\":\"\",\"pageid\":\"videostage\"}}";
        Event event = new Event(new Gson().fromJson(RAW_EVENT, Map.class));
        event.markRedisFailure();
        Map<String, String> flagData = new Gson().fromJson(new Gson().toJson(event.getMap().get("flags")), Map.class);
        Object type = event.getMap().get("type");
        Assert.assertEquals(type, "events");
        Assert.assertNotNull(flagData);
        Assert.assertEquals(flagData.get("dd_processed"), false);
        Assert.assertEquals(flagData.get("dd_redis_failure"), true);
        Assert.assertNotNull(type);

    }

    @Test
    public void shouldMarkSkipped() {
        String RAW_EVENT = "{\"eid\":\"INTERACT\",\"ets\":1573811794043,\"ver\":\"3.0\",\"mid\":\"INTERACT:dfdb7f3e3e5854a9a4b01d20e2ade835\",\"actor\":{\"id\":\"0b96635f-fe2b-4ab0-a511-05cfce8faa3f\",\"type\":\"User\"},\"context\":{\"channel\":\"0126825293972439041\",\"pdata\":{\"id\":\"preprod.diksha.portal\",\"ver\":\"2.5.0\",\"pid\":\"sunbird-portal.contentplayer\"},\"env\":\"contentplayer\",\"sid\":\"0ITT0p3ZqwkxREhxTmCiQatUSWGisRpw\",\"did\":\"a3cf6d00e1b7af06a61300b4a50853fb\",\"cdata\":[{\"type\":\"Feature\",\"id\":\"video:resolutionChange\"},{\"type\":\"Task\",\"id\":\"SB-13358\"},{\"type\":\"Resolution\",\"id\":\"large\"},{\"type\":\"ResolutionChange\",\"id\":\"Auto\"},{\"id\":\"9d9c3e9aa3eb33090b61ca8db196f8e6\",\"type\":\"ContentSession\"}],\"rollup\":{\"l1\":\"0126825293972439041\"}},\"object\":{\"id\":\"do_312579855868370944110877\",\"type\":\"Content\",\"ver\":\"1\",\"rollup\":{}},\"tags\":[\"0126825293972439041\"],\"edata\":{\"type\":\"TOUCH\",\"subtype\":\"CHANGE\",\"id\":\"\",\"pageid\":\"videostage\"}}";
        Event event = new Event(new Gson().fromJson(RAW_EVENT, Map.class));
        event.markSkipped();
        Map<String, String> flagData = new Gson().fromJson(new Gson().toJson(event.getMap().get("flags")), Map.class);
        Assert.assertNotNull(flagData);
        Assert.assertEquals(flagData.get("dd_processed"), false);
        Assert.assertEquals(flagData.get("dd_checksum_present"), false);

    }


}
