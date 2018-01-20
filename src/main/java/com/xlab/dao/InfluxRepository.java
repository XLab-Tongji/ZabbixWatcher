package com.xlab.dao;

import org.influxdb.InfluxDB;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface InfluxRepository {
    InfluxDB influxBuilder(String username, String password, String database, String url);
    void insert(String database, String measurement, Map<String, String> tags, Map<String, Object> fields,long time);
    void createPolicy(String name,String database,String duration,String replication);
    QueryResult.Series readData(String database,String measurement);
}
