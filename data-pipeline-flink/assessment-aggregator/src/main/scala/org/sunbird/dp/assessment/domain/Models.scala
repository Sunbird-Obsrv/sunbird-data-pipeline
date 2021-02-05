package org.sunbird.dp.assessment.domain

import com.datastax.driver.core.UDTValue

import java.util

object Models {}

case class Question(id: String, maxscore: Double, params: util.List[util.HashMap[String, Any]], title: String, `type`: String, desc: String)

case class QuestionData(resvalues: util.List[util.HashMap[String, Any]], duration: Double, score: Double, item: Question)

case class AssessEvent(ets: Long, edata: QuestionData)


case class Aggregate(totalScore: Double, totalMaxScore: Double, grandTotal: String, questionsList: List[UDTValue], createdOn: Option[Long], lastAttemptedOn: Option[Long])

case class AssessmentAgg(userId: String, courseId:String, batchId:String, contentId:String, attemptId:String, aggregate: Aggregate)