package com.kms.demo.component.ui.presenter;

import android.util.Log;

import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.EscrowWalletContract;
import com.kms.demo.engine.KYCManager;
import com.kms.demo.engine.WalletManager;
import com.kms.demo.entity.Wallet;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class EscrowWalletPresenter extends BasePresenter<EscrowWalletContract.View> implements EscrowWalletContract.Presenter {

    private final static String TAG = EscrowWalletPresenter.class.getSimpleName();

    private final static int DEFAULT_PAGE_NO = 1;
    private final static int DEFAULT_PAGE_SIZE = 100;

    public EscrowWalletPresenter(EscrowWalletContract.View view) {
        super(view);
    }

    @Override
    public void loadWalletList() {

        if (isViewAttached()) {

            String phoneNumber = KYCManager.getInstance().getPhoneNumber();

            WalletManager.getInstance()
                    .getWalletList(getLifecycleProvider(), phoneNumber, DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE)
                    .subscribe(new Consumer<List<Wallet>>() {
                        @Override
                        public void accept(List<Wallet> walletList) throws Exception {
                            if (isViewAttached()) {
                                getView().notifyDataSetChanged(walletList);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e(TAG, throwable.getMessage());
                        }
                    });
        }
    }
}
