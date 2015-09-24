package com.library.checksum.system;


import com.library.checksum.fixtures.Event;
import com.library.checksum.fixtures.EventFixture;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ChecksumGeneratorTest {
    @Test
    public void shouldCheckKeysToRejectStrategy(){

        Event event = new Event(EventFixture.event());

        String[] keys_to_reject = {"eid","@timestamp","pdata","gdata"};
        ChecksumGenerator checksumGenerator = new ChecksumGenerator(new KeysToReject(keys_to_reject));
        checksumGenerator.stampChecksum(event);

        Assert.assertEquals(true, event.getMap().containsKey("metadata"));
        Assert.assertNotNull(event.getMap().containsKey("metadata"));
    }

    @Test
    public void shouldCheckKeysToAcceptStrategy(){
        Event event = new Event(EventFixture.event());

        String[] keys_to_accept = {"uid","cid","ts"};
        ChecksumGenerator checksumGenerator = new ChecksumGenerator(new KeysToAccept(keys_to_accept));
        checksumGenerator.stampChecksum(event);

        Assert.assertEquals(true, event.getMap().containsKey("metadata"));
        Assert.assertNotNull(event.getMap().containsKey("metadata"));
    }
}
