package org.sunbird.dp.reader

//import org.sunbird.dp.util.Logger

import java.util
import java.util.Map
import java.util.logging.Logger


// import org.ekstep.ep.samza.core.Logger;

object ParentMap {
  //private[reader] val LOGGER = new Logger(classOf[ParentMap])
}

class ParentMap private[reader](var map: util.Map[String, Any], var childKey: String) extends ParentType {
  override def readChild[T >: Null]: T = {
    if (map != null && map.containsKey(childKey) && map.get(childKey) != null) {
      val child = map.get(childKey)
      return child.asInstanceOf[T]
    }
    null
  }

  override def addChild(value: Any): Unit = {
    if (map != null) map.put(childKey, value)
  }
}
