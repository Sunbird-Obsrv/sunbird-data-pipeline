package org.ekstep.dp.core.model

import java.io.Serializable
import java.util.Date

sealed trait EventModel extends Serializable {}

class GData(val id: String, val ver: String) extends Serializable {}

class Eks(dspec: Map[String, AnyRef], loc: String, pass: String, qid: String, score: Int, res: Array[String], length: AnyRef,
          atmpts: Int, failedatmpts: Int, category: String, current: String, max: String, `type`: String, extype: String,
          id: String, gid: String, itype: String, stageid: String, stageto: String, resvalues: Array[Map[String, AnyRef]],
          params: Array[Map[String, AnyRef]], uri: String, state: String, subtype: String, pos: Array[Map[String, AnyRef]],
          values: Array[AnyRef], tid: String, direction: String, datatype: String, count: AnyRef, contents: Array[Map[String, AnyRef]],
          comments: String, rating: Double, qtitle: String, qdesc: String, mmc: Array[String], context: Map[String, AnyRef],
          method: String, request: AnyRef) extends Serializable {}

class Ext(stageId: String, `type`: String) extends Serializable {}

class EData(eks: Eks, ext: Ext) extends Serializable {}

class EventMetadata(sync_timestamp: String, public: String) extends Serializable {}

class Event(eid: String, ts: String, ets: Long, `@timestamp`: String, ver: String, gdata: GData, sid: String,
            uid: String, did: String, channel: Option[String], pdata: Option[PData], edata: EData, etags: Option[ETags],
            tags: AnyRef = null, cdata: List[CData] = List(), metadata: EventMetadata = null)

// Computed Event Model
case class CData(id: String, `type`: Option[String])
case class DerivedEvent(eid: String, ets: Long, syncts: Long, ver: String, mid: String, uid: String, channel: String, content_id: Option[String] = None, cdata: Option[CData], context: Context, dimensions: Dimensions, edata: MEEdata, etags: Option[ETags] = Option(ETags(None, None, None)), tags: Option[List[AnyRef]] = None, `object`: Option[V3Object] = None)
case class MeasuredEvent(eid: String, ets: Long, syncts: Long, ver: String, mid: String, uid: String, channel: String, content_id: Option[String] = None, cdata: Option[CData], context: Context, dimensions: Dimensions, edata: MEEdata, etags: Option[ETags] = None, tags: Option[List[AnyRef]] = None, `object`: Option[V3Object] = None)
case class Dimensions(uid: Option[String], did: Option[String], gdata: Option[GData], cdata: Option[CData], domain: Option[String],
                      user: Option[UserProfile], pdata: Option[PData], loc: Option[String] = None, group_user: Option[Boolean] = None,
                      anonymous_user: Option[Boolean] = None, tag: Option[String] = None, period: Option[Int] = None,
                      content_id: Option[String] = None, ss_mid: Option[String] = None, item_id: Option[String] = None, sid: Option[String] = None,
                      stage_id: Option[String] = None, funnel: Option[String] = None, dspec: Option[Map[String, AnyRef]] = None,
                      onboarding: Option[Boolean] = None, genieVer: Option[String] = None, author_id: Option[String] = None,
                      partner_id: Option[String] = None, concept_id: Option[String] = None, client: Option[Map[String, AnyRef]] = None,
                      textbook_id: Option[String] = None, channel: Option[String] = None, `type`: Option[String] = None,
                      mode: Option[String] = None, content_type: Option[String] = None, dial_code: Option[String] = None)
case class PData(id: String, ver: String, model: Option[String] = None, pid: Option[String] = None)
case class DtRange(from: Long, to: Long)
case class Context(pdata: PData, dspec: Option[Map[String, String]] = None, granularity: String, date_range: DtRange, status: Option[String] = None, client_id: Option[String] = None, attempt: Option[Int] = None, rollup: Option[RollUp] = None, cdata: Option[List[V3CData]] = None)
case class MEEdata(eks: AnyRef)
case class ETags(app: Option[List[String]] = None, partner: Option[List[String]] = None, dims: Option[List[String]] = None)

// User profile event models

class ProfileEks(ueksid: String, utype: String, loc: String, err: String, attrs: Array[AnyRef], uid: String, age: Int,
                 day: Int, month: Int, gender: String, language: String, standard: Int, is_group_user: Boolean,
                 dspec: Map[String, AnyRef]) extends Serializable {}
class ProfileData(eks: ProfileEks, ext: Ext) extends Serializable {}
class ProfileEvent(eid: String, ts: String, `@timestamp`: String, ver: String, gdata: GData, sid: String, uid: String,
                   did: String, pdata: Option[PData] = None, channel: Option[String] = None, edata: ProfileData)

// User Model
case class UserProfile(uid: String, gender: String, age: Int)

// Search Items
case class SearchFilter(property: String, operator: String, value: Option[AnyRef])
case class Metadata(filters: Array[SearchFilter])
case class Request(metadata: Metadata, resultSize: Int)
case class Search(request: Request)

// Item Models
case class MicroConcept(id: String, metadata: Map[String, AnyRef])
case class Item(id: String, metadata: Map[String, AnyRef], tags: Option[Array[String]], mc: Option[Array[String]], mmc: Option[Array[String]])
case class ItemSet(id: String, metadata: Map[String, AnyRef], items: Array[Item], tags: Option[Array[String]], count: Int)
case class Questionnaire(id: String, metadata: Map[String, AnyRef], itemSets: Array[ItemSet], items: Array[Item], tags: Option[Array[String]])
case class ItemConcept(concepts: Array[String], maxScore: Int)

// Content Models
case class Content(id: String, metadata: Map[String, AnyRef], tags: Option[Array[String]], concepts: Array[String])
case class Game(identifier: String, code: String, subject: String, objectType: String)


// telemetry v3 case classes
case class Actor(id: String, `type`: String)
case class V3PData(id: String, ver: Option[String] = None, pid: Option[String] = None, model: Option[String] = None)
case class Question(id: String, maxscore: Int, exlength: Int, params: Array[Map[String, AnyRef]], uri: String, desc: String, title: String, mmc: Array[String], mc: Array[String])
case class V3CData(id: String, `type`: String)
case class RollUp(l1: String, l2: String, l3: String, l4: String)
case class V3Context(channel: Option[String] = None, pdata: Option[V3PData] = None, env: Option[String] = None, sid: Option[String] = None, did: Option[String] = None, cdata: Option[List[V3CData]] = None, rollup: Option[RollUp] = None)
case class Visit(objid: String, objtype: String, objver: Option[String], section: Option[String], index: Option[Int])
case class V3Object(id: Option[String], `type`: Option[String], ver: Option[String], rollup: Option[RollUp], subtype: Option[String] = None, parent: Option[CommonObject] = None)
case class CommonObject(id: Option[String], `type`: Option[String], ver: Option[String] = None)
case class ShareItems(id: Option[String], `type`: Option[String], ver: Option[String], params: Option[List[Map[String, AnyRef]]],
                      origin: Option[CommonObject], to: Option[CommonObject])

case class V3EData(`type`: Option[String], dspec: Option[Map[String, AnyRef]], uaspec: Option[Map[String, String]], loc: Option[String],
                   mode: Option[String], duration: Option[Long], pageid: Option[String], subtype: Option[String], uri: Option[String],
                   visits: Option[List[Visit]], id: Option[String], target: Option[Map[String, AnyRef]], plugin: Option[Map[String, AnyRef]],
                   extra: Option[Map[String, AnyRef]], item: Option[Question], pass: Option[String], score: Option[Int],
                   resvalues: Option[Array[Map[String, AnyRef]]], values: Option[AnyRef], rating: Option[Double], comments: Option[String],
                   dir: Option[String], items: Option[List[ShareItems]], props : Option[List[String]], state: Option[String],
                   prevstate: Option[String], err: Option[AnyRef], errtype: Option[String], stacktrace: Option[String],
                   `object`: Option[Map[String, AnyRef]], level: Option[String], message: Option[String], params: Option[List[Map[String, AnyRef]]],
                   summary: Option[List[Map[String, AnyRef]]], index: Option[Int], `class`: Option[String], status: Option[String],
                   query: Option[String], data: Option[AnyRef], sort: Option[AnyRef], correlationid: Option[String], topn: Option[List[AnyRef]],
                   filters: Option[AnyRef] = None, size: Option[Int] = None)

case class V3Event(eid: String, ets: Long, `@timestamp`: Option[String] = None, ver: String, mid: String, actor: Option[Actor], context: V3Context,
                   `object`: Option[V3Object] = None, edata: Option[V3EData], tags: Option[List[AnyRef]] = None,
                   flags: Option[V3FlagContent] = None, metadata: Option[V3Metadata] = None) extends EventModel {
  private val serialVersionUID = - 569929397201282845L
}

case class V3DerivedEvent(eid: String, ets: Long, `@timestamp`: String, ver: String, mid: String, actor: Actor, context: V3Context,
                          `object`: Option[V3Object], edata: AnyRef, tags: List[AnyRef] = null) extends EventModel

case class V3MetricEdata(metric: String, value: AnyRef, range: Option[AnyRef] = None)

case class V3Metadata(tv_error: Option[String], src: Option[String])

case class V3FlagContent(derived_location_retrieved: Option[Boolean] = None, device_data_retrieved: Option[Boolean] = None, user_data_retrieved: Option[Boolean] = None,
                         dialcode_data_retrieved: Option[Boolean] = None, content_data_retrieved: Option[Boolean] = None, collection_data_retrieved: Option[Boolean] = None,
                         extractor_duplicate: Option[Boolean] = None, tv_processed: Option[Boolean] = None, tv_skipped: Option[Boolean] = None, dd_processed: Option[Boolean] = None,
                         dd_duplicate_event: Option[Boolean] = None, dd_redis_failure: Option[Boolean] = None)

// Experiment Models
case class DeviceProfileModel(device_id: Option[String], state: Option[String], city: Option[String], first_access: Option[Date])

case class GraphUpdateEvent(ets: Long, nodeUniqueId: String, transactionData: Map[String, Map[String, Map[String, Any]]], objectType: String, operationType: String = "UPDATE", nodeType: String = "DATA_NODE", graphId: String = "domain", nodeGraphId: Int = 0)

// WFS Models
case class ItemResponse(itemId: String, itype: Option[AnyRef], ilevel: Option[AnyRef], timeSpent: Option[Double], exTimeSpent: Option[AnyRef], res: Option[Array[String]], resValues: Option[Array[AnyRef]], exRes: Option[AnyRef], incRes: Option[AnyRef], mc: Option[AnyRef], mmc: Option[AnyRef], score: Int, time_stamp: Long, maxScore: Option[AnyRef], domain: Option[AnyRef], pass: String, qtitle: Option[String], qdesc: Option[String])
case class EventSummary(id: String, count: Int)
case class EnvSummary(env: String, time_spent: Double, count: Long)
case class PageSummary(id: String, `type`: String, env: String, time_spent: Double, visit_count: Long)
