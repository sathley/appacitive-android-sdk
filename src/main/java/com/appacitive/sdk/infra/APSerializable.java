package com.appacitive.sdk.infra;

import java.util.Map;

/**
 * Created by sathley.
 */
public interface APSerializable {

    public void setSelf(Map<String, Object> APEntity);

    public Map<String, Object> getMap();
}