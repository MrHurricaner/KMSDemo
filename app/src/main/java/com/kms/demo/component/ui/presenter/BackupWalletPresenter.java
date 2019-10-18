package com.kms.demo.component.ui.presenter;

import android.graphics.Bitmap;

import com.kms.appcore.apiservice.SchedulersTransformer;
import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.app.LoadingTransformer;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.BackupWalletContract;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.CommonUtil;
import com.kms.demo.utils.QRCodeEncoder;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class BackupWalletPresenter extends BasePresenter<BackupWalletContract.View> implements BackupWalletContract.Presenter {

    private Wallet wallet;

    public BackupWalletPresenter(BackupWalletContract.View view) {
        super(view);
        wallet = view.getWalletFromIntent();
    }

    @Override
    public void showWalletInfo() {
        if (isViewAttached() && wallet != null) {

            getView().showWalletInfo(wallet);

            Single.fromCallable(new Callable<Bitmap>() {

                @Override
                public Bitmap call() throws Exception {
                    return QRCodeEncoder.syncEncodeQRCode(wallet.getWalletAddress(), DensityUtil.dp2px(getContext(), 220f));
                }
            })
                    .compose(new SchedulersTransformer())
                    .compose(LoadingTransformer.bindToLifecycle((BaseActivity) getView()))
                    .subscribe(new Consumer<Bitmap>() {
                        @Override
                        public void accept(Bitmap bitmap) throws Exception {
                            if (isViewAttached() && bitmap != null) {
                                getView().showWalletQrCode(bitmap);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            
                        }
                    });
        }
    }

    @Override
    public void copyWalletPrivateKey() {
        if (isViewAttached()) {
            CommonUtil.copyTextToClipboard(getContext(), wallet.getWalletAddress());
        }
    }
}
