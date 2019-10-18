package com.kms.demo.component.ui.presenter;

import android.text.TextUtils;

import com.kms.appcore.apiservice.ApiErrorCode;
import com.kms.appcore.apiservice.ApiResponse;
import com.kms.demo.R;
import com.kms.demo.app.BusinessApiSingleObserver;
import com.kms.demo.app.LoadingTransformer;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.ActivateAccountContract;
import com.kms.demo.component.ui.view.MainActivity;
import com.kms.demo.engine.KYCManager;
import com.kms.demo.entity.Country;
import com.kms.demo.utils.PhoneUtil;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.realm.Realm;
import io.realm.RealmObject;

/**
 * @author matrixelement
 */
public class ActivateAccountPresenter extends BasePresenter<ActivateAccountContract.View> implements ActivateAccountContract.Presenter {

    private final static String DEFAULT_COUNTRY_CODE = "86";

    private Realm realm;
    private String countryCode;

    public ActivateAccountPresenter(ActivateAccountContract.View view) {
        super(view);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void activateAccount() {
        if (isViewAttached()) {
            if (checkParams()) {

                String phoneNumber = getView().getPhoneNumber().trim();
                String smsCode = getView().getSMSCode().trim();
                String countryCode = getView().getCountryCode().trim();

                KYCManager.getInstance().activateUser(getLifecycleProvider(), phoneNumber, smsCode).compose(LoadingTransformer.bindToLifecycle((BaseActivity) getView())).subscribe(new BusinessApiSingleObserver<Void>(getContext()) {
                    @Override
                    public void onApiSuccess(Void value) {
                        if (isViewAttached()) {
                            KYCManager.getInstance().setPhoneNumber(phoneNumber);
                            KYCManager.getInstance().setCountryCode(countryCode);
                            MainActivity.actionStartWithClearTask(getContext());
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        super.onApiFailure(response);
                        if (response.getResult() == ApiErrorCode.VERIFICATION_CODE_ERR) {
                            if (isViewAttached()) {
                                getView().showSMSCodeError(string(R.string.verification_code_error));
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean checkParams() {
        return checkPhoneNumber() && checkSMSCode();
    }

    @Override
    public void checkActivateButton() {
        if (isViewAttached()) {

            String phoneNumber = getView().getPhoneNumber().trim();
            String smsCode = getView().getSMSCode().trim();

            getView().setActivateButtonEnable(!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(smsCode));
        }
    }

    @Override
    public void updateCountryCode(String countryCode) {
        this.countryCode = countryCode;
        if (isViewAttached()) {
            getView().updateCountryCode(String.format("+%s", countryCode));
        }
    }

    @Override
    public void loadCurrentCountryCode() {

        String countryShortEnName = Locale.CHINA.getLanguage();

        realm.where(Country.class).equalTo("countryShortEnName", countryShortEnName).findFirstAsync().asFlowable().filter(new Predicate<RealmObject>() {
            @Override
            public boolean test(RealmObject realmObject) throws Exception {
                return realmObject.isLoaded();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<RealmObject>() {
            @Override
            public void accept(RealmObject realmObject) throws Exception {
                if (isViewAttached()) {
                    updateCountryCode(String.valueOf(((Country) realmObject).getCountryCode()));
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (isViewAttached()) {
                    updateCountryCode(DEFAULT_COUNTRY_CODE);
                }
            }
        });

    }

    @Override
    public void getSMSCode() {

        if (checkPhoneNumber()) {

            String phoneNumber = getView().getPhoneNumber();

            KYCManager.getInstance().sendSmsCode(getLifecycleProvider(), phoneNumber, KYCManager.KYCOperation.ACTIVATE_USER.toString())
                    .compose(LoadingTransformer.bindToLifecycle((BaseActivity) getView()))
                    .subscribe(new BusinessApiSingleObserver<Void>(getContext()) {
                        @Override
                        public void onApiSuccess(Void aVoid) {
                            if (isViewAttached()) {
                                getView().startCountDown();
                            }
                        }
                    });

        }

    }

    @Override
    public boolean checkPhoneNumber() {
        String phoneNumber = getView().getPhoneNumber().trim();
        String errMsg = null;
        if (TextUtils.isEmpty(phoneNumber)) {
            errMsg = string(R.string.phone_number_required);
        } else {
            if (!PhoneUtil.checkPhoneNumber(phoneNumber, countryCode)) {
                errMsg = string(R.string.invalid_phone_number);
            }
        }

        getView().showPhoneNumberError(errMsg);

        return TextUtils.isEmpty(errMsg);
    }

    @Override
    public boolean checkSMSCode() {

        String smsCode = getView().getSMSCode().trim();
        String errMsg = null;
        if (TextUtils.isEmpty(smsCode)) {
            errMsg = string(R.string.sms_code_required);
        }

        getView().showSMSCodeError(errMsg);

        return TextUtils.isEmpty(errMsg);
    }

    @Override
    public void detachView() {
        super.detachView();
        realm.close();
    }
}
