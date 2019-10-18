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
public interface KYCService {

    /**
     * 激活用户
     *
     * @param phone
     * @param smsCode
     * @return
     */
    @POST("app_api/userActivate")
    Single<Response<ApiResponse<Void>>> activateUser(@Body RequestBody requestBody);

    /**
     * kyc申请
     *
     * @param phone
     * @param operation
     * @return
     */
    @POST("app_api/sendSmsCode")
    Single<Response<ApiResponse<Void>>> sendSmsCode(@Body RequestBody requestBody);
}
