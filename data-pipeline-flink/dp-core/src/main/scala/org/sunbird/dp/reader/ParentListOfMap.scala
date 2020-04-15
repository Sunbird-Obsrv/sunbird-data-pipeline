package org.sunbird.dp.reader

//import org.sunbird.dp.util.Logger

import sun.reflect.generics.reflectiveObjects.NotImplementedException

import java.util.List

import java.util.Map

import ParentListOfMap._

//remove if not needed
import scala.collection.JavaConversions._

object ParentListOfMap {

  //TODO#: Make this class more genic.
  //var LOGGER: Logger = new Logger(classOf[ParentListOfMap])

}

class ParentListOfMap(var list: List[Map[String, Any]], var childKey: String)
  extends ParentType {

  override def readChild[T]: Option[T] = {
    if (list == null) {
      null
    }
    for (itemsObject <- list) {
      if (!(itemsObject.isInstanceOf[Map[_, _]])) {
        //continue
      }
      val items: Map[String, Any] = itemsObject.asInstanceOf[Map[String, Any]]
      if (items.containsKey(childKey)) {
        val o: AnyRef = items.get(childKey).asInstanceOf[AnyRef]
        if (o.isInstanceOf[List[_]] && o.asInstanceOf[List[_]].size > 0) {
          Some(o.asInstanceOf[List[_]].get(0).asInstanceOf[T])
        }
      }
    }
    null
  }

  override def addChild(value: Any): Unit = {
    throw new NotImplementedException()
  }

}

// import org.ekstep.ep.samza.core.Logger;
