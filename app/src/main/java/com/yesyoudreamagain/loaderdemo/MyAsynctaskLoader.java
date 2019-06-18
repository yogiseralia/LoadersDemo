package com.yesyoudreamagain.loaderdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;

/**
 * Created by Yogesh Seralia on 6/17/2019.
 */
public class MyAsynctaskLoader extends AsyncTaskLoader<ResponseData> {

    private ResponseData mData;
    private SampleObserver mObserver;

    // this context is application context so no worry of memory leak
    public MyAsynctaskLoader(@NonNull Context context) {
        // Loaders may be used across multiple Activitys (assuming they aren't
        // bound to the LoaderManager), so NEVER hold a reference to the context
        // directly. Doing so will cause you to leak an entire Activity's context.
        // The superclass constructor will store a reference to the Application
        // Context instead, and can be retrieved with a call to getContext().
        super(context);
    }

    /****************************************************/
    /** (1) A task that performs the asynchronous load **/
    /****************************************************/
    @Nullable
    @Override
    public ResponseData loadInBackground() {
        APiInterface instance = APiInterface.Builder.getInstance();
        try {
            return instance.todo(1).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /********************************************************/
    /** (2) Deliver the results to the registered listener **/
    /********************************************************/
    @Override
    public void deliverResult(@Nullable ResponseData data) {
        super.deliverResult(data);
        if (isReset()) {
            // loader has been reset, ignore the result and invalidate the data
            releaseData(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        ResponseData oldData = mData;
        mData = data;


        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseData(oldData);
        }
    }

    private void releaseData(ResponseData data) {
        // for any cursor or list, data can could be made closed or empty list hence leaving it empty
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mData != null) {
            // Deliver any previously loaded data immediately.
            deliverResult(mData);
        }

        // Begin monitoring the underlying data source.
        if (mObserver == null) {
            mObserver = new SampleObserver();
        }

        if (takeContentChanged() || mData == null) {
            // When the observer detects a change, it should call onContentChanged()
            // on the Loader, which will cause the next call to takeContentChanged()
            // to return true. If this is ever the case (or if the current data is
            // null), we force a new load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        // The Loader is in a stopped state, so we should attempt to cancel the
        // current load (if there is one).
        cancelLoad();

        // Note that we leave the observer as is. Loaders in a stopped state
        // should still monitor the data source for changes so that the Loader
        // will know to force a new load if it is ever started again.
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader has been stopped.
        onStopLoading();

        // At this point we can release the resources associated with 'mData'.
        if (mData != null) {
            releaseData(mData);
            mData = null;
        }

        // The Loader is being reset, so we should stop monitoring for changes.
        if (mObserver != null) {
            // TODO: unregister the observer
            mObserver = null;
        }
    }

    @Override
    public void onCanceled(@Nullable ResponseData data) {
        super.onCanceled(data);

        // The load has been canceled, so we should release the resources
        // associated with 'data'.
        releaseData(data);
    }


}
