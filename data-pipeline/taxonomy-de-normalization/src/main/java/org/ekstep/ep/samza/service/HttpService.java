package org.ekstep.ep.samza.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.Map;


public class HttpService {

    private String host;
    private HttpClient httpClient;
    private HttpResponse httpResponse;
    private HttpGet httpGet;

    // TODO Use HTTPBuilder
    public HttpService(HttpClient httpClient,String host){
        this(host);
        this.httpClient = httpClient;
    }

    public HttpService(String host){
        this.host = host;
        this.httpClient = new DefaultHttpClient();
    }
    public HttpGet createRequest(String url){
        httpGet = new HttpGet(host+url);
        return getHttpGet();
    }
    public HttpGet setRequest(HttpGet httpGet){
        this.httpGet = httpGet;
        return httpGet;
    }
    public void addHeaders(Map<String,String> headerOptions){
        for(Map.Entry<String,String> entry: headerOptions.entrySet()){
            getHttpGet().addHeader(entry.getKey(), entry.getValue());
        }
    }
    public HttpGet getHttpGet(){
        return this.httpGet;
    }
    public HttpResponse get(String url,Map<String,String> headerOptions) throws java.io.IOException{
        HttpGet getRequest = createRequest(url);
        addHeaders(headerOptions);
        httpResponse = httpClient.execute(getRequest);
        System.out.println(httpResponse);
        // TODO take care of pooling and keep-alive
        return httpResponse;
    }
//    public void closeConnection(){
//        ClientConnectionManager connectionManager = httpClient.getConnectionManager();
//        if(connectionManager!=null)
//            connectionManager.shutdown();
//    }
}
