package com.library.checksum.system;


import com.library.checksum.fixtures.EventFixture;
import com.library.checksum.system.ChecksumGenerator;
import com.library.checksum.system.KeysToAccept;
import com.library.checksum.system.KeysToReject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ChecksumGeneratorTest {
    @Test
    public void shouldCheckKeysToRejectStrategy(){

        Map<String,Object> event = (Map<String,Object>) EventFixture.event();

        String[] keys_to_reject = {"eid","@timestamp","pdata","gdata"};
        ChecksumGenerator checksumGenerator = new ChecksumGenerator(new KeysToReject());
        String checksum = checksumGenerator.generateChecksum(event, keys_to_reject);

        Assert.assertNotNull(checksum);
    }

    @Test
    public void shouldCheckKeysToAcceptStrategy(){
        Map<String,Object> event = (Map<String,Object>) EventFixture.event();

        String[] keys_to_accept = {"uid","cid","ts"};
        ChecksumGenerator checksumGenerator = new ChecksumGenerator(new KeysToAccept());
        String checksum = checksumGenerator.generateChecksum(event, keys_to_accept);

        Assert.assertNotNull(checksum);
    }

}
