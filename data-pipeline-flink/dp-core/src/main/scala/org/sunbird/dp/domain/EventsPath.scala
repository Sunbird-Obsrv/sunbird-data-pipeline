package org.sunbird.dp.domain

object EventsPath {

  val METADATA_PATH = "metadata"

  val FLAGS_PATH = "flags"





  val ETS_PATH = "ets"

  val TIMESTAMP_PATH = "ts"

  val MID_PATH = "mid"

  val EID_PATH = "eid"

  val DEVICE_DATA_PATH = "devicedata"


  val USERDATA_PATH = "userdata"

  val CONTENT_DATA_PATH = "contentdata"

  val DIAL_CODE_PATH = "dialcodedata"

  val COLLECTION_PATH = "collectiondata"

  val VERSION_KEY_PATH = "ver"

  val EDATA_PATH = "edata"
  val EDATA_TYPE_PATH = s"$EDATA_PATH.type"
  val EDATA_ITEM = s"$EDATA_PATH.items"
  val EDATA_LOCATION_PATH = s"$EDATA_PATH.loc"

  val CHECKSUM_PATH = "metadata.checksum"

  val CHANNEL_PATH = "channel"

  val DERIVED_LOCATION_PATH = "derivedlocationdata"

  val STATE_KEY_PATH = "state"

  val DISTRICT_KEY_PATH = "district"

  val LOCATION_DERIVED_FROM_PATH = "from"

  val TIMESTAMP = "@timestamp"

  val CONTEXT_PATH = "context"
  val CONTEXT_P_DATA_PID_PATH = s"$CONTEXT_PATH.pdata.pid"
  val CONTEXT_P_DATA_ID_PATH = s"$CONTEXT_PATH.pdata.id"
  val CONTEXT_DID_PATH = s"$CONTEXT_PATH.did"
  val DIMENSION_PATH = "dimensions"
  val DIMENSION_CHANNEL_PATH = s"$DIMENSION_PATH.channel"
  val DIMENSION_DID_PATH = s"$DIMENSION_PATH.did"
  val CONTEXT_CHANNEL_PATH = s"$CONTEXT_PATH.channel"
  val UID_PATH = "uid"
  val ACTOR_PATH = "actor"
  val ACTOR_ID_PATH = s"$ACTOR_PATH.id"
  val ACTOR_TYPE_PATH = s"$ACTOR_PATH.type"
  val OBJECT_PATH = "object"
  val OBJECT_ID_PATH = s"$OBJECT_PATH.id"
  val OBJECT_TYPEPATH = s"$OBJECT_PATH.type"
}
