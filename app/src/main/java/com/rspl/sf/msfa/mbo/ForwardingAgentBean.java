package com.rspl.sf.msfa.mbo;

import java.io.Serializable;

/**
 * Created by e10604 on 16/5/2017.
 */

public class ForwardingAgentBean implements Serializable {

    public String getForAgentCode() {
        return forAgentCode;
    }

    public void setForAgentCode(String forAgentCode) {
        this.forAgentCode = forAgentCode;
    }

    public String getForAgentDesc() {
        return forAgentDesc;
    }

    public void setForAgentDesc(String forAgentDesc) {
        this.forAgentDesc = forAgentDesc;
    }

    String forAgentCode,forAgentDesc;


}
