package org.sunbird.dp.reader

import java.text.MessageFormat


// import org.ekstep.ep.samza.core.Logger;

object NullParent {
  //private[reader] val LOGGER = new Logger(classOf[NullParent])
}

class NullParent private[reader](var parent: Any, var childKey: String) extends ParentType {
  override def readChild[T]: Option[T] = {
    //NullParent.LOGGER.warn(null, MessageFormat.format("NULL PARENT READ CHILD INVOKED FOR PARENT: {0}, CHILD KEY: {1}", parent, childKey))
    Some(null.asInstanceOf[T])
  }

  override def addChild(value: Any): Unit = {
   // NullParent.LOGGER.warn(null, MessageFormat.format("NULL PARENT ADD CHILD INVOKED FOR PARENT: {0}, CHILD KEY: {1}", parent, childKey))
  }
}
