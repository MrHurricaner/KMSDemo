package com.kms.demo.app;

import android.content.Context;

import com.kms.appcore.apiservice.ApiResponse;
import com.kms.appcore.apiservice.ApiSingleObserver;
import com.kms.demo.utils.ToastUtil;

/**
 * @author matrixelement
 */
public class BusinessApiSingleObserver<T> extends ApiSingleObserver<T> {

    private Context context;

    public BusinessApiSingleObserver(Context context) {
        this.context = context;
    }

    @Override
    public void onApiSuccess(T t) {

    }

    @Override
    public void onApiFailure(ApiResponse response) {
        ToastUtil.showLongToast(context, response.getErrMsg(context));
    }
}
