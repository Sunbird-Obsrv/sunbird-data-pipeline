package org.ekstep.dp.core

object MapUtils {

  /*
  def toJavaMap(m: Any): AnyRef = {
    import java.util
    import scala.collection.JavaConverters._
    m match {
      case sm: Map[String, Any] => sm.map(kv => (kv._1, toJavaMap(kv._2))).asJava
      case sl: Iterable[Any] => new util.ArrayList(sl.map( toJavaMap ).asJava.asInstanceOf[util.Collection[_]])
      case _ => m.asInstanceOf[AnyRef]
    }
  }
  */

}
