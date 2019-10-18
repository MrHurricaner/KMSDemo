package com.kms.demo.engine;

import android.os.Parcel;
import android.os.Parcelable;

import com.kms.appcore.apiservice.ApiRequestBody;
import com.kms.appcore.apiservice.ApiResponse;
import com.kms.appcore.apiservice.EmptySingleTransformer;
import com.kms.appcore.apiservice.HttpClient;
import com.kms.appcore.apiservice.SchedulersTransformer;
import com.kms.demo.engine.api.KYCService;
import com.trello.rxlifecycle2.LifecycleProvider;

import io.reactivex.Single;
import retrofit2.Response;

/**
 * @author matrixelement
 */
public class KYCManager {

    private String phoneNumber;
    private String countryCode;

    private KYCManager() {

    }

    private static class SingletonHolder {
        private static KYCManager KYC_MANAGER = new KYCManager();
    }

    public static KYCManager getInstance() {
        return SingletonHolder.KYC_MANAGER;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    /**
     * 发送验证码
     *
     * @param provider
     * @param phoneNumber
     * @param operation
     * @return
     */
    public Single<Response<ApiResponse<Void>>> sendSmsCode(LifecycleProvider<?> provider, String phoneNumber, String operation) {
        return HttpClient.getInstance().createService(KYCService.class)
                .sendSmsCode(ApiRequestBody.newBuilder().put("phone", phoneNumber).put("operation", operation).build())
                .compose(new SchedulersTransformer())
                .compose(provider != null ? provider.bindToLifecycle() : EmptySingleTransformer.get());
    }

    /**
     * 发送验证码
     *
     * @param provider
     * @param phoneNumber
     * @param smsCode
     * @return
     */
    public Single<Response<ApiResponse<Void>>> activateUser(LifecycleProvider<?> provider, String phoneNumber, String smsCode) {
        return HttpClient.getInstance().createService(KYCService.class)
                .activateUser(ApiRequestBody.newBuilder()
                        .put("phone", phoneNumber)
                        .put("smsCode", smsCode).build())
                .compose(new SchedulersTransformer())
                .compose(provider != null ? provider.bindToLifecycle() : EmptySingleTransformer.get());
    }

    public enum KYCOperation implements Parcelable {

        ACTIVATE_USER("userActivate"), CREATE_WALLET("createWallet"), RECOVER_WALLET("recoverWallet");

        private String desc;

        KYCOperation(String desc) {
            this.desc = desc;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(desc);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<KYCOperation> CREATOR = new Creator<KYCOperation>() {
            @Override
            public KYCOperation createFromParcel(Parcel in) {
                return KYCOperation.valueOf(in.readString());
            }

            @Override
            public KYCOperation[] newArray(int size) {
                return new KYCOperation[size];
            }
        };

        @Override
        public String toString() {
            return desc;
        }
    }


}
