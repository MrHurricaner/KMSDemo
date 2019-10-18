package com.kms.demo.component.ui.presenter;

import android.text.TextUtils;

import com.kms.appcore.apiservice.ApiErrorCode;
import com.kms.appcore.apiservice.ApiResponse;
import com.kms.demo.R;
import com.kms.demo.app.BusinessApiSingleObserver;
import com.kms.demo.app.Constants;
import com.kms.demo.app.LoadingTransformer;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.CreateEscrowWalletContract;
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
public class CreateEscrowWalletPresenter extends BasePresenter<CreateEscrowWalletContract.View> implements CreateEscrowWalletContract.Presenter {

    public CreateEscrowWalletPresenter(CreateEscrowWalletContract.View view) {
        super(view);
    }

    @Override
    public boolean checkWalletName() {

        String errorMsg = null;

        String walletName = getView().getWalletName().trim();
        if (TextUtils.isEmpty(walletName)) {
            errorMsg = string(R.string.wallet_name_required);
        } else {
            if (walletName.length() > 16) {
                errorMsg = string(R.string.wallet_name_bit_error);
            }
        }

        getView().showWalletNameError(errorMsg);

        return TextUtils.isEmpty(errorMsg);
    }

    @Override
    public boolean checkPassword() {

        String errorMsg = null;

        String password = getView().getWalletPassword().trim();
        if (TextUtils.isEmpty(password)) {
            errorMsg = string(R.string.wallet_password_required);
        } else {
            if (password.length() < 6) {
                errorMsg = string(R.string.wallet_password_bit_error);
            }

        }

        getView().showWalletPasswordError(errorMsg);

        return TextUtils.isEmpty(errorMsg);
    }

    @Override
    public boolean checkRepeatPassword() {

        String errorMsg = null;

        String repeatPassword = getView().getRepeatWalletPassword().trim();
        String password = getView().getWalletPassword().trim();

        if (TextUtils.isEmpty(repeatPassword)) {
            errorMsg = string(R.string.repeat_password_confirmation_required);
        } else {
            if (!repeatPassword.equals(password)) {
                errorMsg = string(R.string.match_wallet_password);
            }
        }

        getView().showRepeatWalletPasswordError(errorMsg);

        return TextUtils.isEmpty(errorMsg);
    }

    @Override
    public void checkCreateWalletButton() {
        if (isViewAttached()) {

            String walletName = getView().getWalletName().trim();
            String password = getView().getWalletPassword().trim();
            String repeatPassword = getView().getRepeatWalletPassword().trim();

            getView().setCreateWalletButtonEnable(!TextUtils.isEmpty(walletName) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(repeatPassword) && password.length() >= 6 && repeatPassword.length() >= 6);
        }
    }

    @Override
    public void createEscrowWallet() {
        if (isViewAttached()) {

            if (checkWalletName() && checkPassword() && checkRepeatPassword()) {

                String phoneNumber = KYCManager.getInstance().getPhoneNumber();
                String countryCode = KYCManager.getInstance().getCountryCode();
                String walletName = getView().getWalletName().trim();
                String password = getView().getWalletPassword().trim();

                VerificationCodeManager.getInstance()
                        .sendSmsCode(getLifecycleProvider(), phoneNumber, KYCManager.KYCOperation.CREATE_WALLET.toString())
                        .compose(LoadingTransformer.bindToLifecycle((BaseActivity) getContext()))
                        .subscribe(new BusinessApiSingleObserver(getContext()) {
                            @Override
                            public void onApiSuccess(Object o) {
                                super.onApiSuccess(o);
                                showAuthenticationDialogFragment(getLifecycleProvider(), phoneNumber, walletName, password,countryCode);
                            }

                            @Override
                            public void onApiFailure(ApiResponse response) {
                                super.onApiFailure(response);
                                if (response.getResult() == ApiErrorCode.VERIFICATION_CODE_SENT) {
                                    showAuthenticationDialogFragment(getLifecycleProvider(), phoneNumber, walletName, password,countryCode);
                                }
                            }
                        });
            }
        }
    }

    private void showAuthenticationDialogFragment(LifecycleProvider<?> provider, String phoneNumber, String walletName, String password, String countryCode) {

        AuthenticationDialogFragment.getInstance(phoneNumber,countryCode, KYCManager.KYCOperation.CREATE_WALLET).setOnVerificationCodeCompleteListener(new AuthenticationDialogFragment.OnVerificationCodeCompleteListener() {
            @Override
            public void onComplete(AuthenticationDialogFragment dialogFragment, String smsCode) {
                WalletManager.getInstance().createTrustWallet(provider, phoneNumber, walletName, password, smsCode, String.valueOf(System.currentTimeMillis()))
                        .compose(LoadingTransformer.bindToLifecycle((BaseActivity) getContext(), string(R.string.creating)))
                        .subscribe(new BusinessApiSingleObserver<Wallet>(getContext()) {
                            @Override
                            public void onApiSuccess(Wallet wallet) {
                                if (isViewAttached()) {
                                    dialogFragment.dismiss();
                                    CreateOrRestoreEscrowWalletSucceedActivity.actionStart(getContext(), wallet, Constants.Action.ACTION_CREATE_ESCROW_WALLET);
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
