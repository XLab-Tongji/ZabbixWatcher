package com.xlab.entity.bean;

import java.io.Serializable;

public class WebZabbixRequest implements Serializable{
    private String zabbixUrl;
    private String zabbixIp;
    private String zabbixPort;
    private String zabbixUsername;
    private String zabbixPassword;
    private String zabbixHostname;

    public String getZabbixIp() {
        return zabbixIp;
    }

    public void setZabbixIp(String zabbixIp) {
        this.zabbixIp = zabbixIp;
    }

    public String getZabbixPort() {
        return zabbixPort;
    }

    public void setZabbixPort(String zabbixPort) {
        this.zabbixPort = zabbixPort;
    }

    public String getZabbixUrl() {
        return "http://"+zabbixIp+":"+zabbixPort+"/api_jsonrpc.php";
    }

    public void setZabbixUrl(String zabbixUrl,String zabbixPort) {
        this.zabbixUrl = "http://"+zabbixUrl+":"+zabbixPort+"/api_jsonrpc.php";
    }

    public String getZabbixUsername() {
        return zabbixUsername;
    }

    public void setZabbixUsername(String zabbixUsername) {
        this.zabbixUsername = zabbixUsername;
    }

    public String getZabbixPassword() {
        return zabbixPassword;
    }

    public void setZabbixPassword(String zabbixPassword) {
        this.zabbixPassword = zabbixPassword;
    }

    public String getZabbixHostname() {
        return zabbixHostname;
    }

    public void setZabbixHostname(String zabbixHostname) {
        this.zabbixHostname = zabbixHostname;
    }

}
