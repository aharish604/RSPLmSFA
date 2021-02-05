/*
package com.rspl.sf.msfa.pushNotification;

import com.sap.maf.tools.logon.core.LogonCore;
import com.sap.maf.tools.logon.core.LogonCoreException;
import com.sap.maf.tools.logon.core.reg.AppSettings;
import com.sap.maf.tools.logon.core.reg.IAppSettingsDownloadListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

*/
/**
 * Created by e10769 on 07-08-2017.
 *//*


public class FcmAppSettDownListener implements IAppSettingsDownloadListener {
    private static FcmAppSettDownListener instance;
    private final CountDownLatch latch = new CountDownLatch(1);
    AppSettings settings;
    Exception error;

    private FcmAppSettDownListener() {
    }

    */
/**
     * @return FlightOpenListener
     *//*

    public static FcmAppSettDownListener getInstance() {
        if (instance == null) {
            instance = new FcmAppSettDownListener();
        }
        return instance;
    }

    @Override
    public void appSettingsDownloadFinished() {
        try {
            LogonCore logonCore = LogonCore.getInstance();
            this.settings = logonCore.getAppSettings();
            this.latch.countDown();
        } catch (LogonCoreException e) {
            this.error = e;
        }
    }

    @Override
    public void appSettingsDownloadFailed() {
        this.error = new GenericException("appSettingsDownloadFailed");
        latch.countDown();
    }

    public synchronized boolean finished() {
        return (settings != null || error != null);
    }

    public synchronized Exception getError() {
        return error;
    }

    public synchronized AppSettings getAppSettings() {
        return settings;
    }

    */
/**
     * Waits for the completion of the asynchronous process.       * In case this listener is not invoked within 60 seconds       * then it fails with an exception.
     *//*

    public void waitForCompletion() {
        try {
            if (!latch.await(60, TimeUnit.SECONDS))
                throw new IllegalStateException("AppSettings download listener " + "was not called within 30 seconds.");
            else if (!finished())
                throw new IllegalStateException("AppSettings download  listener is " + "not in finished state after having completed successfully");
        } catch (InterruptedException e) {
            throw new IllegalStateException("AppSettings download listener waiting for" + " results was interrupted.", e);
        }
    }
}
*/
