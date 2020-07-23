package org.sunbird.dp.core.reader


import java.util

class ParentMap private[reader](var map: util.Map[String, Any], var childKey: String) extends ParentType {
  override def readChild[T]: Option[T] = {
    if (map != null && map.containsKey(childKey) && map.get(childKey) != null) {
      val child = map.get(childKey)
      return Some(child.asInstanceOf[T])
    }
    None
  }

  override def addChild(value: Any): Unit = {
    if (map != null) map.put(childKey, value)
  }
}
