package com.kms.demo.component.ui.presenter;

import com.kms.appcore.apiservice.FlowableSchedulersTransformer;
import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.TransactionDetailContract;
import com.kms.demo.engine.TransactionManager;
import com.kms.demo.entity.Transaction;
import com.kms.demo.entity.TransactionStatus;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class TransactionDetailPresenter extends BasePresenter<TransactionDetailContract.View> implements TransactionDetailContract.Persenter {

    private Transaction transaction;
    private String walletAddress;
    private String walletAvatar;

    public TransactionDetailPresenter(TransactionDetailContract.View view) {
        super(view);
        transaction = view.getTransactionFromIntent();
        walletAddress = view.getWalletAddress();
        walletAvatar = view.getWalletAvatar();
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void loadTransactionDetail() {
        if (isViewAttached() && transaction != null) {
            getView().setTransactionWalletAvatar(transaction.getWalletName(), walletAvatar);
            getView().showTransactionDetail(transaction, walletAddress);
        }
    }

    @Override
    public void updateTransactionDetail(Transaction tran) {
        if (isViewAttached()) {
            if (tran != null && transaction != null && tran.getId().equals(transaction.getId())) {
                updateTransactionDetail(tran, walletAddress);
            }
        }
    }

    @Override
    public void refreshTransactionDetail() {
        if (isViewAttached() && transaction != null) {
            TransactionStatus status = transaction.getTransactionStatus();
            if (status == TransactionStatus.PENDING_FAILED) {
                Flowable
                        .interval(1, TimeUnit.SECONDS)
                        .flatMap(new Function<Long, Publisher<Transaction>>() {
                            @Override
                            public Publisher<Transaction> apply(Long aLong) throws Exception {
                                return TransactionManager.getInstance().getTransaction(transaction);
                            }
                        })
                        .takeUntil(new Predicate<Transaction>() {
                            @Override
                            public boolean test(Transaction transaction) throws Exception {
                                return transaction.getSignedBlockNumber() >= TransactionManager.DEFAULT_SIGNED_BLOCK_NUMBER;
                            }
                        })
                        .doOnNext(new Consumer<Transaction>() {
                            @Override
                            public void accept(Transaction transaction) throws Exception {

                                if (transaction.getSignedBlockNumber() >= TransactionManager.DEFAULT_SIGNED_BLOCK_NUMBER) {
                                    transaction.setTransactionStatusCode(TransactionStatus.COMPLETED.statusCode);
                                    transaction.setEndTime(System.currentTimeMillis());
                                } else {
                                    transaction.setTransactionStatusCode(TransactionStatus.PENDING.statusCode);
                                }

                                TransactionManager.getInstance().insertOrUpdate(transaction.parseTransactionEntity());
                            }
                        })
                        .compose(new FlowableSchedulersTransformer())
                        .compose(bindToLifecycle())
                        .subscribe(new Consumer<Transaction>() {
                            @Override
                            public void accept(Transaction transaction) throws Exception {
                                if (isViewAttached()) {
                                    updateTransactionDetail(transaction, walletAddress);
                                }
                            }
                        });
            }
        }
    }

    private void updateTransactionDetail(Transaction transaction, String walletAddress) {
        this.transaction = transaction;
        getView().showTransactionDetail(transaction, walletAddress);
    }
}
