package org.sunbird.dp.reader


import java.util.{List, Map}

import sun.reflect.generics.reflectiveObjects.NotImplementedException

import scala.collection.JavaConversions._



class ParentListOfMap(var list: List[Map[String, Any]], var childKey: String)
  extends ParentType {

  override def readChild[T]: Option[T] = {
    if (list == null) {
      null
    }
    for (itemsObject <- list) {
      if (!(itemsObject.isInstanceOf[Map[_, _]])) {}
      val items: Map[String, Any] = itemsObject.asInstanceOf[Map[String, Any]]
      if (items.containsKey(childKey)) {
        val o: AnyRef = items.get(childKey).asInstanceOf[AnyRef]
        if (o.isInstanceOf[List[_]] && o.asInstanceOf[List[_]].size > 0) {
          Some(o.asInstanceOf[List[_]].get(0).asInstanceOf[T])
        }
      }
    }
    None
  }

  override def addChild(value: Any): Unit = {
    throw new NotImplementedException()
  }
}

