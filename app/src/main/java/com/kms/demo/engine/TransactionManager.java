package com.kms.demo.engine;

import android.text.TextUtils;
import android.util.Log;

import com.kms.appcore.apiservice.EmptyFlowableTransformer;
import com.kms.appcore.apiservice.EmptySingleTransformer;
import com.kms.appcore.apiservice.FlowableSchedulersTransformer;
import com.kms.appcore.apiservice.SchedulersTransformer;
import com.kms.demo.config.AppSettings;
import com.kms.demo.db.TransactionDao;
import com.kms.demo.db.TransactionEntity;
import com.kms.demo.entity.TransactionStatus;
import com.kms.demo.entity.Wallet;
import com.kms.demo.event.EventPublisher;
import com.kms.demo.utils.BigDecimalUtil;
import com.kms.demo.utils.NumericUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.reactivestreams.Publisher;
import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class TransactionManager implements ITransactionService {

    private final static String TAG = TransactionManager.class.getSimpleName();
    public final static int DEFAULT_SIGNED_BLOCK_NUMBER = 12;

    private Web3j web3j;

    private static class SingletonHolder {
        private static TransactionManager TRANSACTION_MANAGER = new TransactionManager();
    }

    public static TransactionManager getInstance() {
        return TransactionManager.SingletonHolder.TRANSACTION_MANAGER;
    }

    public void init() {

        if (AppSettings.getInstance().getFirstEnter()) {
            NodeManager.getInstance()
                    .insertDefaultNodeAndGetUrl()
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String url) throws Exception {
                            setWeb3jUrl(url);
//                            AppSettings.getInstance().setFirstEnter(false);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e(TAG, throwable.getMessage());
                        }
                    });
        } else {
            NodeManager.getInstance().getUrl().subscribe(new Consumer<String>() {
                @Override
                public void accept(String url) throws Exception {
                    setWeb3jUrl(url);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Log.e(TAG, throwable.getMessage());
                }
            });
        }

    }

    public void setWeb3jUrl(String url) {
        web3j = Web3jFactory.build(new HttpService(url));
    }

    public void send(Wallet wallet, TransactionEntity transactionEntity, String sk, String to, String amount, String note, long gasPrice, long gasLimit, String transactionId) {

        if (wallet == null) {
            return;
        }

        String fromAddress = wallet.getWalletAddress();
        String walletId = wallet.getId();
        String phoneNumber = wallet.getUserPhone();
        String multPk = wallet.getMultPk();
        String multSk = wallet.getMultSk();
        String publicKey = wallet.getPk();
        com.kms.demo.entity.Transaction transaction = transactionEntity.parseTransaction();

        Single.just(TransactionDao.insertTransaction(transactionEntity)).filter(new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean success) throws Exception {
                return success;
            }
        }).map(new Function<Boolean, RawTransaction>() {

            @Override
            public RawTransaction apply(Boolean aBoolean) throws Exception {
                return TransactionManager.getInstance().createRawTransaction(fromAddress, to, amount, note, gasPrice, gasLimit);
            }
        })
                .toSingle()
                .flatMap(new Function<RawTransaction, SingleSource<String>>() {
                    @Override
                    public SingleSource<String> apply(RawTransaction rawTransaction) throws Exception {
                        return MPCManager.getInstance().signMessage(rawTransaction, phoneNumber, walletId, publicKey, sk, multPk, multSk, transactionId);
                    }
                })
                .map(new Function<String, TransactionReceipt>() {

                    @Override
                    public TransactionReceipt apply(String signMessage) throws Exception {
                        return TransactionManager.getInstance().sendTransaction(signMessage, transactionId, new OnTransactionStatusChangedListener() {
                            @Override
                            public void OnTransactionStatusChanged(TransactionStatus transactionStatus, String transactionHash) {
                                updateTransaction(transactionId, transactionHash, transactionStatus);
                            }
                        });
                    }
                })
                .filter(new Predicate<TransactionReceipt>() {
                    @Override
                    public boolean test(TransactionReceipt transactionReceipt) throws Exception {
                        return transactionReceipt != null;
                    }
                })
                .toFlowable()
                .flatMap(new Function<TransactionReceipt, Publisher<com.kms.demo.entity.Transaction>>() {
                    @Override
                    public Publisher<com.kms.demo.entity.Transaction> apply(TransactionReceipt transactionReceipt) throws Exception {
                        long blockNumber = NumericUtil.decodeQuantity(transactionReceipt.getBlockNumberRaw(), BigInteger.ZERO).longValue();
                        String hash = transactionReceipt.getTransactionHash();
                        return Flowable.interval(1, TimeUnit.SECONDS).flatMap(new Function<Long, Publisher<Long>>() {
                            @Override
                            public Publisher<Long> apply(Long aLong) throws Exception {
                                return getLatestBlockNumber();
                            }
                        }).doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                TransactionManager.getInstance().updateTransaction(transactionId, hash, TransactionStatus.PENDING_FAILED);
                            }
                        }).takeUntil(new Predicate<Long>() {
                            @Override
                            public boolean test(Long latestBlockNumber) throws Exception {
                                return latestBlockNumber - blockNumber >= DEFAULT_SIGNED_BLOCK_NUMBER;
                            }
                        }).map(new Function<Long, com.kms.demo.entity.Transaction>() {
                            @Override
                            public com.kms.demo.entity.Transaction apply(Long latestBlockNumber) throws Exception {
                                TransactionStatus transactionStatus = latestBlockNumber - blockNumber >= DEFAULT_SIGNED_BLOCK_NUMBER ? TransactionStatus.COMPLETED : TransactionStatus.PENDING;
                                transaction.setBlockNumber(blockNumber);
                                transaction.setLatestBlockNumber(latestBlockNumber);
                                transaction.setHash(hash);
                                transaction.setTransactionStatusCode(transactionStatus.statusCode);
                                if (transactionStatus == TransactionStatus.COMPLETED) {
                                    transaction.setEndTime(System.currentTimeMillis());
                                }
                                return transaction;
                            }
                        }).doOnNext(new Consumer<com.kms.demo.entity.Transaction>() {
                            @Override
                            public void accept(com.kms.demo.entity.Transaction transaction) throws Exception {
                                //存入数据库
                                TransactionDao.insertTransaction(transaction.parseTransactionEntity());
                                EventPublisher.getInstance().sendTransactionStatusChangedEvent(transaction);
                            }
                        });
                    }
                })
                .compose(new FlowableSchedulersTransformer())
                .subscribe(new Consumer<com.kms.demo.entity.Transaction>() {
                    @Override
                    public void accept(com.kms.demo.entity.Transaction transaction) throws Exception {
                        Log.e(TAG, transaction.toString());
                    }
                });
    }

    @Override
    public Single<Double> getBalance(LifecycleProvider<?> provider, String address) {
        return getBalanceByAddress(address)
                .compose(new SchedulersTransformer())
                .compose(provider == null ? EmptySingleTransformer.get() : provider.bindToLifecycle());
    }

    @Override
    public Flowable<com.kms.demo.entity.Transaction> getTransaction(com.kms.demo.entity.Transaction transaction) {
        return Flowable.fromCallable(new Callable<com.kms.demo.entity.Transaction>() {
            @Override
            public com.kms.demo.entity.Transaction call() throws Exception {
                return getTransactionByHash(transaction);
            }
        }).zipWith(getLatestBlockNumber(), new BiFunction<com.kms.demo.entity.Transaction, Long, com.kms.demo.entity.Transaction>() {
            @Override
            public com.kms.demo.entity.Transaction apply(com.kms.demo.entity.Transaction transaction, Long latestBlockNumber) throws Exception {
                return transaction.updateLatestBlockNumber(latestBlockNumber);
            }
        });

    }

    @Override
    public Flowable<String> sendTransaction(String privateKey, String ownAddress, String toAddress, String amount, String memo, long gasPrice, long gasLimit) {
        return Flowable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return send(privateKey, ownAddress, toAddress, amount, memo, gasPrice, gasLimit);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public TransactionReceipt sendTransaction(String signMessage, String transactionId, TransactionManager.OnTransactionStatusChangedListener listener) {

        String transactionHash = null;
        TransactionReceipt transactionReceipt = null;
        try {
            transactionHash = web3j.ethSendRawTransaction(signMessage).send().getTransactionHash();
            if (TextUtils.isEmpty(transactionHash)) {
                listener.OnTransactionStatusChanged(TransactionStatus.FAILED, null);
            } else {
                listener.OnTransactionStatusChanged(TransactionStatus.PENDING, transactionHash);

                TransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(
                        web3j, org.web3j.tx.TransactionManager.DEFAULT_POLLING_FREQUENCY, org.web3j.tx.TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
                transactionReceipt = transactionReceiptProcessor.waitForTransactionReceipt(transactionHash);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransactionException e) {
            e.printStackTrace();
        } finally {
            if (transactionReceipt == null) {
                listener.OnTransactionStatusChanged(TransactionStatus.FAILED, transactionHash);
            }
        }

        return transactionReceipt;
    }

    @Override
    public Flowable<com.kms.demo.entity.Transaction> getTransactionList(String address) {
        return Flowable.fromIterable(TransactionDao.getTransactionList(address)).map(new Function<TransactionEntity, com.kms.demo.entity.Transaction>() {

            @Override
            public com.kms.demo.entity.Transaction apply(TransactionEntity transactionEntity) throws Exception {
                return transactionEntity.parseTransaction();
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<List<com.kms.demo.entity.Transaction>> getTransactionDetailList(LifecycleProvider<?> provider, String address) {

        return Flowable.fromCallable(new Callable<List<TransactionEntity>>() {
            @Override
            public List<TransactionEntity> call() throws Exception {
                return TransactionDao.getTransactionList(address);
            }
        })
                .flatMap(new Function<List<TransactionEntity>, Publisher<TransactionEntity>>() {
                    @Override
                    public Publisher<TransactionEntity> apply(List<TransactionEntity> transactionEntities) throws Exception {
                        return Flowable.fromIterable(transactionEntities);
                    }
                })
                .flatMap(new Function<TransactionEntity, Publisher<com.kms.demo.entity.Transaction>>() {
                    @Override
                    public Publisher<com.kms.demo.entity.Transaction> apply(TransactionEntity transactionEntity) throws Exception {
                        com.kms.demo.entity.Transaction transaction = transactionEntity.parseTransaction();
                        TransactionStatus status = transaction.getTransactionStatus();
                        if (status == TransactionStatus.PENDING_FAILED) {
                            return getTransaction(transaction);
                        } else {
                            return Flowable.just(transaction);
                        }
                    }
                })
                .doOnNext(new Consumer<com.kms.demo.entity.Transaction>() {
                    @Override
                    public void accept(com.kms.demo.entity.Transaction transaction) throws Exception {

                        if (transaction.getTransactionStatus() == TransactionStatus.PENDING_FAILED) {

                            Log.e(TAG, "doOnNext:   " + transaction.toString());

                            if (transaction.getSignedBlockNumber() >= TransactionManager.DEFAULT_SIGNED_BLOCK_NUMBER) {
                                transaction.setTransactionStatusCode(TransactionStatus.COMPLETED.statusCode);
                                transaction.setEndTime(System.currentTimeMillis());
                            }

                            TransactionManager.getInstance().insertOrUpdate(transaction.parseTransactionEntity());
                        }
                    }
                })
                .collect(new Callable<List<com.kms.demo.entity.Transaction>>() {

                    @Override
                    public List<com.kms.demo.entity.Transaction> call() throws Exception {
                        return new ArrayList<com.kms.demo.entity.Transaction>();
                    }
                }, new BiConsumer<List<com.kms.demo.entity.Transaction>, com.kms.demo.entity.Transaction>() {
                    @Override
                    public void accept(List<com.kms.demo.entity.Transaction> transactionList, com.kms.demo.entity.Transaction transaction) throws Exception {
                        transactionList.add(transaction);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(provider == null ? EmptySingleTransformer.get() : provider.bindToLifecycle());

    }


    @Override
    public Flowable<BigInteger> getEstimateGas(LifecycleProvider<?> provider, String from, String to, String memo) {
        return Flowable.fromCallable(new Callable<BigInteger>() {
            @Override
            public BigInteger call() throws Exception {
                return getGas(from, to, memo);
            }
        }).compose(new FlowableSchedulersTransformer())
                .compose(provider == null ? EmptyFlowableTransformer.get() : provider.bindToLifecycle());
    }

    @Override
    public Disposable insertOrUpdate(List<TransactionEntity> transactionEntityList) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return TransactionDao.insertTransaction(transactionEntityList);
            }
        }).compose(new SchedulersTransformer()).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {

            }
        });
    }

    @Override
    public Disposable insertOrUpdate(TransactionEntity transactionEntity) {

        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return TransactionDao.insertTransaction(transactionEntity);
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

            }
        });
    }

    /**
     * 更新transaction，包括两种情况 hash为空和hash不为空
     *
     * @param transactionId
     * @param hash
     * @param transactionStatus
     */
    public void updateTransaction(String transactionId, String hash, TransactionStatus transactionStatus) {

        Log.e(TAG, transactionStatus.toString());

        if (TextUtils.isEmpty(hash)) {
            Single.fromCallable(new Callable<Boolean>() {

                @Override
                public Boolean call() throws Exception {
                    return TransactionDao.updateTransaction(transactionId, hash, transactionStatus.statusCode);
                }
            }).map(new Function<Boolean, com.kms.demo.entity.Transaction>() {
                @Override
                public com.kms.demo.entity.Transaction apply(Boolean aBoolean) throws Exception {
                    return TransactionDao.getTransactionById(transactionId).parseTransaction();
                }
            }).compose(new SchedulersTransformer()).subscribe(new Consumer<com.kms.demo.entity.Transaction>() {
                @Override
                public void accept(com.kms.demo.entity.Transaction transaction) throws Exception {
                    EventPublisher.getInstance().sendTransactionStatusChangedEvent(transaction);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {

                }
            });
        } else {
            Flowable.fromCallable(new Callable<com.kms.demo.entity.Transaction>() {
                @Override
                public com.kms.demo.entity.Transaction call() throws Exception {
                    com.kms.demo.entity.Transaction transaction = TransactionDao.getTransactionById(transactionId).parseTransaction();
                    transaction.setHash(hash);
                    transaction.setTransactionStatusCode(transactionStatus.statusCode);
                    return transaction;
                }
            })
                    .doOnNext(new Consumer<com.kms.demo.entity.Transaction>() {
                        @Override
                        public void accept(com.kms.demo.entity.Transaction transaction) throws Exception {
                            TransactionDao.insertTransaction(transaction.parseTransactionEntity());
                        }
                    })
                    .compose(new FlowableSchedulersTransformer())
                    .subscribe(new Consumer<com.kms.demo.entity.Transaction>() {
                        @Override
                        public void accept(com.kms.demo.entity.Transaction transaction) throws Exception {
                            EventPublisher.getInstance().sendTransactionStatusChangedEvent(transaction);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e(TAG, throwable.getMessage());
                        }
                    });

        }

    }

    private Single<Double> getBalanceByAddress(String address) {
        return Single.fromCallable(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                double balance = 0D;
                EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
                if (ethGetBalance != null) {
                    balance = BigDecimalUtil.div(ethGetBalance.getBalance().toString(), "1E18");
                }
                return balance;
            }
        }).onErrorReturnItem(0D);
    }

    private com.kms.demo.entity.Transaction getTransactionByHash(com.kms.demo.entity.Transaction t) {

        try {

            String id = t.getId();
            String transactionHash = t.getHash();
            long createTime = t.getCreateTime();
            String walletName = t.getWalletName();
            String note = t.getNote();
            int avatar = t.getAvatar();
            int transactionStatusCode = t.getTransactionStatusCode();
            long latestBlockNumber = t.getLatestBlockNumber();

            Transaction transaction = web3j.ethGetTransactionByHash(transactionHash).send().getTransaction();

            if (transaction != null) {

                Log.e(TAG, "latestBlockNumber is: " + latestBlockNumber + "blockNumber is: " + NumericUtil.decodeQuantity(transaction.getBlockNumberRaw(), BigInteger.ZERO).longValue());

                return new com.kms.demo.entity.Transaction.Builder(id, createTime, walletName)
                        .hash(transactionHash)
                        .avatar(avatar)
                        .fromAddress(transaction.getFrom())
                        .toAddress(transaction.getTo())
                        .latestBlockNumber(latestBlockNumber)
                        .sendAmount(BigDecimalUtil.div(transaction.getValue().toString(), "1E18"))
                        .blockNumber(NumericUtil.decodeQuantity(transaction.getBlockNumberRaw(), BigInteger.ZERO).longValue())
                        .feeAmount(BigDecimalUtil.div(BigDecimalUtil.mul(transaction.getGas().doubleValue(), transaction.getGasPrice().doubleValue()), 1E18))
                        .note(note)
                        .transactionStatusCode(transactionStatusCode)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public RawTransaction createRawTransaction(String from, String toAddress, String amount, String
            note, long gasPrice, long gasLimit) {

        BigInteger GAS_PRICE = BigInteger.valueOf(gasPrice);
        BigInteger GAS_LIMIT = BigInteger.valueOf(gasLimit);

        BigInteger value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();

        String hexNote = Hex.toHexString(note.getBytes(Charset.forName("UTF-8")));

        RawTransaction rawTransaction = RawTransaction.createTransaction(getNonce(from), GAS_PRICE, GAS_LIMIT, toAddress, value, hexNote);

        return rawTransaction;
    }

    private String send(String privateKey, String from, String toAddress, String amount, String
            memo, long gasPrice, long gasLimit) {

        BigInteger GAS_PRICE = BigInteger.valueOf(gasPrice);
        BigInteger GAS_LIMIT = BigInteger.valueOf(gasLimit);

        Credentials credentials = Credentials.create(privateKey);
        try {

            BigInteger value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();

            RawTransaction rawTransaction = RawTransaction.createTransaction(getNonce(from), GAS_PRICE, GAS_LIMIT, toAddress, value, NumericUtil.encode(memo));

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

            String hexValue = Numeric.toHexString(signedMessage);

            return web3j.ethSendRawTransaction(hexValue).send().getTransactionHash();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private BigInteger getGas(String from, String to, String note) {

        BigInteger gas = BigInteger.ZERO;
        try {
            String hexText = Hex.toHexString(note.getBytes(Charset.forName("UTF-8")));
            EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(from, to, hexText)).send();
            gas = NumericUtil.decodeQuantity(ethEstimateGas.getResult(), BigInteger.ZERO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return gas;
    }

    private BigInteger getNonce(String from) {

        BigInteger nonce = BigInteger.ZERO;
        try {
            nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.LATEST).send().getTransactionCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nonce;
    }

    @Override
    public Flowable<Long> getLatestBlockNumber() {
        return Flowable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send().getBlock().getNumber().longValue();
            }
        });
    }

    public interface OnTransactionStatusChangedListener {

        void OnTransactionStatusChanged(TransactionStatus transactionStatus, String transactionHash);
    }

}
