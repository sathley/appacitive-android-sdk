package com.appacitive.sdk.infra;

import org.omg.CORBA.NameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sathley.
 */
public class Url {

    public Url(String baseUrl, String endpoint, String suffix, Map<String, String> queryStringParameters)
    {
        this.queryStringParameters = new HashMap<String, String>();
        this.baseUrl = baseUrl;
        this.endpoint = endpoint;
        this.suffix = suffix;
        if(queryStringParameters != null)
            this.queryStringParameters = queryStringParameters;
    }

    public String baseUrl = null;

    public String endpoint = null;

    public String suffix = null;

    public Map<String, String> queryStringParameters = null;

    @Override
    public String toString()
    {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl).append("/").append(endpoint).append("/").append(suffix);

        if(queryStringParameters.size() > 0)
            urlBuilder.append("?");

        String separator = "";
        for (Map.Entry<String, String> qsp : queryStringParameters.entrySet())
        {
            urlBuilder.append(separator);
            separator = "&";
            urlBuilder.append(qsp.getKey()).append("=").append(qsp.getValue());
        }
        return urlBuilder.toString();
    }
}
