package org.ekstep.ep.samza.system;

import java.util.Date;

public class Clock implements Clockable{

    @Override
    public Date getDate(){
        return new Date();
    }

}
