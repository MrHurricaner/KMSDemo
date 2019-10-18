package com.kms.demo.component.ui.contract;

import com.kms.demo.component.ui.base.IPresenter;
import com.kms.demo.component.ui.base.IView;

/**
 * @author matrixelement
 */
public class CreateSharedWalletContract {

    public interface View extends IView {

        String getWalletName();

        String getWalletPassword();

        String getRepeatWalletPassword();

        String getSignedUserNumber();

        void showWalletNameError(String errorMsg);

        void showWalletPasswordError(String errorMsg);

        void showRepeatWalletPasswordError(String errorMsg);

        void setCreateWalletButtonEnable(boolean enable);

    }

    public interface Presenter extends IPresenter<View> {

        boolean checkWalletName();

        boolean checkPassword();

        boolean checkRepeatPassword();

        boolean checkCreateWalletButton();

        void createSharedWallet();

    }
}
