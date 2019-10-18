package com.kms.demo.component.ui.contract;

import android.graphics.Bitmap;

import com.kms.demo.component.ui.base.IPresenter;
import com.kms.demo.component.ui.base.IView;
import com.kms.demo.entity.Wallet;

/**
 * @author matrixelement
 */
public class BackupWalletContract {

    public interface View extends IView {

        Wallet getWalletFromIntent();

        void showWalletInfo(Wallet wallet);

        void showWalletQrCode(Bitmap bitmap);

    }

    public interface Presenter extends IPresenter<View> {

        void showWalletInfo();

        void copyWalletPrivateKey();

    }
}
