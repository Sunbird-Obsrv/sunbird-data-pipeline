package org.ekstep.ep.es_router.util;

/**
 * Created by aks on 01/08/17.
 */
public class Constants {
  public static String METADATA_KEY="metadata";
  public static String INDEX_TYPE_KEY=String.format("%s.%s",METADATA_KEY,"index_type");
  public static String INDEX_NAME_KEY=String.format("%s.%s",METADATA_KEY,"index_name");
  public static String DEFAULT_INDEX_TYPE="events_v1";
}
