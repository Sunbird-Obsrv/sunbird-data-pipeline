package org.sunbird
import org.sunbird.cloud.storage.factory.{StorageConfig, StorageServiceFactory}
object AzureDataFetcher {
  val storageService = StorageServiceFactory.getStorageService(StorageConfig("azure", System.getenv("azure_storage_key"), System.getenv("azure_storage_secret")))
  @throws(classOf[DataFetcherException])
  def getObjectKeys(queries: Array[Query]): Array[String] = {
    val keys = for (query <- queries) yield {
      val paths = if (query.folder.isDefined && query.endDate.isDefined && query.folder.getOrElse("false").equals("true")) {
        Array("wasb://" + getBucket(query.bucket) + "@" + System.getenv("azure_storage_key") + ".blob.core.windows.net" + "/" + getPrefix(query.prefix) + query.endDate.getOrElse(""))
      } else {
        getKeys(query);
      }
      if (query.excludePrefix.isDefined) {
        paths.filter { x => !x.contains(query.excludePrefix.get) }
      } else {
        paths
      }
    }
    keys.flatMap { x => x.map { x => x } }
  }
  private def getKeys(query: Query): Array[String] = {
    val keys = storageService.searchObjects(getBucket(query.bucket), getPrefix(query.prefix), query.startDate, query.endDate, query.delta, query.datePattern.getOrElse("yyyy-MM-dd"))
    storageService.getPaths("telemetry-data-store", keys).toArray
  }

  private def getBucket(bucket: Option[String]): String = {
    bucket.getOrElse("prod-data-store");
  }

  private def getPrefix(prefix: Option[String]): String = {
    prefix.getOrElse("raw/");
  }
}
