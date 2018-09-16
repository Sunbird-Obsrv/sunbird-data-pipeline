package org.ekstep.ep.samza.util;

public class Path {
    public String loc(){
        return "edata.loc";
    }

    public String checksum(){
        return "metadata.checksum";
    }

    public String metadata(){
        return "metadata";
    }

    public String channel(){
        return "channel";
    }

    public String flags(){
        return "flags";
    }

    public String dimensionsDid(){
        return "dimensions.did";
    }

    public String contextDid(){
        return "context.did";
    }

    public String ets(){
        return "ets";
    }

    public String ts(){
        return "ts";
    }

    public String mid(){
        return "mid";
    }

    public String ldata(){
        return "ldata";
    }
}
