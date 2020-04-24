package org.sunbird.dp.usercache.domain

import java.util

import org.sunbird.dp.core.domain.Events

class Event(eventMap: util.Map[String, Any]) extends Events(eventMap) {

  override def kafkaKey(): String = {
    did()
  }



}
