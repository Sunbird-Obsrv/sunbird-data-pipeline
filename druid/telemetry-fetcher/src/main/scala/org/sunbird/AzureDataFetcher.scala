package org.sunbird

import org.sunbird.cloud.storage.factory.{StorageConfig, StorageServiceFactory}

object AzureDataFetcher {
  val storageService = StorageServiceFactory.getStorageService(StorageConfig("azure", System.getenv("azure_storage_key"), System.getenv("azure_storage_secret")))

  @throws(classOf[DataFetcherException])
  def getObjectKeys(queries: Array[Query]): Array[String] = {
    val keys = for (query <- queries) yield {
      getKeys(query)
    }
    keys.flatMap { x => x.map { x => x } }
  }

  private def getKeys(query: Query): Array[String] = {
    val keys = storageService.searchObjects(query.bucket.get, query.prefix.get, query.startDate, query.endDate, None, query.datePattern.getOrElse("yyyy-MM-dd"))
    storageService.getPaths(query.bucket.get, keys).toArray
  }
}
