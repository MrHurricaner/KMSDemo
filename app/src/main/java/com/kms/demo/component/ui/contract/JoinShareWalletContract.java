package com.kms.demo.component.ui.contract;

import com.kms.demo.component.ui.base.IPresenter;
import com.kms.demo.component.ui.base.IView;

/**
 * @author matrixelement
 */
public class JoinShareWalletContract {

    public interface View extends IView {

        String getWalletPassword();

        String getRepeatWalletPassword();

        void showWalletPasswordError(String errorMsg);

        void showRepeatWalletPasswordError(String errorMsg);

        void setJoinShareWalletBtnEnable(boolean enable);

    }

    public interface Presenter extends IPresenter<View> {

        boolean checkWalletPassword();

        boolean checkRepeatWalletPassword();

        boolean checkJoinShareWalletBtnEnable();

        void joinShareWallet();

    }

}
