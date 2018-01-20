package com.xlab.service;

import com.xlab.entity.bean.CsvBean;
import com.xlab.entity.bean.WebInfluxRequest;

import java.util.List;
import java.util.Map;

public interface ZabbixInfluxService {
    boolean setInfluxRequestSource(WebInfluxRequest webInfluxRequest);
    boolean connect();
    void insertEntity(Map entity,Map<String,String> tags,String measurement);
    CsvBean readData(String measurement);
}
