package com.rspl.sf.msfa.mbo;

import java.io.Serializable;

/**
 * Created by e10604 on 15/5/2017.
 */

public class RouteBean implements Serializable {

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteDesc() {
        return routeDesc;
    }

    public void setRouteDesc(String routeDesc) {
        this.routeDesc = routeDesc;
    }

    String routeId ,routeDesc;
}
