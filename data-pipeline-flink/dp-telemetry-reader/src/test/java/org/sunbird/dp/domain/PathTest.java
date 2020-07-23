package org.sunbird.dp.domain;

import org.sunbird.dp.domain.Path;
import org.junit.Assert;
import org.junit.Test;

public class PathTest {
    private Path path = new Path();

    @Test
    public void shouldGetMetadataName() {
        Assert.assertEquals("metadata", path.metadata());
    }
    @Test
    public void shouldGetFlagsName() {
        Assert.assertEquals("flags", path.flags());
    }
    @Test
    public void shouldGetDimensionDid() {
        Assert.assertEquals("dimensions.did", path.dimensionsDid());
    }
    @Test
    public void shouldGetContextDid() {
        Assert.assertEquals("context.did", path.contextDid());
    }
    @Test
    public void shouldGetTheEts() {
        Assert.assertEquals("ets", path.ets());
    }
    @Test
    public void shouldGetCheckSumKey() {
        Assert.assertEquals("metadata.checksum", path.checksum());
    }
    @Test
    public void shouldGetTimeStampKey() {
        Assert.assertEquals("ts", path.ts());
    }
    @Test
    public void shouldGetMidKey() {
        Assert.assertEquals("mid", path.mid());
    }
    @Test
    public void shouldGetEidKey() {
        Assert.assertEquals("eid", path.eid());
    }
    @Test
    public void shouldGetDeviceDataKey() {
        Assert.assertEquals("devicedata", path.deviceData());
    }
    @Test
    public void shouldGetLocationKey() {
        Assert.assertEquals("edata.loc", path.loc());
    }
    @Test
    public void shouldGetUserDataKey() {
        Assert.assertEquals("userdata", path.userData());
    }
    @Test
    public void shouldGetContentDataKey() {
        Assert.assertEquals("contentdata", path.contentData());
    }
    @Test
    public void shouldGetDialCodeDataKey() {
        Assert.assertEquals("dialcodedata", path.dialCodeData());
    }
    @Test
    public void shouldGetCollectionDataKey() {
        Assert.assertEquals("collectiondata", path.collectionData());
    }
    @Test
    public void shouldGetDerivedLocationDataKey() {
        Assert.assertEquals("derivedlocationdata", path.derivedLocationData());
    }
    @Test
    public void shouldGetStateKey() {
        Assert.assertEquals("state", path.stateKey());
    }
    @Test
    public void shouldGetDistrictKey() {
        Assert.assertEquals("district", path.districtKey());
    }
    @Test
    public void shouldGetLocationDerivedFromKey() {
        Assert.assertEquals("from", path.locDerivedFromKey());
    }
    @Test
    public void shouldGetEdataKey() {
        Assert.assertEquals("edata", path.edata());
    }
    @Test
    public void shouldGetChannelKey() {
        Assert.assertEquals("channel", path.channel());
    }
    @Test
    public void shouldGetVersionKey() {
        Assert.assertEquals("ver", path.ver());
    }

}