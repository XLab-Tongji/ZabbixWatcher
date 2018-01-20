package com.xlab.dao.imp;

import com.xlab.dao.InfluxRepository;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class InfluxRepositoryImp implements InfluxRepository {

    private InfluxDB influxDB;

    @Override
    public InfluxDB influxBuilder(String username, String password, String database, String url) {
        if(influxDB==null){
            influxDB= InfluxDBFactory.connect(url,username,password);
            if(!influxDB.databaseExists(database)) {
                //influxDB.createDatabase(database);
                influxDB.query(new Query("CREATE DATABASE "+database,"zabbix"));
            }
        }
        return influxDB;
    }

    @Override
    public QueryResult.Series readData(String database, String measurement){
        Query query = new Query("select * from "+measurement, database);
        QueryResult queryResult = influxDB.query(query);
        QueryResult.Result result = queryResult.getResults().get(0);
        QueryResult.Series series=result.getSeries().get(0);
        return series;
    }

    @Override
    public void insert(String database, String measurement, Map<String, String> tags, Map<String, Object> fields,long time) {
        Point.Builder builder=Point.measurement(measurement);
        builder.tag(tags);
        builder.fields(fields);
        builder.time(time, TimeUnit.SECONDS);
        influxDB.write(database,"",builder.build());
    }

    @Override
    public void createPolicy(String name, String database, String duration, String replication) {
        String query=String.format("CREATE RETENTION POLICY " +name +" ON "+database+
                " DURATION "+duration+" REPLICATION "+replication+"DEFAULT");
        influxDB.query(new Query(query,database));
    }
}
