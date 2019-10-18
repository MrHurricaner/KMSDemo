package com.kms.demo.engine.api;

import com.kms.appcore.apiservice.ApiResponse;
import com.kms.demo.engine.entity.GetPkReturn;
import com.kms.demo.entity.Wallet;

import java.util.List;

import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author matrixelement
 */
public interface WalletService {


    /**
     * 创建托管钱包
     *
     * @param phone
     * @param walletName
     * @param smsCode
     * @return
     */
    @POST("app_api/createWallet")
    Single<Response<ApiResponse<Wallet>>> createTrustWallet(@Body RequestBody requestBody);

    /**
     * 恢复托管钱包
     *
     * @param phone
     * @param walletId
     * @param smsCode
     * @return
     */
    @POST("app_api/recoverWallet")
    Single<Response<ApiResponse<Void>>> recoverWallet(@Body RequestBody requestBody);
    /**
     * 获取私钥分量
     *
     * @param pk   公钥
     * @param rand 随机数
     * @return
     */
    @POST("url-http.encrypt-kms_api/v1/wallet/getPk")
    Single<Response<ApiResponse<GetPkReturn>>> getPk(@Body RequestBody requestBody);

    /**
     * 查询托管钱包列表
     *
     * @param phone
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GET("app_api/listWallet")
    Single<Response<ApiResponse<List<Wallet>>>> getWalletList(@Query("phone") String phone, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize);

}
