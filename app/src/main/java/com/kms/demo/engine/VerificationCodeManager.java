package com.kms.demo.engine;

import com.kms.appcore.apiservice.ApiErrorCode;
import com.kms.appcore.apiservice.ApiResponse;
import com.kms.appcore.apiservice.SchedulersTransformer;
import com.kms.demo.component.widget.CountDownTimer;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.math.BigDecimal;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import retrofit2.Response;

/**
 * @author matrixelement
 */
public class VerificationCodeManager {

    private static final long MILLIS_IN_FUTURE_TIME = 60 * 1000;
    private static final long COUNT_DOWN_INTERVAL_TIME = 1000;

    private CountDownTimer countDownTimer;
    private OnCountDownTimerListener listener;
    private int leftMillisUntilFinished = 0;

    private VerificationCodeManager() {

        countDownTimer = new CountDownTimer(MILLIS_IN_FUTURE_TIME, COUNT_DOWN_INTERVAL_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (listener != null) {
                    leftMillisUntilFinished = BigDecimal.valueOf(millisUntilFinished).divide(BigDecimal.valueOf(1000), BigDecimal.ROUND_HALF_UP).intValue();
                    listener.onTick(leftMillisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                if (listener != null) {
                    listener.onTick(0);
                }
            }
        };
    }

    private final static class SingletonHolder {
        private final static VerificationCodeManager VERIFICATIONCODE_MANAGER = new VerificationCodeManager();
    }

    public static VerificationCodeManager getInstance() {
        return SingletonHolder.VERIFICATIONCODE_MANAGER;
    }

    public void setOnCountDownTimerListener(OnCountDownTimerListener listener) {
        this.listener = listener;
    }

    public void removeOnCountDownTimerListener() {
        this.listener = null;
    }

    public void start() {
        countDownTimer.start();
    }

    public void cancel() {
        countDownTimer.cancel();
    }

    public Single<Response<ApiResponse<Void>>> sendSmsCode(LifecycleProvider<?> provider, String phoneNumber, String operation) {
        return KYCManager.getInstance()
                .sendSmsCode(provider, phoneNumber, operation)
                .compose(new SchedulersTransformer())
                .doOnSuccess(new Consumer<Response<ApiResponse<Void>>>() {
                    @Override
                    public void accept(Response<ApiResponse<Void>> response) throws Exception {
                        if (response.isSuccessful() && response.body().getResult() == ApiErrorCode.SUCCESS) {
                            start();
                        }
                    }
                });
    }

    public interface OnCountDownTimerListener {

        void onTick(int millisLeft);
    }

}
