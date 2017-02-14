package org.ekstep.ep.samza.model;

import java.sql.SQLException;
import java.text.ParseException;

public interface IModel {

    void process(Event event) throws SQLException, ParseException;

    void setInserted();

    void setDefault();

    boolean getIsInserted();
}