package org.ekstep.ep.samza.converter.domain;

import java.util.List;

public class Rollup {
    private String l1 = "";
    private String l2 = "";
    private String l3 = "";
    private String l4 = "";

    public Rollup(String... values) {
        initializeValues(values);
    }

    private void initializeValues(String[] values) {
        if(indexExists(values,0))
            this.l1 = values[0];

        if(indexExists(values,1))
            this.l2 = values[1];

        if(indexExists(values,2))
            this.l3 = values[2];

        if(indexExists(values,3))
            this.l4 = values[3];
    }


    public String getL1() {
        return l1;
    }

    public void setL1(String l1) {
        this.l1 = l1;
    }

    public String getL2() {
        return l2;
    }

    public void setL2(String l2) {
        this.l2 = l2;
    }

    public String getL3() {
        return l3;
    }

    public void setL3(String l3) {
        this.l3 = l3;
    }

    public String getL4() {
        return l4;
    }

    public void setL4(String l4) {
        this.l4 = l4;
    }

    public void setLData(int index, String value) {

    }

    public boolean indexExists(final String[] list, final int index) {
        return index >= 0 && index < list.length;
    }
}
