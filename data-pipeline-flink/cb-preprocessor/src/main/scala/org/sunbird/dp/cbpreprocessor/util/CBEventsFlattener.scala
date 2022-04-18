package org.sunbird.dp.cbpreprocessor.util

import org.sunbird.dp.cbpreprocessor.domain.Event

import java.util
import scala.collection.mutable.ListBuffer

// import scala.util.Try

/**
 * util for flattening cb_audit work order events
 */
class CBEventsFlattener extends java.io.Serializable {

  private val serialVersionUID = 1167435095740381669L  // TODO: update?

  def emptyArrayList(): util.ArrayList[util.Map[String, Any]] = { new util.ArrayList[util.Map[String, Any]]() }
  def emptyMap(): util.HashMap[String, Any] = { new util.HashMap[String, Any]() }

  /**
   * merge arbitrary number of maps and return a new map
   * `mergedMap(List((map, "", exclusions)))` returns a shallow copy of `map` excluding `exclusions`
   *
   * @param mergeParamList List[(map, prefix, exclusions)]
   * @return
   */
  def mergedMap(mergeParamList: List[(util.Map[String, Any], String, Set[String])]): util.Map[String, Any] = {
    val newMap = new util.LinkedHashMap[String, Any]()
    mergeParamList.foreach{
      case (map, prefix, exclusions) => {
        // println(s"mergedMap() prefix=${prefix} exclusions=${exclusions}")
        map.keySet().forEach(key => {
          if (!exclusions.contains(key)) newMap.put(prefix + key, map.get(key))
        })
      }
    }
    newMap
  }

  def getOrNone(map: util.Map[String, Any], key: String): Option[Any] = {
    if (map == null || !map.containsKey(key)) return None
    Option(map.get(key))
  }

  def getArrayListOrEmpty(map: util.Map[String, Any], key: String): util.ArrayList[util.Map[String, Any]] = {
    getOrNone(map, key) match {
      case Some(value) => value.asInstanceOf[util.ArrayList[util.Map[String, Any]]]
      case None => emptyArrayList()
    }
  }

  def getMapOrEmpty(map: util.Map[String, Any], key: String): util.Map[String, Any] = {
    getOrNone(map, key) match {
      case Some(value) => value.asInstanceOf[util.Map[String, Any]]
      case None => emptyMap()
    }
  }

  /**
   * return a list of flattened (till officer) (denormalized) maps from workOrderMap
   *
   * @param workOrderMap
   * @return List(work_order_officer_map)
   */
  def flattenWorkOrderOfficerData(workOrderMap: util.Map[String, Any]): List[util.Map[String, Any]] = {
    val flattenedList = ListBuffer[util.Map[String, Any]]()
    // TO-MERGE: workOrderMap, prefix=None, exclude=['users']
    val users = getArrayListOrEmpty(workOrderMap, "users")

    users.forEach(user => {
      // TO-MERGE: user, prefix='wa_', exclude=['roleCompetencyList', 'unmappedActivities', 'unmappedCompetencies']
      val newMap = mergedMap(List(
        // Map            Prefix             Exclusion
        (workOrderMap,    "",                Set("users")),
        (user,            "wa_",             Set("roleCompetencyList", "unmappedActivities", "unmappedCompetencies")),
      ))
      flattenedList.append(newMap)
    })
    flattenedList.toList
  }

  /**
   * return a list of flattened (denormalized) maps from workOrderMap
   *
   * @param workOrderMap
   * @return List((work_order_row_map, child_type=activity|competency, has_role=true|false))
   */
  def flattenWorkOrderEventData(workOrderMap: util.Map[String, Any]): List[(util.Map[String, Any], String, Boolean)] = {
    val flattenedList = ListBuffer[(util.Map[String, Any], String, Boolean)]()
    // TO-MERGE: workOrderMap, prefix=None, exclude=['users']

    val users = getArrayListOrEmpty(workOrderMap, "users")
    // if users is empty, TODO: handle this for all empty lists
//    if (users.isEmpty) {
//      val newMap = mergedMap(List(MapMergeParams(workOrderMap, "", Set("users"))))
//      flattenedList.append((newMap, "", false))
//      return flattenedList.toList
//    }

    users.forEach(user => {
      // TO-MERGE: user, prefix='wa_', exclude=['roleCompetencyList', 'unmappedActivities', 'unmappedCompetencies']

      // take care of unmappedActivities
      val unmappedActivities = getArrayListOrEmpty(user, "unmappedActivities")
      unmappedActivities.forEach(activity => {
        // TO-MERGE: activity, prefix='wa_activity_', exclude=[]
        val newMap = mergedMap(List(
          // Map            Prefix            Exclusion
          (workOrderMap,    "",               Set("users")),
          (user,            "wa_",            Set("roleCompetencyList", "unmappedActivities", "unmappedCompetencies")),
          (activity,        "wa_activity_",   Set())
        ))
        flattenedList.append((newMap, "activity", false))
      })

      // take care of unmappedCompetencies
      val unmappedCompetencies = getArrayListOrEmpty(user, "unmappedCompetencies")
      unmappedCompetencies.forEach(competency => {
        // TO-MERGE: competency, prefix='wa_competency_', exclude=[]
        val newMap = mergedMap(List(
          // Map          Prefix              Exclusion
          (workOrderMap,  "",                 Set("users")),
          (user,          "wa_",              Set("roleCompetencyList", "unmappedActivities", "unmappedCompetencies")),
          (competency,    "wa_competency_",   Set())
        ))
        flattenedList.append((newMap, "competency", false))
      })

      // dive into roleCompetencyList
      val roleCompetencyList = getArrayListOrEmpty(user, "roleCompetencyList")
      roleCompetencyList.forEach(roleCompetency => {
        // TO-MERGE: roleCompetency, prefix='wa_rcl_', exclude=['roleDetails','competencyDetails']
        // exclude=['roleDetails','competencyDetails'] results in no fields from this level being merged
        val roleDetails = getMapOrEmpty(roleCompetency, "roleDetails")
        // TO-MERGE: roleDetails, prefix='wa_role_', exclude=['childNodes']

        // activities associated with this role
        val roleChildNodes = getArrayListOrEmpty(roleDetails, "childNodes")
        roleChildNodes.forEach(childNode => {
          // TO-MERGE: childNode, prefix='wa_activity_', exclude=[]
          val newMap = mergedMap(List(
            // Map            Prefix            Exclusion
            (workOrderMap,    "",               Set("users")),
            (user,            "wa_",            Set("roleCompetencyList", "unmappedActivities", "unmappedCompetencies")),
            (roleCompetency,  "wa_rcl_",        Set("roleDetails", "competencyDetails")),
            (roleDetails,     "wa_role_",       Set("childNodes")),
            (childNode,       "wa_activity_",   Set())
          ))
          flattenedList.append((newMap, "activity", true))
        })

        // competencies associated with this role
        val competencyDetails = getArrayListOrEmpty(roleCompetency, "competencyDetails")
        competencyDetails.forEach(competency => {
          // TO-MERGE: competency, prefix='wa_competency_', exclude=[]
          val newMap = mergedMap(List(
            // Map            Prefix             Exclusion
            (workOrderMap,    "",                Set("users")),
            (user,            "wa_",             Set("roleCompetencyList", "unmappedActivities", "unmappedCompetencies")),
            (roleCompetency,  "wa_rcl_",         Set("roleDetails", "competencyDetails")),
            (roleDetails,     "wa_role_",        Set("childNodes")),
            (competency,      "wa_competency_",  Set())
          ))
          flattenedList.append((newMap, "competency", true))
        })
      })
    })

    flattenedList.toList
  }

  /**
   * generate a new Event with parent event map, and given eid and cbData
   *
   * @param eid
   * @param parentEventMap
   * @param cbData
   * @return
   */
  def getNewCbEvent(eid: String, parentEventMap: util.Map[String, Any], cbData: util.Map[String, Any]): Event = {
    val eventMapEData = parentEventMap.get("edata").asInstanceOf[util.Map[String, Any]]
    // create a shallow copy of eventMap excluding edata
    val newEventMap = mergedMap(List((parentEventMap, "", Set("edata"))))
    // store parent mid as parent_mid
    newEventMap.put("mid_parent", newEventMap.get("mid"))
    // generate new mid
    newEventMap.put("mid", s"${eid}:${util.UUID.randomUUID().toString}")
    // update eid
    newEventMap.put("eid", eid)
    // TODO: update ets and timestamp
    // create a shallow copy of eventMapEData excluding cb_data
    val newEventMapEData = mergedMap(List((eventMapEData, "", Set("cb_data"))))
    // update new edata, put flatWorkOrderMap as cb_data
    newEventMapEData.put("cb_data", cbData)
    // update new event map, put newEventMapEData as edata
    newEventMap.put("edata", newEventMapEData)
    new Event(newEventMap)
  }

  /**
   * flatten work order data in `event` and return a Seq of CB_ITEM events
   *
   * @param event
   * @return
   */
  def flattenedEvents(event: Event): Seq[(Event, String, Boolean)] = {
    val workOrderMap = event.cbData.get("data").asInstanceOf[util.Map[String, Any]]
    val eventMap = event.getMap()
    flattenWorkOrderEventData(workOrderMap).map {
      case (flatWorkOrderMap, childType, hasRole) => {
        (getNewCbEvent("CB_ITEM", eventMap, flatWorkOrderMap), childType, hasRole)
      }
    }
  }

  /**
   * flatten work order officer data in `event` and return a Seq of CB_ITEM_USER events
   *
   * @param event
   * @return
   */
  def flattenedOfficerEvents(event: Event): Seq[Event] = {
    val workOrderMap = event.cbData.get("data").asInstanceOf[util.Map[String, Any]]
    val eventMap = event.getMap()
    flattenWorkOrderOfficerData(workOrderMap).map { data => getNewCbEvent("CB_ITEM_USER", eventMap, data) }
  }

}
