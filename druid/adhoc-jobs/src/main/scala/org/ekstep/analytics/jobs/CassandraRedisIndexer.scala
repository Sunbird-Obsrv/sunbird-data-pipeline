package org.ekstep.analytics.jobs

import java.text.SimpleDateFormat

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.lang.StringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.{col, collect_set, concat_ws, explode_outer, first, lit, lower, _}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.ekstep.analytics.util.JSONUtils
import com.typesafe.config.{Config, ConfigFactory}
import org.ekstep.analytics.util.JSONUtils
import com.datastax.spark.connector._
import com.redislabs.provider.redis._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

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


    val conf = new SparkConf()
      .setAppName("CassandraToRedisIndexer")
      .setMaster("local[*]")
      // Cassandra settings
      .set("spark.cassandra.connection.host", "localhost")
      // redis settings
      .set("spark.redis.host", "localhost")
      .set("spark.redis.port", "6379")
      .set("spark.redis.db", "12")
    //.set("spark.redis.max.pipeline.size", config.getString("redis.max.pipeline.size"))

    val sc = new SparkContext(conf)
    //val userKeyspace = config.getString("cassandra.user.keyspace")


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
        case (x, y) => (x.toString, if (!y.isInstanceOf[String]) {
          if (null != y) {
            JSONUtils.serialize(y.asInstanceOf[AnyRef])
          } else {
            ""
          }
        } else {
          y.asInstanceOf[String]
        })
      }
    }

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

    def getUserData(): DataFrame = {

      val userDF = filterUserData(spark.read.format("org.apache.spark.sql.cassandra").option("table", "user").option("keyspace", sunbirdKeyspace).load().
        select("*").persist())
        // Flattening the BGMS
        .withColumn("medium", explode_outer(col("framework.medium")))
        .withColumn("subject", explode_outer(col("framework.subject")))
        .withColumn("board", explode_outer(col("framework.board")))
        .withColumn("grade", explode_outer(col("framework.gradeLevel")))

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
      filteredDf(userDataDF)
    }

    def filteredDf(denormedUserDF: DataFrame): DataFrame = {
      denormedUserDF.select(
        col("id").as("id"),
        col("userid").as("userid"),
        col("channel").as("channel"),
        col("countrycode").as("countrycode"),
        col("createdby").as("createdby"),
        col("createddate").as("createddate"),
        col("currentlogintime").as("currentlogintime"),
        col("email").as("email"),
        col("emailverified").as("emailverified"),
        col("firstname").as("firstname"),
        col("flagsvalue").as("flagsvalue"),
        col("framework").as("framework"),
        col("gender").as("gender"),
        col("grade").as("grade"),
        col("isdeleted").as("isdeleted"),
        col("language").as("language"),
        col("lastlogintime").as("lastlogintime"),
        col("lastname").as("lastname"),
        col("location").as("location"),
        col("locationids").as("locationids"),
        col("loginid").as("loginid"),
        col("maskedemail").as("maskedemail"),
        col("maskedphone").as("maskedphone"),
        col("password").as("password"),
        col("phone").as("phone"),
        col("phoneverified").as("phoneverified"),
        col("prevusedemail").as("prevusedemail"),
        col("prevusedphone").as("prevusedphone"),
        col("profilesummary").as("profilesummary"),
        col("profilevisibility").as("profilevisibility"),
        col("provider").as("provider"),
        col("recoveryemail").as("recoveryemail"),
        col("recoveryphone").as("recoveryphone"),
        col("registryid").as("registryid"),
        col("roles").as("roles"),
        col("rootorgid").as("rootorgid"),
        col("status").as("status"),
        col("subject").as("subject"),
        col("tcstatus").as("tcstatus"),
        col("tcupdateddate").as("tcupdateddate"),
        col("temppassword").as("temppassword"),
        col("thumbnail").as("thumbnail"),
        col("temppassword").as("temppassword"),
        col("tncacceptedon").as("tncacceptedon"),
        col("tncacceptedversion").as("tncacceptedversion"),
        col("updatedby").as("updatedby"),
        col("updateddate").as("updateddate"),
        col("username").as("username"),
        col("usertype").as("usertype"),
        col("webpages").as("webpages"),
        col("temppassword").as("temppassword"),
        col("declared-ext-id").as("externalid"),
        col("declared-school-name").as("schoolname"),
        col("declared-school-udise-code").as("schooludisecode"),
        col("user_channel").as("userchannel"),
        col("user_channel").as("orgname"),
        col("schoolname_resolved").as("schoolname"),
        col("district").as("district"),
        col("block").as("block")
      )
    }

    def getCustodianOrgId(): String = {
      val systemSettingDF = spark.read.format("org.apache.spark.sql.cassandra").option("table", "system_settings").option("keyspace", sunbirdKeyspace).load()
        .where(col("id") === "custodianOrgId" && col("field") === "custodianOrgId")
        .select(col("value")).persist()
      systemSettingDF.select("value").first().getString(0)
    }

    def generateCustodianOrgUserData(custodianOrgId: String, userDF: DataFrame, organisationDF: DataFrame,
                                     locationDF: DataFrame, externalIdentityDF: DataFrame): DataFrame = {
      /**
       * Resolve the state, district and block information for CustodianOrg Users
       * CustodianOrg Users will have state, district and block (optional) information
       */

      val userExplodedLocationDF = userDF
        .withColumn("exploded_location", explode_outer(col("locationids")))
        .select(col("userid"), col("exploded_location"), col("locationids"))

      println("userExplodedLocationDF" + userExplodedLocationDF.show(false))

      val userStateDF = userExplodedLocationDF
        .join(locationDF, col("exploded_location") === locationDF.col("id") && locationDF.col("type") === "state")
        .select(userExplodedLocationDF.col("userid"), col("name").as("state_name"))

      val userDistrictDF = userExplodedLocationDF
        .join(locationDF, col("exploded_location") === locationDF.col("id") && locationDF.col("type") === "district")
        .select(userExplodedLocationDF.col("userid"), col("name").as("district"))

      val userBlockDF = userExplodedLocationDF
        .join(locationDF, col("exploded_location") === locationDF.col("id") && locationDF.col("type") === "block")
        .select(userExplodedLocationDF.col("userid"), col("name").as("block"))

      /**
       * Join with the userDF to get one record per user with district and block information
       */

      val custodianOrguserLocationDF = userDF.filter(col("rootorgid") === lit(custodianOrgId))
        .join(userStateDF, Seq("userid"), "inner")
        .join(userDistrictDF, Seq("userid"), "left")
        .join(userBlockDF, Seq("userid"), "left")
        .select(userDF.col("*"),
          col("state_name"),
          col("district"),
          col("block"))
      // .drop(col("locationids"))

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
        .select(stateOrgExplodedDF.col("id"), col("name").as("district"))

      val orgBlockDF = stateOrgExplodedDF
        .join(locationDF, col("exploded_location") === locationDF.col("id") && locationDF.col("type") === "block")
        .select(stateOrgExplodedDF.col("id"), col("name").as("block"))

      val stateOrgLocationDF = organisationDF
        .join(orgStateDF, Seq("id"))
        .join(orgDistrictDF, Seq("id"), "left")
        .join(orgBlockDF, Seq("id"), "left")
        .select(organisationDF.col("id").as("orgid"), col("orgname"),
          col("orgcode"), col("isrootorg"), col("state_name"), col("district"), col("block"))

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
          subOrgDF.col("district"),
          subOrgDF.col("block"))
      // .drop(col("locationids"))

      val stateUserDF = stateUserLocationResolvedDF.as("state_user")
        .join(externalIdentityDF, externalIdentityDF.col("idtype") === col("state_user.channel")
          && externalIdentityDF.col("provider") === col("state_user.channel")
          && externalIdentityDF.col("userid") === col("state_user.userid"), "left")
        .select(col("state_user.*"), externalIdentityDF.col("externalid").as("declared-ext-id"), col("rootorgid").as("user_channel"))
      stateUserDF
    }

    val userDenormedData = getUserData()
    val fn = userDenormedData.schema.fieldNames
    val maps = userDenormedData.rdd.map(row => fn.map(field => field -> row.getAs(field)).toMap).collect()
    val mappedData = maps.map(x => (x.getOrElse(redisKeyProperty, ""), x.toSeq))
    mappedData.foreach(y => {
      val toSeq = seqOfAnyToSeqString(y._2)
      spark.sparkContext.toRedisHASH(spark.sparkContext.parallelize(toSeq), y._1)
    })
  }
}
