package org.sunbird.dp.reader

trait ParentType {
  def readChild[T]: T

  def addChild(value: Any): Unit
}
