package com.rspl.sf.msfa.sync.SyncHistoryInfo;

public interface CollectionSyncInterface {
    void onUploadDownload(boolean isUpload, PendingCountBean countBean, String syncType);
}
