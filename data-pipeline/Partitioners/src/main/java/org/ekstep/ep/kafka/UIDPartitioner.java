package org.ekstep.ep.kafka;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;
import org.apache.mahout.math.MurmurHash;
import org.apache.mahout.math.MurmurHash3;

import java.io.UnsupportedEncodingException;
// import ie.ucd.murmur;

@SuppressWarnings("UnusedDeclaration")
public class UIDPartitioner implements Partitioner {
    public UIDPartitioner(VerifiableProperties properties) {
    }

    public int partition(Object key, int numberOfPartitions) {
        if((key==null)||(key=="")){
            return 0;
        }
        String keyString = key.toString();
        int partition = 0;
        long intKey = 0;
        try {
            intKey = MurmurHash3.murmurhash3x8632(keyString.getBytes("UTF-8"),0,keyString.length(),1);
            intKey = intKey & 0xffffffffL;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (intKey > 0) {
            partition = (int)(intKey % numberOfPartitions);
        }
        System.out.println(partition);
        return partition;
    }
}
