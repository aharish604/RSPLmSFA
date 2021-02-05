package com.rspl.sf.msfa.mbo;

import java.io.Serializable;

/**
 * Created by ccb on 08-06-2017.
 */

public class ProcessingField implements Serializable{


    String processingFieldCode;

    public String getProcessingFieldDesc() {
        return processingFieldDesc;
    }

    public void setProcessingFieldDesc(String processingFieldDesc) {
        this.processingFieldDesc = processingFieldDesc;
    }

    public String getProcessingFieldCode() {
        return processingFieldCode;
    }

    public void setProcessingFieldCode(String processingFieldCode) {
        this.processingFieldCode = processingFieldCode;
    }

    String processingFieldDesc;
}
