package com.kms.demo.component.ui.contract;

import com.kms.demo.component.ui.base.IPresenter;
import com.kms.demo.component.ui.base.IView;
import com.kms.demo.entity.Wallet;

/**
 * @author matrixelement
 */
public class RestoreWalletContract {

    public interface View extends IView {

        Wallet getWalletFromIntent();

        String getPassword();

        String getRepeatPassword();

        void showPasswordErrMsg(String errMsg);

        void showRepeatPasswordErrMsg(String errMsg);

        void setRestoreWalletBtnEnable(boolean enable);

        void setWalletInfo(Wallet wallet);
    }

    public interface Presenter extends IPresenter<View> {

        void showWalletInfo();

        boolean checkPassword();

        boolean checkRepeatPassword();

        void checkRestoreWalletBtnEnable();

        void restoreWallet();

    }
}
