package com.rspl.sf.msfa.so;

/**
 * Created by e10769 on 12-07-2017.
 */

public class UnlRecBean {
    private String text="";

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text.toString();
    }
}
