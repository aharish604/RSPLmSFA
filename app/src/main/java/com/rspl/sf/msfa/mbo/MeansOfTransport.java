package com.rspl.sf.msfa.mbo;

import java.io.Serializable;

/**
 * Created by e10604 on 15/5/2017.
 */

public class MeansOfTransport implements Serializable{

    public String getTransportId() {
        return transportId;
    }

    public void setTransportId(String transportId) {
        this.transportId = transportId;
    }

    public String getTransportDesc() {
        return transportDesc;
    }

    public void setTransportDesc(String transportDesc) {
        this.transportDesc = transportDesc;
    }

    String transportId,transportDesc;
}
