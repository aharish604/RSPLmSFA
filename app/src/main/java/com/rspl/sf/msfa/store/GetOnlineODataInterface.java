package com.rspl.sf.msfa.store;

import android.os.Bundle;

import com.sap.smp.client.odata.ODataEntity;
import com.sap.smp.client.odata.store.ODataRequestExecution;

import java.util.List;

/**
 * Created by e10769 on 20-07-2017.
 */

public interface GetOnlineODataInterface {
    void responseSuccess(ODataRequestExecution oDataRequestExecution, List<ODataEntity> entities, int operation, int requestCode, String resourcePath, Bundle bundle);
    void responseFailed(ODataRequestExecution oDataRequestExecution, int operation, int requestCode, String resourcePath, String errorMsg, Bundle bundle);
}
