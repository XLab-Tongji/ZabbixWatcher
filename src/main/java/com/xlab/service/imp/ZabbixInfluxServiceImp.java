package com.xlab.service.imp;

import com.xlab.dao.InfluxRepository;
import com.xlab.entity.bean.CsvBean;
import com.xlab.entity.bean.WebInfluxRequest;
import com.xlab.service.ZabbixInfluxService;
import org.influxdb.InfluxDB;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZabbixInfluxServiceImp implements ZabbixInfluxService{

    @Autowired
    private InfluxRepository influxRepository;

    private WebInfluxRequest webInfluxRequest;


    @Override
    public boolean setInfluxRequestSource(WebInfluxRequest webInfluxRequest) {
        this.webInfluxRequest=webInfluxRequest;
        return true;
    }

    @Override
    public boolean connect() {
        InfluxDB influxDB=influxRepository.influxBuilder(webInfluxRequest.getInfluxUsername(),webInfluxRequest.getInfluxPassword(),
                webInfluxRequest.getInfluxDbName(),webInfluxRequest.getInfluxUrl());
        if (influxDB!=null) {
            influxRepository.createPolicy("zabbixPolicy", webInfluxRequest.getInfluxDbName(),
                    "10d", "1");
            return true;
        }
        return false;
    }

    @Override
    public void insertEntity(Map entity,Map<String,String> tags,String measurement) {
        if(entity!=null) {
            for (Object index : entity.keySet()) {
                Map<String, Object> fields = new HashMap<>();
                fields.put(((List)index).get(0).toString(),entity.get(index));
                influxRepository.insert(webInfluxRequest.getInfluxDbName(), measurement, tags, fields,Long.parseLong((String)((List)index).get(1)));
            }
        }
    }

    @Override
    public CsvBean readData(String measurement) {
        QueryResult.Series series=influxRepository.readData(webInfluxRequest.getInfluxDbName(),measurement);
        List<String> columns=series.getColumns();
        List<List<Object>>values=series.getValues();
        CsvBean csvBean=new CsvBean();
        csvBean.setColumns(columns);
        csvBean.setValues(values);
        csvBean.setName(series.getName());
        return csvBean;
    }
}
