package org.ekstep.analytics.jobs

import java.text.SimpleDateFormat

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.lang.StringUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.{col, collect_set, concat_ws, explode_outer, first, lit, lower, _}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.ekstep.analytics.util.JSONUtils
//import org.ekstep.analytics.framework.Level._
//import org.ekstep.analytics.framework._
//import org.ekstep.analytics.framework.fetcher.DruidDataFetcher
//import org.ekstep.analytics.framework.util.DatasetUtil.extensions
//import org.ekstep.analytics.framework.util.{CommonUtil, JSONUtils, JobLogger}
//import org.joda.time.format.DateTimeFormat
//import org.sunbird.analytics.util.ESUtil


object CassandraRedisIndexer {

  private val config: Config = ConfigFactory.load

  def main(args: Array[String]): Unit = {

    //val isForAllUsers = args(0) // true/false
    val specificUserId = args(0) // userid
    val fromSpecificDate = args(1) // date in YYYY-MM-DD format

    val sunbirdKeyspace = "sunbird"
    val userTableName = "user"
    val redisKeyProperty = "id" // userid

    val dtf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSZ")
    val dtf2 = new SimpleDateFormat("yyyy-MM-dd")


    //    val conf = new SparkConf()
    //      .setAppName("CassandraToRedisIndexer")
    //      .setMaster("local[*]")
    //      // Cassandra settings
    //      .set("spark.cassandra.connection.host", "localhost")
    //      // redis settings
    //      .set("spark.redis.host", "localhost")
    //      .set("spark.redis.port", "6379")
    //      .set("spark.redis.db", "12")
    //.set("spark.redis.max.pipeline.size", config.getString("redis.max.pipeline.size"))

    //val sc = new SparkContext(conf)
    val spark: SparkSession =
      SparkSession
        .builder()
        .appName("AppName")
        .config("spark.master", "local")
        .config("spark.cassandra.connection.host", "localhost")
        .config("spark.redis.host", "localhost")
        .config("spark.redis.port", "6379")
        .config("spark.redis.db", "12")
        .getOrCreate()

    def seqOfAnyToSeqString(param: Seq[AnyRef]): Seq[(String, String)]
    = {
      param.map {
        case (x, y) => (x.toString, if (!y.isInstanceOf[String]) JSONUtils.serialize(y.asInstanceOf[AnyRef]) else y.asInstanceOf[String])
      }
    }

//    def getFilteredUserRecords(usersData: RDD[Map[String, Any]]): RDD[Map[String, Any]] = {
//      if (StringUtils.equalsIgnoreCase(isForAllUsers, "true") && StringUtils.isNotEmpty(isForAllUsers)) {
//        usersData
//      } else if (null != specificUserId && StringUtils.isNotEmpty(specificUserId)) {
//        usersData.filter(user => StringUtils.equalsIgnoreCase(user.getOrElse(redisKeyProperty, "").asInstanceOf[String], specificUserId))
//      } else if (null != fromSpecificDate && StringUtils.isNotEmpty(specificUserId)) {
//        println(s"Fetching all the user records from this specific date:$fromSpecificDate ")
//        usersData.filter(user => {
//          dtf1.parse(user.getOrElse("updateddate", null).asInstanceOf[String]).after(dtf2.parse(fromSpecificDate))
//        })
//      } else {
//        println("Data is not fetching from the table since input is invalid")
//        null
//      }
//    }

    def filterUserData(userDF: DataFrame): DataFrame = {
      if (null != specificUserId && StringUtils.isNotEmpty(specificUserId)) {
        println("for specfic userId" + specificUserId)
        userDF.filter(col("id") === specificUserId)
      } else if (null != fromSpecificDate && StringUtils.isNotEmpty(fromSpecificDate)) {
        println(s"Fetching all the user records from this specific date:$fromSpecificDate ")
        userDF.filter(col("updateddate").isNull || to_date(col("updateddate"), "yyyy-MM-dd HH:mm:ss:SSSZ").geq(lit(fromSpecificDate)))
      } else {
        println("elsePart")
        userDF
      }
    }

    println("Total UserData1" + getUserData().show(false))

    def getUserData(): DataFrame = {

      val userDF = spark.read.format("org.apache.spark.sql.cassandra").option("table", "user").option("keyspace", sunbirdKeyspace).load().select("*").persist()
      val userDF1 = filterUserData(userDF)
      println("userDF====" + userDF.show(false))
      println("userDF1====" + userDF1.show(false))
      val userOrgDF = spark.read.format("org.apache.spark.sql.cassandra").option("table", "user_org").option("keyspace", sunbirdKeyspace).load().filter(lower(col("isdeleted")) === "false")
        .select(col("userid"), col("organisationid")).persist()

      val organisationDF = spark.read.format("org.apache.spark.sql.cassandra").option("table", "organisation").option("keyspace", sunbirdKeyspace).load()
        .select(col("id"), col("orgname"), col("channel"), col("orgcode"),
          col("locationids"), col("isrootorg")).persist()

      val locationDF = spark.read.format("org.apache.spark.sql.cassandra").option("table", "location").option("keyspace", sunbirdKeyspace).load()
        .select(col("id"), col("name"), col("type")).persist()

      val externalIdentityDF = spark.read.format("org.apache.spark.sql.cassandra").option("table", "usr_external_identity").option("keyspace", sunbirdKeyspace).load()
        .select(col("provider"), col("idtype"), col("externalid"), col("userid")).persist()
      // Get CustodianOrgID
      val custRootOrgId = getCustodianOrgId()
      val custodianUserDF = generateCustodianOrgUserData(custRootOrgId, userDF, organisationDF, locationDF, externalIdentityDF)
      val stateUserDF = generateStateOrgUserData(custRootOrgId, userDF, organisationDF, locationDF, externalIdentityDF, userOrgDF)

      val userLocationResolvedDF = custodianUserDF.unionByName(stateUserDF)
      /**
       * Get a union of RootOrg and SubOrg information for a User
       */
      val userRootOrgDF = userDF
        .join(userOrgDF, userOrgDF.col("userid") === userDF.col("userid") && userOrgDF.col("organisationid") === userDF.col("rootorgid"))
        .select(userDF.col("userid"), col("rootorgid"), col("organisationid"), userDF.col("channel"))

      val userSubOrgDF = userDF
        .join(userOrgDF, userOrgDF.col("userid") === userDF.col("userid") && userOrgDF.col("organisationid") =!= userDF.col("rootorgid"))
        .select(userDF.col("userid"), col("rootorgid"), col("organisationid"), userDF.col("channel"))

      val rootOnlyOrgDF = userRootOrgDF.join(userSubOrgDF, Seq("userid"), "leftanti").select(userRootOrgDF.col("*"))
      val userOrgDenormDF = rootOnlyOrgDF.union(userSubOrgDF)

      /**
       * Resolve organization name for a RootOrg
       */
      val resolvedOrgNameDF = userOrgDenormDF
        .join(organisationDF, organisationDF.col("id") === userOrgDenormDF.col("rootorgid"), "left_outer")
        .groupBy("userid")
        .agg(concat_ws(",", collect_set("orgname")).as("orgname_resolved"))

      val schoolNameDF = userOrgDenormDF
        .join(organisationDF, organisationDF.col("id") === userOrgDenormDF.col("organisationid"), "left_outer")
        .select(userOrgDenormDF.col("userid"), col("orgname").as("schoolname_resolved"))
        .groupBy("userid")
        .agg(concat_ws(",", collect_set("schoolname_resolved")).as("schoolname_resolved"))

      val userDataDF = userLocationResolvedDF
        .join(resolvedOrgNameDF, Seq("userid"), "left")
        .join(schoolNameDF, Seq("userid"), "left")
        .persist()

      userOrgDF.unpersist()
      organisationDF.unpersist()
      locationDF.unpersist()
      externalIdentityDF.unpersist()
      userDF.unpersist()

      userDataDF
    }

    def getCustodianOrgId(): String = {
      val systemSettingDF = spark.read.format("org.apache.spark.sql.cassandra").option("table", "system_settings").option("keyspace", sunbirdKeyspace).load()
        .where(col("id") === "custodianOrgId" && col("field") === "custodianOrgId")
        .select(col("value")).persist()
      systemSettingDF.select("value").first().getString(0)
      //"01285019302823526477"
    }

    def generateCustodianOrgUserData(custodianOrgId: String, userDF: DataFrame, organisationDF: DataFrame,
                                     locationDF: DataFrame, externalIdentityDF: DataFrame): DataFrame = {
      /**
       * Resolve the state, district and block information for CustodianOrg Users
       * CustodianOrg Users will have state, district and block (optional) information
       */

      val userExplodedLocationDF = userDF.withColumn("exploded_location", explode_outer(col("locationids")))
        .select(col("userid"), col("exploded_location"))

      val userStateDF = userExplodedLocationDF
        .join(locationDF, col("exploded_location") === locationDF.col("id") && locationDF.col("type") === "state")
        .select(userExplodedLocationDF.col("userid"), col("name").as("state_name"))

      val userDistrictDF = userExplodedLocationDF
        .join(locationDF, col("exploded_location") === locationDF.col("id") && locationDF.col("type") === "district")
        .select(userExplodedLocationDF.col("userid"), col("name").as("district_name"))

      val userBlockDF = userExplodedLocationDF
        .join(locationDF, col("exploded_location") === locationDF.col("id") && locationDF.col("type") === "block")
        .select(userExplodedLocationDF.col("userid"), col("name").as("block_name"))

      /**
       * Join with the userDF to get one record per user with district and block information
       */

      val custodianOrguserLocationDF = userDF.filter(col("rootorgid") === lit(custodianOrgId))
        .join(userStateDF, Seq("userid"), "inner")
        .join(userDistrictDF, Seq("userid"), "left")
        .join(userBlockDF, Seq("userid"), "left")
        .select(userDF.col("*"),
          col("state_name"),
          col("district_name"),
          col("block_name")).drop(col("locationids"))

      val custodianUserPivotDF = custodianOrguserLocationDF
        .join(externalIdentityDF, externalIdentityDF.col("userid") === custodianOrguserLocationDF.col("userid"), "left")
        .join(organisationDF, externalIdentityDF.col("provider") === organisationDF.col("channel")
          && organisationDF.col("isrootorg").equalTo(true), "left")
        .groupBy(custodianOrguserLocationDF.col("userid"), organisationDF.col("id"))
        .pivot("idtype", Seq("declared-ext-id", "declared-school-name", "declared-school-udise-code"))
        .agg(first(col("externalid")))
        .select(custodianOrguserLocationDF.col("userid"),
          col("declared-ext-id"),
          col("declared-school-name"),
          col("declared-school-udise-code"),
          organisationDF.col("id").as("user_channel"))

      val custodianUserDF = custodianOrguserLocationDF.as("userLocDF")
        .join(custodianUserPivotDF, Seq("userid"), "left")
        .select("userLocDF.*", "declared-ext-id", "declared-school-name", "declared-school-udise-code", "user_channel")
      custodianUserDF
    }

    def generateStateOrgUserData(custRootOrgId: String, userDF: DataFrame, organisationDF: DataFrame, locationDF: DataFrame,
                                 externalIdentityDF: DataFrame, userOrgDF: DataFrame): DataFrame = {
      val stateOrgExplodedDF = organisationDF.withColumn("exploded_location", explode_outer(col("locationids")))
        .select(col("id"), col("exploded_location"))

      val orgStateDF = stateOrgExplodedDF.join(locationDF, col("exploded_location") === locationDF.col("id") && locationDF.col("type") === "state")
        .select(stateOrgExplodedDF.col("id"), col("name").as("state_name"))

      val orgDistrictDF = stateOrgExplodedDF
        .join(locationDF, col("exploded_location") === locationDF.col("id") && locationDF.col("type") === "district")
        .select(stateOrgExplodedDF.col("id"), col("name").as("district_name"))

      val orgBlockDF = stateOrgExplodedDF
        .join(locationDF, col("exploded_location") === locationDF.col("id") && locationDF.col("type") === "block")
        .select(stateOrgExplodedDF.col("id"), col("name").as("block_name"))

      val stateOrgLocationDF = organisationDF
        .join(orgStateDF, Seq("id"))
        .join(orgDistrictDF, Seq("id"), "left")
        .join(orgBlockDF, Seq("id"), "left")
        .select(organisationDF.col("id").as("orgid"), col("orgname"),
          col("orgcode"), col("isrootorg"), col("state_name"), col("district_name"), col("block_name"))

      // exclude the custodian user != custRootOrgId
      // join userDf to user_orgDF and then join with OrgDF to get orgname and orgcode ( filter isrootorg = false)

      val subOrgDF = userOrgDF
        .join(stateOrgLocationDF, userOrgDF.col("organisationid") === stateOrgLocationDF.col("orgid")
          && stateOrgLocationDF.col("isrootorg").equalTo(false))
        .dropDuplicates(Seq("userid"))
        .select(col("userid"), stateOrgLocationDF.col("*"))

      val stateUserLocationResolvedDF = userDF.filter(col("rootorgid") =!= lit(custRootOrgId))
        .join(subOrgDF, Seq("userid"), "left")
        .select(userDF.col("*"),
          subOrgDF.col("orgname").as("declared-school-name"),
          subOrgDF.col("orgcode").as("declared-school-udise-code"),
          subOrgDF.col("state_name"),
          subOrgDF.col("district_name"),
          subOrgDF.col("block_name")).drop(col("locationids"))

      val stateUserDF = stateUserLocationResolvedDF.as("state_user")
        .join(externalIdentityDF, externalIdentityDF.col("idtype") === col("state_user.channel")
          && externalIdentityDF.col("provider") === col("state_user.channel")
          && externalIdentityDF.col("userid") === col("state_user.userid"), "left")
        .select(col("state_user.*"), externalIdentityDF.col("externalid").as("declared-ext-id"), col("rootorgid").as("user_channel"))
      stateUserDF
    }


    //    val usersData = getFilteredUserRecords(sc.cassandraTable(userKeyspace, userTableName).map(f => f.toMap))
    //    val mappedData = usersData.map { obj =>
    //      (obj.getOrElse(redisKeyProperty, "").asInstanceOf[String], obj)
    //    }
    //    mappedData.collect().foreach(record => {
    //      val filteredData = record._2.toSeq.filterNot { case (_, y) => (y.isInstanceOf[String] && y.asInstanceOf[String].forall(_.isWhitespace)) || y == null }
    //      val userRdd = sc.parallelize(seqOfAnyToSeqString(filteredData))
    //      sc.toRedisHASH(userRdd, record._1)
    //    })
  }
}
