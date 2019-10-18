package com.kms.demo.component.ui.presenter;

import android.graphics.Bitmap;

import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.WalletPublicKeyContract;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.QRCodeEncoder;

import org.reactivestreams.Subscription;

import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class WalletPublicKeyPresenter extends BasePresenter<WalletPublicKeyContract.View> implements WalletPublicKeyContract.Presenter {

    private Wallet wallet;

    public WalletPublicKeyPresenter(WalletPublicKeyContract.View view) {
        super(view);
        wallet = view.getWalletFromIntent();
    }

    @Override
    public void showWalletInfo() {
        
        if (isViewAttached() && wallet != null) {

            getView().showWalletInfo(wallet);

            Flowable.fromCallable(new Callable<Bitmap>() {

                @Override
                public Bitmap call() throws Exception {
                    return QRCodeEncoder.syncEncodeQRCode(wallet.getPk(), DensityUtil.dp2px(getContext(), 220f));
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe(new Consumer<Subscription>() {
                @Override
                public void accept(Subscription subscription) throws Exception {
                    showLoadingDialog();
                }
            }).doOnTerminate(new Action() {
                @Override
                public void run() throws Exception {
                    dismissLoadingDialogImmediately();
                }
            }).subscribe(new Consumer<Bitmap>() {
                @Override
                public void accept(Bitmap bitmap) throws Exception {
                    if (isViewAttached() && bitmap != null) {
                        getView().showWalletQrCode(bitmap);
                    }
                }
            });
        }
    }

    @Override
    public String getPk() {
        return wallet == null ? null : wallet.getPk();
    }
}
