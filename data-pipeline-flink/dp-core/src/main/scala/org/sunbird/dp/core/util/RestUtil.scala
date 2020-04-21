package org.sunbird.dp.core.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.flink.annotation.Internal
import org.apache.http.client.methods.{HttpGet, HttpRequestBase}
import org.apache.http.impl.client.HttpClients

import scala.io.Source

@Internal
class RestUtil extends Serializable {

    def get[T](url: String, headers: Option[Map[String, String]] = None): T = {
        val httpClient = HttpClients.createDefault()
        val gson = new Gson()
        val request = new HttpGet(url)
        headers.getOrElse(Map()).foreach {
            case (headerName, headerValue) => request.addHeader(headerName, headerValue)
        }
        try {
            val httpResponse = httpClient.execute(request.asInstanceOf[HttpRequestBase])
            val entity = httpResponse.getEntity
            val inputStream = entity.getContent
            val content = Source.fromInputStream(inputStream, "UTF-8").getLines.mkString
            inputStream.close
            gson.fromJson(content, new TypeToken[T]() {}.getType)

        } finally {
            httpClient.close()
        }

    }


}