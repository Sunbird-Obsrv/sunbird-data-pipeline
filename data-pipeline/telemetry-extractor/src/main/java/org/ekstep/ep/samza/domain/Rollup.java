package org.ekstep.ep.samza.domain;

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

    public boolean indexExists(final String[] list, final int index) {
        return index >= 0 && index < list.length;
    }
}
