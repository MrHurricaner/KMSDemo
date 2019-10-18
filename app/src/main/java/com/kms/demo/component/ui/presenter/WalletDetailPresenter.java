package com.kms.demo.component.ui.presenter;

import android.util.Log;

import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.WalletDetailContract;
import com.kms.demo.component.ui.view.ManageWalletActivity;
import com.kms.demo.component.ui.view.ReceiveTransactionActivity;
import com.kms.demo.component.ui.view.SendTransactionActivity;
import com.kms.demo.component.ui.view.TransactionDetailActivity;
import com.kms.demo.engine.TransactionManager;
import com.kms.demo.entity.Transaction;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.NumberParserUtil;

import org.reactivestreams.Publisher;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author matrixelement
 */
public class WalletDetailPresenter extends BasePresenter<WalletDetailContract.View> implements WalletDetailContract.Presenter {

    private static final String TAG = WalletDetailPresenter.class.getSimpleName();

    private Wallet wallet;
    private Disposable getTransactionListDisposable;

    public WalletDetailPresenter(WalletDetailContract.View view) {
        super(view);
        wallet = view.getWalletFromIntent();
    }

    @Override
    public void showWalletInfo() {
        if (isViewAttached() && wallet != null) {
            //展示钱包信息
            getView().showWalletInfo(wallet);
            //获取钱包余额
            getWalletBalance();
            //获取钱包交易列表
            getWalletTransactions();
        }
    }

    @Override
    public void getWalletTransactions() {

        getTransactionListDisposable = TransactionManager.getInstance().getTransactionDetailList(getLifecycleProvider(), wallet.getWalletAddress())
                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Flowable<Object> flowable) throws Exception {
                        return flowable.delay(5, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Consumer<List<Transaction>>() {
                    @Override
                    public void accept(List<Transaction> transactionList) throws Exception {
                        if (isViewAttached() && wallet != null) {
                            getView().notifyDataChanged(transactionList, wallet.getWalletAddress());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    @Override
    public void getWalletBalance() {

        TransactionManager.getInstance().getBalance(getLifecycleProvider(), wallet.getWalletAddress())
                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Flowable<Object> flowable) throws Exception {
                        return flowable.delay(5, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double balance) throws Exception {
                        Log.e(TAG, "balance is:  " + balance);
                        if (isViewAttached()) {
                            wallet.setBalance(balance);
                            if (isViewAttached()) {
                                getView().setWalletBalance(NumberParserUtil.getPrettyDetailBalance(balance));
                            }
                        }
                    }
                });
    }

    @Override
    public void enterManageWalletPage() {
        if (isViewAttached()) {
            ManageWalletActivity.actionStartWithExtra(getContext(), wallet);
        }
    }

    @Override
    public void enterReceiveTransactionPage() {
        if (isViewAttached()) {
            ReceiveTransactionActivity.actionStartWithExtra(getContext(), wallet);
        }
    }

    @Override
    public void enterSendTransactionPage() {
        if (isViewAttached()) {
            SendTransactionActivity.actionStart(getContext(), wallet);
        }
    }

    @Override
    public void enterTransactionDetailPage(Transaction transaction) {
        if (isViewAttached() && wallet != null) {
            TransactionDetailActivity.actionStartWithExtra(getContext(), transaction, wallet.getWalletAddress(), wallet.getSmallAvatar());
        }
    }

    @Override
    public void updateWalletTransactions(Transaction transaction) {

        if (getTransactionListDisposable != null) {
            if (!getTransactionListDisposable.isDisposed()) {
                getTransactionListDisposable.dispose();
            }
        }
        getWalletTransactions();
    }
}
