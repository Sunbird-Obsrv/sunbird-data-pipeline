package org.ekstep.ep.samza.model;

import java.util.HashMap;
import java.util.Map;

public class Event {
   private  final Map<String, Object> map;

   public Event(Map<String, Object> map) {
      this.map = map;
   }

   public Child getChild() {
      String uid = (String) map.get("uid");
      Map<String, Object> udata = (Map<String, Object>) map.get("udata");
      Map<String, Boolean> flags = (Map<String, Boolean>) map.get("flags");
      Child child = new Child(uid, flags.get("child_data_processed"), Long.parseLong((String)map.get("ts")));
      child.populate(udata);
      return child;

   }

   public void update(Child child) {
      map.put("udata", child.getData());
      Map<String, Boolean> flags = (Map<String, Boolean>) map.get("flags");
      flags.put("child_data_processed", child.isProcessed());
      map.put("flags", flags);
   }

   public Map<String, Object> getData() {
      return map;
   }
}
