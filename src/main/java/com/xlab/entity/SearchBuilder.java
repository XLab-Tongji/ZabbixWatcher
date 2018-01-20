package com.xlab.entity;

public class SearchBuilder {
    private Search search=new Search();

    private SearchBuilder(){

    }

    static public SearchBuilder newBuilder(){
        return new SearchBuilder();
    }

    public SearchBuilder paramEntry(String key,Object value){
        search.putParams(key,value);
        return this;
    }

    public Search build(){
        return search;
    }
}
