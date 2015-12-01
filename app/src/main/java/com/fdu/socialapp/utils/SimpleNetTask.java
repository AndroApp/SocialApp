package com.fdu.socialapp.utils;

import android.content.Context;

/**
 * Created by mh on 2015/12/1.
 */
public abstract class SimpleNetTask extends NetAsyncTask {
    protected SimpleNetTask(Context cxt) {
        super(cxt);
    }

    protected SimpleNetTask(Context cxt, boolean openDialog) {
        super(cxt, openDialog);
    }


    @Override
    protected void onPost(Exception e) {
        if (e != null) {
            e.printStackTrace();
            Utils.toast(e.getMessage());
            //Utils.toast(ctx, R.string.pleaseCheckNetwork);
        } else {
            onSucceed();
        }
    }

    protected abstract void doInBack() throws Exception;

    protected abstract void onSucceed();

}
