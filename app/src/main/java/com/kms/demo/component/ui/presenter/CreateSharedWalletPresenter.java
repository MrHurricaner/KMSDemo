package com.kms.demo.component.ui.presenter;

import android.text.TextUtils;

import com.kms.demo.R;
import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.CreateSharedWalletContract;
import com.kms.demo.component.ui.view.CreatingShareWalletActivity;

/**
 * @author matrixelement
 */
public class CreateSharedWalletPresenter extends BasePresenter<CreateSharedWalletContract.View> implements CreateSharedWalletContract.Presenter {

    public CreateSharedWalletPresenter(CreateSharedWalletContract.View view) {
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
    public boolean checkCreateWalletButton() {
        if (isViewAttached()) {

            String walletName = getView().getWalletName().trim();
            String password = getView().getWalletPassword().trim();
            String repeatPassword = getView().getRepeatWalletPassword().trim();

            return !TextUtils.isEmpty(walletName) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(repeatPassword) && password.length() >= 6 && repeatPassword.length() >= 6;
        }
        return false;
    }

    @Override
    public void createSharedWallet() {
        if (isViewAttached()) {
            if (checkWalletName() && checkPassword() && checkRepeatPassword()) {
                CreatingShareWalletActivity.actionStart(getContext());
            }
        }
    }
}
