package com.cocosw.xteam.data;

import android.location.Location;

/**
 * Created by kai on 26/11/2015.
 */
public enum  MockResponse {

    SINGLE("Single result","single.json"),
    UNLIMITED("INFINIT","result.json"),
    EXCEPTION("Result with exceptions","result.json");

    public final String name;
    public final String json;

    MockResponse(String name,String json){
        this.name = name;
        this.json = json;
    }

    @Override public String toString() {
        return name;
    }

}
