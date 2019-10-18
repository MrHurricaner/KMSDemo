package com.kms.appcore.apiservice;

import android.content.Context;

import com.alibaba.fastjson.annotation.JSONField;

import androidx.annotation.StringRes;

public class ApiResponse<T> {

    private ApiErrorCode result;
    private T data;
    private String errMsg;
    private static Context sContext;

    public static void init(Context context) {
        sContext = context;
    }

    private String getString(@StringRes int resId) {
        return sContext.getString(resId);
    }

    public ApiResponse() {

    }

    public ApiResponse(ApiErrorCode apiErrorCode, T d) {
        result = apiErrorCode;
        data = d;
    }

    public ApiResponse(ApiErrorCode apiErrorCode) {
        result = apiErrorCode;
        errMsg = getString(apiErrorCode.descId);
    }

    public ApiResponse(ApiErrorCode apiErrorCode, Throwable throwable) {
        result = apiErrorCode;
        errMsg = getString(apiErrorCode.descId);
    }

    @JSONField(serialize = false)
    public ApiErrorCode getResult() {
        return result;
    }

    @JSONField(deserialize = false)
    public void setResult(ApiErrorCode result) {
        this.result = result;
    }

    @JSONField(name = "result")
    public void setErrorCode(int code) {
        this.result = ApiErrorCode.fromCode(code);
    }

    @JSONField(name = "result")
    public int getErrorCode() {
        return result.code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getErrMsg(Context context) {
        return context.getString(result.descId);
    }
}
