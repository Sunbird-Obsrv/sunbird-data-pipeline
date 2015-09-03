package org.ekstep.ep.kafka;

import kafka.utils.VerifiableProperties;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UIDPartitionerTest {

    private UIDPartitioner UIDPartitioner;
    static final int Partitions = 10;
    static final String UID = "ff305EW4-85b4-341b-da2f-eb6b9e5460fa0";
    static final int PARTITION = 6;
    private VerifiableProperties propsMock;

    @Before
    public void setUp() throws Exception {
        propsMock = mock(VerifiableProperties.class);
        UIDPartitioner = new UIDPartitioner(propsMock);
    }

    @Test
    public void testPartition() throws Exception {
        assertEquals(UIDPartitioner.partition(UID,Partitions),PARTITION);
    }

    @Test
     public void testPartitionWithNullKey() throws Exception {
        assertEquals(UIDPartitioner.partition(null, Partitions),0);
    }

    @Test
    public void testPartitionWithBlankKey() throws Exception {
        assertEquals(UIDPartitioner.partition("", Partitions),0);
    }

    @Test
    public void testPartitionWithIntegerKey() throws Exception {
        assertEquals(UIDPartitioner.partition(1, Partitions),6);
    }
}