package org.smartregister.chw.hf.sync;

import android.content.Context;

import org.smartregister.chw.core.sync.CoreClientProcessor;
import org.smartregister.sync.ClientProcessorForJava;

public class HfClientProcessor extends CoreClientProcessor {

    private HfClientProcessor(Context context) {
        super(context);
    }

    public static ClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new HfClientProcessor(context);
        }
        return instance;
    }

    @Override
    public boolean processHfReportEvents() {
        return true;
    }

    @Override
    public boolean saveReportDateSent() {
        return false;
    }

}
