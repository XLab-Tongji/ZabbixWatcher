package com.xlab.entity.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvBean {
//    private Map<String,Object> influxElements=new HashMap<>();
//
//    public Map<String, Object> getInfluxElements() {
//        return influxElements;
//    }
//
//    public void putInfluxElements(String key,Object value) {
//        this.influxElements.put(key,value);
//    }

    private List<String> columns;
    private List<List<Object>> values;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<List<Object>> getValues() {
        return values;
    }

    public void setValues(List<List<Object>> values) {
        this.values = values;
    }
}
