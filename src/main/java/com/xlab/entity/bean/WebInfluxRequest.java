package com.xlab.entity.bean;

public class WebInfluxRequest {
    private String influxUrl;
    private String influxIp;
    private String influxPort;
    private String influxUsername;
    private String influxPassword;
    private String influxDbName;

    public String getInfluxUrl() {
        return "http://"+influxIp+":"+influxPort;
    }

    public void setInfluxUrl(String influxUrl) {
        this.influxUrl = influxUrl;
    }

    public String getInfluxIp() {
        return influxIp;
    }

    public void setInfluxIp(String influxIp) {
        this.influxIp = influxIp;
    }

    public String getInfluxPort() {
        return influxPort;
    }

    public void setInfluxPort(String influxPort) {
        this.influxPort = influxPort;
    }

    public String getInfluxUsername() {
        return influxUsername;
    }

    public void setInfluxUsername(String influxUsername) {
        this.influxUsername = influxUsername;
    }

    public String getInfluxPassword() {
        return influxPassword;
    }

    public void setInfluxPassword(String influxPassword) {
        this.influxPassword = influxPassword;
    }

    public String getInfluxDbName() {
        return influxDbName;
    }

    public void setInfluxDbName(String influxDbName) {
        this.influxDbName = influxDbName;
    }
}
