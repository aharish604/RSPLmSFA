/*
package com.rspl.sf.msfa.pushNotification;

import com.sap.maf.tools.logon.core.reg.IAppSettingsUploadListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

*/
/**
 * Created by e10769 on 07-08-2017.
 *//*


public class FcmAppSettUpListener implements IAppSettingsUploadListener {
    private static FcmAppSettUpListener instance;
    private final CountDownLatch latch = new CountDownLatch(1);
    Exception error;

    private FcmAppSettUpListener() {
    }

    */
/**
     * @return FcmAppSettUpListener
     *//*

    public static FcmAppSettUpListener getInstance() {
        if (instance == null) {
            instance = new FcmAppSettUpListener();
        }
        return instance;
    }

    @Override
    public void updateAppSettingsFinished() {
        latch.countDown();
    }

    @Override
    public void updateAppSettingsFailed() {
        try {
            this.error = new GenericException("updateAppSettingsFailed");
            latch.countDown();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public synchronized Exception getError() {
        return error;
    }

    */
/**
     * Waits for the completion of the asynchronous process.      * In case this listener is not invoked      * within 60 seconds then it fails with an exception.
     *//*

    public void waitForCompletion() {
        try {
            if (!latch.await(60, TimeUnit.SECONDS))
                throw new IllegalStateException("AppSettings upload listener " + "was not called within 30 seconds.");
        } catch (InterruptedException e) {
            throw new IllegalStateException("AppSettings upload listener " + "waiting for results was interrupted.", e);
        }
    }
}
*/
