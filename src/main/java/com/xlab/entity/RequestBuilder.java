package com.xlab.entity;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestBuilder {
    private Request request=new Request();
    private static final AtomicInteger id=new AtomicInteger(1);

    private RequestBuilder(){

    }

    static public RequestBuilder newBuilder(){
        return new RequestBuilder();
    }

    public RequestBuilder paramEntry(String key,Object value){
        request.putParams(key,value);
        return this;
    }

    public RequestBuilder method(String method){
        request.setMethod(method);
        return this;
    }

    public RequestBuilder auth(String auth){
        request.setAuth(auth);
        return this;
    }

    public Request build(){
        if(request.getId()==null){
            request.setId(id.getAndIncrement());
        }
        return request;
    }
}
