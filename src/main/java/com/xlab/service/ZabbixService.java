package com.xlab.service;

import com.google.gson.JsonObject;
import com.xlab.entity.Request;
import com.xlab.entity.Search;
import com.xlab.entity.bean.WebZabbixRequest;

import java.util.Map;

public interface ZabbixService {
    String apiVersion();
    boolean setZabbixRequestSource(WebZabbixRequest webZabbixRequest);
    boolean login();
    JsonObject call(Request request);
    void setDiscoveryDelay(String delay,String itemprototype);
    Map getItems(Search searchKey);
    Map getItemsOnProcessCnt();
    Map getItemsOnInodeUsed();

    Map getItemsOnDiskUsed();
    Map getItemsOnDiskWrite();
    Map getItemsOnDiskRead();

    Map getItemsOnMemoryUsed();

    Map getItemsOnNetIn();
    Map getItemsOnNetOut();

    Map getItemsOnCpuUsed();
    Map getItemsOnCpuLoad();

    Map getHistory(Search searchKey);
    Map getHistoryOnProcessCnt();
    Map getHistoryOnInodeUsed();

    Map getHistoryOnDiskUsed();
    Map getHistoryOnDiskWrite();
    Map getHistoryOnDiskRead();

    Map getHistoryOnMemoryUsed();

    Map getHistoryOnNetIn();
    Map getHistoryOnNetOut();

    Map getHistoryOnCpuUsed();
    Map getHistoryOnCpuLoad();
}
