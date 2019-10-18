package com.kms.demo.component.ui.presenter;

import android.text.TextUtils;

import com.kms.demo.R;
import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.JoinShareWalletContract;
import com.kms.demo.component.ui.view.WaitingCreateShareWalletActivity;

/**
 * @author matrixelement
 */
public class JoinShareWalletPresenter extends BasePresenter<JoinShareWalletContract.View> implements JoinShareWalletContract.Presenter {

    public JoinShareWalletPresenter(JoinShareWalletContract.View view) {
        super(view);
    }

    @Override
    public boolean checkWalletPassword() {
        String walletPassword = getView().getWalletPassword();
        String errMsg = null;
        if (TextUtils.isEmpty(walletPassword)) {
            errMsg = string(R.string.wallet_password_required);
        } else {
            if (walletPassword.length() < 6) {
                errMsg = string(R.string.wallet_password_bit_error);
            }
        }

        getView().showWalletPasswordError(errMsg);

        return TextUtils.isEmpty(errMsg);
    }

    @Override
    public boolean checkRepeatWalletPassword() {

        String walletPassword = getView().getWalletPassword();
        String repeatWalletPassword = getView().getRepeatWalletPassword();
        String errMsg = null;
        if (TextUtils.isEmpty(repeatWalletPassword)) {
            errMsg = string(R.string.repeat_password_confirmation_required);
        } else {
            if (!repeatWalletPassword.equals(walletPassword)) {
                errMsg = string(R.string.match_wallet_password);
            }
        }

        getView().showRepeatWalletPasswordError(errMsg);

        return TextUtils.isEmpty(errMsg);
    }

    @Override
    public boolean checkJoinShareWalletBtnEnable() {

        String walletPassword = getView().getWalletPassword();
        String repeatWalletPassword = getView().getRepeatWalletPassword();

        return !TextUtils.isEmpty(walletPassword) && !TextUtils.isEmpty(repeatWalletPassword) && walletPassword.length() >= 6 && repeatWalletPassword.length() >= 6;

    }

    @Override
    public void joinShareWallet() {
        if (isViewAttached()) {
            WaitingCreateShareWalletActivity.actionStart(getContext());
        }
    }
}
