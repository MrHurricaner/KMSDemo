package com.kms.demo.engine.api;

import com.kms.appcore.apiservice.ApiResponse;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @author matrixelement
 */
public interface MPCService {

    /**
     * 签名申请
     *
     * @param phone
     * @param walletId
     * @param hash
     * @return
     */
    @POST("app_api/applySign")
    Single<Response<ApiResponse<String>>> applySign(@Body RequestBody requestBody);


    /**
     * 签名MPC第一步
     *
     * @param getToOthers
     * @return
     */
    @POST("app_api/signMpcStep1")
    Single<Response<ApiResponse<String>>> signMpcStep1(@Body RequestBody requestBody);


    /**
     * 签名MPC第二步
     *
     * @param getToOthers
     * @return
     */
    @POST("app_api/signMpcStep2")
    Single<Response<ApiResponse<String>>> signMpcStep2(@Body RequestBody requestBody);

}
