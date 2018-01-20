package com.xlab.schedule;

import com.xlab.entity.bean.WebInfluxRequest;
import com.xlab.entity.bean.WebZabbixRequest;
import com.xlab.service.ZabbixInfluxService;
import com.xlab.service.ZabbixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
@Component
public class ZabbixSchedule {

    @Autowired
    private ZabbixService zabbixService;
    @Autowired
    private ZabbixInfluxService zabbixInfluxService;

    @Value("${influxdb.ip}")
    private String influxdbIp;
    @Value("${influxdb.port}")
    private String influxdbPort;
    @Value("${influxdb.user}")
    private String influxdbUser;
    @Value("${influxdb.password}")
    private String influxdbPwd;
    @Value("${influxdb.database}")
    private String influxdbDbName;

    @Value("${zabbix.ip}")
    private String zabbixIp;
    @Value("${zabbix.port}")
    private String zabbixPort;
    @Value("${zabbix.user}")
    private String zabbixUser;
    @Value("${zabbix.password}")
    private String zabbixPwd;
    @Value("${zabbix.hostname}")
    private String zabbixHost;

    @PostConstruct
    public void init(){
        WebInfluxRequest webInfluxRequest=new WebInfluxRequest();
        webInfluxRequest.setInfluxIp(influxdbIp);
        webInfluxRequest.setInfluxPort(influxdbPort);
        webInfluxRequest.setInfluxUsername(influxdbUser);
        webInfluxRequest.setInfluxPassword(influxdbPwd);
        webInfluxRequest.setInfluxDbName(influxdbDbName);

        WebZabbixRequest webZabbixRequest=new WebZabbixRequest();
        webZabbixRequest.setZabbixIp(zabbixIp);
        webZabbixRequest.setZabbixPort(zabbixPort);
        webZabbixRequest.setZabbixUsername(zabbixUser);
        webZabbixRequest.setZabbixPassword(zabbixPwd);
        webZabbixRequest.setZabbixHostname(zabbixHost);

        zabbixService.setZabbixRequestSource(webZabbixRequest);
        zabbixService.login();
        zabbixInfluxService.setInfluxRequestSource(webInfluxRequest);
        zabbixInfluxService.connect();

        zabbixService.setDiscoveryDelay("1s","net.if.discovery");
        zabbixService.setDiscoveryDelay("1s","vfs.fs.discovery");
    }

    @Scheduled(cron = "0/1 * * * * *")
    public void getAllItems(){
        zabbixService.getItemsOnProcessCnt();
        zabbixService.getItemsOnInodeUsed();
        zabbixService.getItemsOnDiskUsed();
        zabbixService.getItemsOnDiskWrite();
        zabbixService.getItemsOnDiskRead();
        zabbixService.getItemsOnMemoryUsed();
        //zabbixService.getItemsOnNetIn();
        //zabbixService.getItemsOnNetOut();
        zabbixService.getItemsOnCpuUsed();
        zabbixService.getItemsOnCpuLoad();
    }
}
