package org.ekstep.ep.samza.model;

import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by sreeharikm on 9/29/15.
 */
public interface IModel {

    void process(Event event) throws SQLException, ParseException;

    boolean canProcessEvent(String eid);

    void saveData() throws SQLException, ParseException;

    void setIsInserted();

    boolean getIsInserted();
}