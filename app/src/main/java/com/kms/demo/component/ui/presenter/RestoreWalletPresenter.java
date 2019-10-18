package com.kms.demo.component.ui.presenter;

import android.text.TextUtils;

import com.kms.appcore.apiservice.ApiErrorCode;
import com.kms.appcore.apiservice.ApiResponse;
import com.kms.appcore.apiservice.SchedulersTransformer;
import com.kms.demo.R;
import com.kms.demo.app.BusinessApiSingleObserver;
import com.kms.demo.app.Constants;
import com.kms.demo.app.LoadingTransformer;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.RestoreWalletContract;
import com.kms.demo.component.ui.dialog.AuthenticationDialogFragment;
import com.kms.demo.component.ui.view.CreateOrRestoreEscrowWalletSucceedActivity;
import com.kms.demo.engine.KYCManager;
import com.kms.demo.engine.VerificationCodeManager;
import com.kms.demo.engine.WalletManager;
import com.kms.demo.entity.Wallet;
import com.trello.rxlifecycle2.LifecycleProvider;

/**
 * @author matrixelement
 */
public class RestoreWalletPresenter extends BasePresenter<RestoreWalletContract.View> implements RestoreWalletContract.Presenter {

    private Wallet wallet;

    public RestoreWalletPresenter(RestoreWalletContract.View view) {
        super(view);
        wallet = view.getWalletFromIntent();
    }

    @Override
    public void showWalletInfo() {
        if (isViewAttached() && wallet != null) {
            getView().setWalletInfo(wallet);
        }
    }

    @Override
    public boolean checkPassword() {
        if (isViewAttached()) {
            String errMsg = null;
            String password = getView().getPassword().trim();
            if (TextUtils.isEmpty(password)) {
                errMsg = string(R.string.wallet_password_required);
            } else {
                if (password.length() < 6) {
                    errMsg = string(R.string.wallet_password_bit_error);
                }
            }

            getView().showPasswordErrMsg(errMsg);

            return TextUtils.isEmpty(errMsg);
        }

        return false;
    }

    @Override
    public boolean checkRepeatPassword() {
        if (isViewAttached()) {
            String errMsg = null;
            String repeatPassword = getView().getRepeatPassword().trim();
            if (TextUtils.isEmpty(repeatPassword)) {
                errMsg = string(R.string.repeat_password_confirmation_required);
            } else {
                String password = getView().getPassword();
                if (!repeatPassword.equals(password)) {
                    errMsg = string(R.string.match_wallet_password);
                }
            }

            getView().showRepeatPasswordErrMsg(errMsg);

            return TextUtils.isEmpty(errMsg);
        }

        return false;
    }

    @Override
    public void checkRestoreWalletBtnEnable() {
        if (isViewAttached()) {
            String password = getView().getPassword().trim();
            String repeatPassword = getView().getRepeatPassword().trim();
            getView().setRestoreWalletBtnEnable(!TextUtils.isEmpty(password) && !TextUtils.isEmpty(repeatPassword) && password.length() >= 6 && repeatPassword.length() >= 6);
        }
    }

    @Override
    public void restoreWallet() {

        if (isViewAttached()) {

            if (checkPassword() && checkRepeatPassword() && wallet != null) {

                String phoneNumber = KYCManager.getInstance().getPhoneNumber();
                String password = getView().getPassword().trim();
                String countryCode = KYCManager.getInstance().getCountryCode();

                VerificationCodeManager.getInstance()
                        .sendSmsCode(getLifecycleProvider(), phoneNumber, KYCManager.KYCOperation.RECOVER_WALLET.toString())
                        .compose(LoadingTransformer.bindToLifecycle((BaseActivity) getContext()))
                        .subscribe(new BusinessApiSingleObserver(getContext()) {
                            @Override
                            public void onApiSuccess(Object o) {
                                super.onApiSuccess(o);
                                showAuthenticationDialogFragment(getLifecycleProvider(), phoneNumber, password, countryCode);
                            }

                            @Override
                            public void onApiFailure(ApiResponse response) {
                                super.onApiFailure(response);
                                if (response.getResult() == ApiErrorCode.VERIFICATION_CODE_SENT) {
                                    showAuthenticationDialogFragment(getLifecycleProvider(), phoneNumber, password, countryCode);
                                }
                            }
                        });
            }
        }
    }

    private void showAuthenticationDialogFragment(LifecycleProvider<?> provider, String phoneNumber, String password, String countryCode) {

        AuthenticationDialogFragment.getInstance(phoneNumber,countryCode, KYCManager.KYCOperation.RECOVER_WALLET).setOnVerificationCodeCompleteListener(new AuthenticationDialogFragment.OnVerificationCodeCompleteListener() {
            @Override
            public void onComplete(AuthenticationDialogFragment dialogFragment, String smsCode) {
                WalletManager.getInstance().restoreWallet(provider, wallet, phoneNumber, password, smsCode)
                        .compose(new SchedulersTransformer())
                        .compose(LoadingTransformer.bindToLifecycle((BaseActivity) getContext(), string(R.string.restoring)))
                        .subscribe(new BusinessApiSingleObserver<Wallet>(getContext()) {
                            @Override
                            public void onApiSuccess(Wallet wallet) {
                                if (isViewAttached()) {
                                    dialogFragment.dismiss();
                                    CreateOrRestoreEscrowWalletSucceedActivity.actionStart(getContext(), wallet, Constants.Action.ACTION_RESTORE_ESCROW_WALLET);
                                    ((BaseActivity) getView()).finish();
                                }
                            }

                            @Override
                            public void onApiFailure(ApiResponse response) {
                                super.onApiFailure(response);
                                if (response.getResult() == ApiErrorCode.VERIFICATION_CODE_ERR) {
                                    dialogFragment.clearVerificationCode();
                                }
                            }
                        });
            }
        }).show(((BaseActivity) getContext()).getSupportFragmentManager(), "authentication");
    }
}
