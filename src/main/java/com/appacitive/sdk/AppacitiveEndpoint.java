package com.appacitive.sdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sathley.
 */
public class AppacitiveEndpoint {
    public AppacitiveEndpoint(Map<String, Object> endpoint)
    {
        this.setSelf(endpoint);
    }

    public AppacitiveEndpoint()
    {}

    public Map<String, Object> getMap()
    {
        Map<String, Object> nativeMap = new HashMap<String, Object>();
        nativeMap.put("label", this.label);
        nativeMap.put("type", this.type);
        nativeMap.put("objectid", this.objectId);
        nativeMap.put("object", this.object.getMap());
        return nativeMap;
    }

    public String label = null;

    public String type = null;

    public AppacitiveObject object = null;

    public long objectId = 0;

    public void setSelf(Map<String, Object> endpoint)
    {
        this.label = (String)endpoint.get("label");
        this.type = (String)endpoint.get("type");
        this.objectId = Long.getLong((String)endpoint.get("objectid"));
        if(this.object == null)
            this.object = new AppacitiveObject((Map<String, Object>)endpoint.get("object"));
        else
            this.object.setSelf((Map<String, Object>)endpoint.get("object"));
    }
}