package com.kms.demo.engine;

import com.kms.demo.db.NodeEntity;
import com.kms.demo.db.TransactionEntity;
import com.kms.demo.entity.Node;
import com.kms.demo.entity.Transaction;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

/**
 * @author matrixelement
 */
public interface ITransactionService {

    Single<Double> getBalance(LifecycleProvider<?> provider, String address);

    Flowable<Transaction> getTransaction(Transaction transaction);

    Flowable<String> sendTransaction(String privateKey, String ownAddress, String toAddress, String amount, String memo, long gasPrice, long gasLimit);

    TransactionReceipt sendTransaction(String signMessage, String transactionId, TransactionManager.OnTransactionStatusChangedListener listener);

    Flowable<Transaction> getTransactionList(String address);

    Single<List<Transaction>> getTransactionDetailList(LifecycleProvider<?> provider, String address);

    Flowable<Long> getLatestBlockNumber();

    Flowable<BigInteger> getEstimateGas(LifecycleProvider<?> provider, String from, String to, String memo);

    Disposable insertOrUpdate(List<TransactionEntity> transactionEntityList);

    Disposable insertOrUpdate(TransactionEntity transactionEntity);

}
