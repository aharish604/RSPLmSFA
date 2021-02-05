package androidx.core.os;

import android.os.AsyncTask;

class AsyncTaskCompatHoneycomb {


    static <Params, Progress, Result> void executeParallel(
            AsyncTask<Params, Progress, Result> task, Params... params) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }
}
