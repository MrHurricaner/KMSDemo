package com.kms.appcore.apiservice;

import com.alibaba.fastjson.JSON;
import com.kms.appcore.networkstate.NetConnectivity;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.ResponseBody;
import retrofit2.Response;


/**
 * @author ziv
 */
public abstract class ApiSingleObserver<T> extends AtomicReference<Disposable> implements SingleObserver<Response<ApiResponse<T>>>, Disposable {

    private static final String TAG = "ApiService/ApiSingleObserver";
    private static final long serialVersionUID = 6117901259965613041L;

    private ApiCallback callback;

    public ApiSingleObserver() {
        this.callback = new ApiCallback<T>() {

            @Override
            public void onSuccess(T result) {
                onApiSuccess(result);
            }

            @Override
            public void onFailure(ApiResponse errorResponse) {
                onApiFailure(errorResponse);
            }
        };
    }

    public interface OnKickedCallback {
        /**
         * 用户被踢
         */
        void doKick();
    }

    private static OnKickedCallback mKCallback;

    public static void setKickCallback(OnKickedCallback callback) {
        mKCallback = callback;
    }

    @Override
    public void onError(Throwable e) {
        try {
            if (!NetConnectivity.getConnectivityManager().isConnected()) {
                callback.onFailure(new ApiResponse(ApiErrorCode.NETWORK_ERROR, e));
            } else {
                callback.onFailure(new ApiResponse(ApiErrorCode.SYSTEM_ERROR, e));
            }
            e.printStackTrace();
        } catch (Throwable ex) {
            Exceptions.throwIfFatal(ex);
            RxJavaPlugins.onError(new CompositeException(e, ex));
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        DisposableHelper.setOnce(this, d);
    }

    @Override
    public void onSuccess(Response<ApiResponse<T>> value) {
        try {
            Response<ApiResponse<T>> response = value;

            if (response.isSuccessful()) {
                ApiResponse apiResponse = response.body();
                if (apiResponse != null && apiResponse.getResult() == ApiErrorCode.SUCCESS) {
                    Object data = apiResponse.getData();
                    callback.onSuccess(data == null ? null : (T) data);
                } else if (apiResponse != null && apiResponse.getResult() != ApiErrorCode.SUCCESS) {
                    callback.onFailure(new ApiResponse(apiResponse.getResult()));
                }
            } else {
                ResponseBody responseBody = response.errorBody();
                ApiResponse apiResponse = JSON.parseObject(responseBody.string(), ApiResponse.class);
                callback.onFailure(new ApiResponse(apiResponse.getResult()));
            }

        } catch (Throwable ex) {
            callback.onFailure(new ApiResponse(ApiErrorCode.SYSTEM_ERROR, ex));
            Exceptions.throwIfFatal(ex);
            RxJavaPlugins.onError(ex);
            ex.printStackTrace();
        }
    }

    public abstract void onApiSuccess(T t);

    public abstract void onApiFailure(ApiResponse response);

    @Override
    public void dispose() {
        DisposableHelper.dispose(this);
    }

    @Override
    public boolean isDisposed() {
        return get() == DisposableHelper.DISPOSED;
    }
}
