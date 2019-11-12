package org.ekstep.ep.samza.events.domain;

public class Path {

    public String metadata(){
        return "metadata";
    }

    public String flags(){ return "flags"; }

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

    public String eid() { return "eid" ; }
}
