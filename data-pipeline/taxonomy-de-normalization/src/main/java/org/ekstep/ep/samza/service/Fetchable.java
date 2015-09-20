package org.ekstep.ep.samza.service;

import java.util.Map;

/**
 * Created by shashankteotia on 9/20/15.
 */
public interface Fetchable {
    Map<String, Object> fetch() throws java.io.IOException;
}
