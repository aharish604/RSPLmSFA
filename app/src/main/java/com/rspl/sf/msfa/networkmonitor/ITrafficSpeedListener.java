package com.rspl.sf.msfa.networkmonitor;

public interface ITrafficSpeedListener {
    void onTrafficSpeedMeasured(double upStream, double downStream);
}
