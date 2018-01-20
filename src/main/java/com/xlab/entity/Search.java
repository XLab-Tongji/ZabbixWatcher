package com.xlab.entity;

import java.util.HashMap;
import java.util.Map;

public class Search {
    private Map<String,Object> params=new HashMap<>();

    public Map<String, Object> getParams() {
        return params;
    }

    public void putParams(String key,Object value) {
        params.put(key,value);
    }
}
