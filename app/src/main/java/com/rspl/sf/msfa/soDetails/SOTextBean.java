package com.rspl.sf.msfa.soDetails;

import java.io.Serializable;

/**
 * Created by e10769 on 19-09-2017.
 */

public class SOTextBean implements Serializable {
    private String SONo = "";
    private String ItemNo = "";
    private String TextID = "";
    private String TextIDDesc = "";
    private String Text = "";

    public String getSONo() {
        return SONo;
    }

    public void setSONo(String SONo) {
        this.SONo = SONo;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getTextID() {
        return TextID;
    }

    public void setTextID(String textID) {
        TextID = textID;
    }

    public String getTextIDDesc() {
        return TextIDDesc;
    }

    public void setTextIDDesc(String textIDDesc) {
        TextIDDesc = textIDDesc;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }
}
