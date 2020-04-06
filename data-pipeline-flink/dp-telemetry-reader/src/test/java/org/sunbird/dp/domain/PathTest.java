package org.sunbird.dp.domain;

import org.sunbird.dp.domain.Path;
import org.junit.Assert;
import org.junit.Test;

public class PathTest {

    @Test
    public void shouldGetMetadataName() {
        Assert.assertEquals("metadata", Path.METADATA);
    }
    @Test
    public void shouldGetFlagsName() {
        Assert.assertEquals("flags", Path.FLAGS);
    }
    @Test
    public void shouldGetDimensionDid() {
        Assert.assertEquals("dimensions.did", Path.DIMENSIONS_DID);
    }
    @Test
    public void shouldGetContextDid() {
        Assert.assertEquals("context.did", Path.CONTEXT_DID);
    }
    @Test
    public void shouldGetTheEts() {
        Assert.assertEquals("ets", Path.ETS);
    }
    @Test
    public void shouldGetCheckSumKey() {
        Assert.assertEquals("metadata.checksum", Path.CHECKSUM);
    }
    @Test
    public void shouldGetTimeStampKey() {
        Assert.assertEquals("ts", Path.TS);
    }
    @Test
    public void shouldGetMidKey() {
        Assert.assertEquals("mid", Path.MID);
    }
    @Test
    public void shouldGetEidKey() {
        Assert.assertEquals("eid", Path.EID);
    }
    @Test
    public void shouldGetDeviceDataKey() {
        Assert.assertEquals("devicedata", Path.DEVICE_DATA);
    }
    @Test
    public void shouldGetLocationKey() {
        Assert.assertEquals("edata.loc", Path.LOC);
    }
    @Test
    public void shouldGetUserDataKey() {
        Assert.assertEquals("userdata", Path.USER_DATA);
    }
    @Test
    public void shouldGetContentDataKey() {
        Assert.assertEquals("contentdata", Path.CONTENT_DATA);
    }
    @Test
    public void shouldGetDialCodeDataKey() {
        Assert.assertEquals("dialcodedata", Path.DIALCODE_DATA);
    }
    @Test
    public void shouldGetCollectionDataKey() {
        Assert.assertEquals("collectiondata", Path.COLLECTION_DATA);
    }
    @Test
    public void shouldGetDerivedLocationDataKey() {
        Assert.assertEquals("derivedlocationdata", Path.DERIVED_LOC_DATA);
    }
    @Test
    public void shouldGetStateKey() {
        Assert.assertEquals("state", Path.STATE_KEY);
    }
    @Test
    public void shouldGetDistrictKey() {
        Assert.assertEquals("district", Path.DISTRICT_KEY);
    }
    @Test
    public void shouldGetLocationDerivedFromKey() {
        Assert.assertEquals("from", Path.DERIVED_LOC_FROM);
    }
    @Test
    public void shouldGetEdataKey() {
        Assert.assertEquals("edata", Path.EDATA);
    }
    @Test
    public void shouldGetChannelKey() {
        Assert.assertEquals("channel", Path.CHANNEL);
    }
    @Test
    public void shouldGetVersionKey() {
        Assert.assertEquals("ver", Path.VER);
    }

}
