package com.kms.demo.engine;

import android.text.TextUtils;
import android.util.Log;

import com.kms.appcore.apiservice.ApiErrorCode;
import com.kms.appcore.apiservice.ApiRequestBody;
import com.kms.appcore.apiservice.ApiResponse;
import com.kms.appcore.apiservice.EmptySingleTransformer;
import com.kms.appcore.apiservice.HttpClient;
import com.kms.appcore.apiservice.SchedulersTransformer;
import com.kms.demo.db.WalletDao;
import com.kms.demo.db.WalletEntity;
import com.kms.demo.engine.api.WalletService;
import com.kms.demo.engine.entity.GetPkReturn;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.JSONUtil;
import com.kms.demo.utils.WalletUtil;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import retrofit2.Response;

/**
 * @author matrixelement
 */
public class WalletManager {

    private final static String TAG = WalletManager.class.getSimpleName();

    private List<Wallet> waittingRestoredWalletList = new ArrayList<Wallet>();

    private WalletManager() {

    }

    private static class SingletonHolder {
        private static WalletManager WALLET_MANAGER = new WalletManager();
    }

    public static WalletManager getInstance() {
        return WalletManager.SingletonHolder.WALLET_MANAGER;
    }

    public List<Wallet> getWaittingRestoredWalletList() {
        return waittingRestoredWalletList;
    }

    public Single<Response<ApiResponse<Wallet>>> createTrustWallet(LifecycleProvider<?> provider, String phoneNumber, String walletName, String password, String smsCode, String timestamp) {

        return HttpClient.getInstance().createService(WalletService.class).createTrustWallet(ApiRequestBody.newBuilder()
                .put("phone", phoneNumber)
                .put("walletName", walletName)
                .put("smsCode", smsCode)
                .put("timestamp", timestamp)
                .build())
                .flatMap(new Function<Response<ApiResponse<Wallet>>, SingleSource<Response<ApiResponse<Wallet>>>>() {
                    @Override
                    public SingleSource<Response<ApiResponse<Wallet>>> apply(Response<ApiResponse<Wallet>> apiResponseResponse) throws Exception {
                        if (apiResponseResponse.isSuccessful() && apiResponseResponse.body().getResult() == ApiErrorCode.SUCCESS) {
                            Wallet wallet = apiResponseResponse.body().getData().generateRandomAvatar();
                            return getPk(wallet.getPk()).map(new Function<Response<ApiResponse<GetPkReturn>>, Response<ApiResponse<Wallet>>>() {
                                @Override
                                public Response<ApiResponse<Wallet>> apply(Response<ApiResponse<GetPkReturn>> response) throws Exception {
                                    if (response.isSuccessful() && response.body().getResult() == ApiErrorCode.SUCCESS) {
                                        Wallet w = updateWallet(wallet, response.body().getData(), password);
                                        WalletDao.insertOrUpdate(w.parseWalletEntity());
                                        return Response.success(new ApiResponse<Wallet>(ApiErrorCode.SUCCESS, w));
                                    } else {
                                        return Response.success(new ApiResponse<Wallet>(ApiErrorCode.SUCCESS, wallet));
                                    }
                                }
                            });
                        }
                        return Single.just(apiResponseResponse);
                    }
                })
                .map(new Function<Response<ApiResponse<Wallet>>, Response<ApiResponse<Wallet>>>() {

                    @Override
                    public Response<ApiResponse<Wallet>> apply(Response<ApiResponse<Wallet>> apiResponseResponse) throws Exception {
                        return apiResponseResponse;
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(provider != null ? provider.bindToLifecycle() : EmptySingleTransformer.get());
    }

    private Wallet updateWallet(Wallet wallet, GetPkReturn getPkReturn, String password) {
        String key = WalletUtil.generateWalletKey(getPkReturn.getSk1(), password);
        String multPk = getPkReturn.getMultPk();
        String multSk = getPkReturn.getMultSk();
        return wallet.clone().updateKey(key).updateMultPk(multPk).updateMultSk(multSk);
    }

    public Single<Response<ApiResponse<Wallet>>> restoreWallet(LifecycleProvider<?> provider, Wallet wallet, String phoneNumber, String password, String smsCode) {

        return recoverWallet(phoneNumber, wallet.getId(), smsCode)
                .flatMap(new Function<Response<ApiResponse<Void>>, SingleSource<Response<ApiResponse<Wallet>>>>() {
                    @Override
                    public SingleSource<Response<ApiResponse<Wallet>>> apply(Response<ApiResponse<Void>> apiResponseResponse) throws Exception {
                        if (apiResponseResponse.isSuccessful() && apiResponseResponse.body().getResult() == ApiErrorCode.SUCCESS) {
                            return getPk(wallet.getPk()).map(new Function<Response<ApiResponse<GetPkReturn>>, Response<ApiResponse<Wallet>>>() {
                                @Override
                                public Response<ApiResponse<Wallet>> apply(Response<ApiResponse<GetPkReturn>> response) throws Exception {
                                    return Response.success(new ApiResponse<Wallet>(apiResponseResponse.body().getResult(), updateWallet(wallet, response.body().getData(), password)));
                                }
                            });
                        } else {
                            return Single.just(apiResponseResponse).map(new Function<Response<ApiResponse<Void>>, Response<ApiResponse<Wallet>>>() {
                                @Override
                                public Response<ApiResponse<Wallet>> apply(Response<ApiResponse<Void>> apiResponseResponse) throws Exception {
                                    return Response.error(apiResponseResponse.errorBody(), apiResponseResponse.raw());
                                }
                            });
                        }
                    }
                })
                .doOnSuccess(new Consumer<Response<ApiResponse<Wallet>>>() {
                    @Override
                    public void accept(Response<ApiResponse<Wallet>> apiResponseResponse) throws Exception {
                        if (apiResponseResponse.isSuccessful() && apiResponseResponse.body().getResult() == ApiErrorCode.SUCCESS) {
                            WalletDao.insertOrUpdate(apiResponseResponse.body().getData().generateRandomAvatar().parseWalletEntity());
                        }
                    }
                });
    }

    public Single<List<Wallet>> getWalletList(LifecycleProvider<?> provider, String phoneNumber, int pageNo, int pageSize) {

        return getWalletListFromNet(phoneNumber, pageNo, pageSize)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        waittingRestoredWalletList.clear();
                    }
                })
                .flatMap(new Function<Response<ApiResponse<List<Wallet>>>, SingleSource<Response<ApiResponse<List<Wallet>>>>>() {
                    @Override
                    public SingleSource<Response<ApiResponse<List<Wallet>>>> apply(Response<ApiResponse<List<Wallet>>> response) throws Exception {
                        if (response.isSuccessful() && response.body().getResult() == ApiErrorCode.SUCCESS) {
                            return Single.just(Response.success(new ApiResponse(ApiErrorCode.SUCCESS, response.body().getData())));
                        } else {
                            return getWalletListFromDB();
                        }
                    }
                })
                .map(new Function<Response<ApiResponse<List<Wallet>>>, List<Wallet>>() {

                    @Override
                    public List<Wallet> apply(Response<ApiResponse<List<Wallet>>> response) throws Exception {
                        return response.body().getData();
                    }
                })
                .toFlowable()
                .flatMap(new Function<List<Wallet>, Publisher<Wallet>>() {
                    @Override
                    public Publisher<Wallet> apply(List<Wallet> walletList) throws Exception {
                        return Flowable.fromIterable(walletList);
                    }
                })
                .map(new Function<Wallet, Wallet>() {
                    @Override
                    public Wallet apply(Wallet wallet) throws Exception {

                        WalletEntity walletEntity = WalletDao.getWalletEntity(wallet.getId());

                        if (walletEntity != null) {

                            if (wallet.getAvatar() == 0) {
                                wallet.updateAvatar(walletEntity.getAvatar());
                            }
                            if (TextUtils.isEmpty(wallet.getKey())) {
                                wallet.updateKey(walletEntity.getKey());
                            }

                            if (TextUtils.isEmpty(wallet.getMultSk())) {
                                wallet.updateMultSk(walletEntity.getMultSk());
                            }

                            if (TextUtils.isEmpty(wallet.getMultPk())) {
                                wallet.updateMultSk(walletEntity.getMultPk());
                            }
                        }

                        if (wallet.getAvatar() == 0) {
                            wallet.generateRandomAvatar();
                        }

                        return wallet;
                    }
                })
                .doOnNext(new Consumer<Wallet>() {
                    @Override
                    public void accept(Wallet wallet) throws Exception {
                        if (TextUtils.isEmpty(wallet.getKey())) {
                            waittingRestoredWalletList.add(wallet);
                        }
                    }
                })
                .collect(new Callable<List<Wallet>>() {
                    @Override
                    public List<Wallet> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<Wallet>, Wallet>() {
                    @Override
                    public void accept(List<Wallet> walletList, Wallet wallet) throws Exception {
                        walletList.add(wallet);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(provider != null ? provider.bindToLifecycle() : EmptySingleTransformer.get());
    }

    public Single<Boolean> insertOrUpdate(List<Wallet> walletList) {

        return Flowable.fromIterable(walletList).map(new Function<Wallet, WalletEntity>() {

            @Override
            public WalletEntity apply(Wallet wallet) throws Exception {
                return wallet.parseWalletEntity();
            }
        })
                .toList()
                .flatMap(new Function<List<WalletEntity>, SingleSource<Boolean>>() {
                    @Override
                    public SingleSource<Boolean> apply(List<WalletEntity> walletEntityList) throws Exception {
                        return Single.just(WalletDao.insertOrUpdate(walletEntityList));
                    }
                })
                .compose(new SchedulersTransformer());

    }

    public Single<Boolean> insertOrUpdate(Wallet wallet) {

        return Single.just(wallet).map(new Function<Wallet, WalletEntity>() {

            @Override
            public WalletEntity apply(Wallet wallet) throws Exception {
                return wallet.parseWalletEntity();
            }
        }).flatMap(new Function<WalletEntity, SingleSource<Boolean>>() {
            @Override
            public SingleSource<Boolean> apply(WalletEntity walletEntity) throws Exception {
                return Single.just(WalletDao.insertOrUpdate(walletEntity));
            }
        }).compose(new SchedulersTransformer());
    }

    public Single<String> isWalletPasswordCorrect(LifecycleProvider<?> provider, Wallet wallet, String password) {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return WalletUtil.exportPrivateKey(wallet, password);
            }
        }).onErrorResumeNext(new Single<String>() {
            @Override
            protected void subscribeActual(SingleObserver<? super String> observer) {
                observer.onSuccess("");
            }
        })
                .compose(new SchedulersTransformer())
                .compose(provider != null ? provider.bindToLifecycle() : EmptySingleTransformer.get());
    }

    private Single<Response<ApiResponse<List<Wallet>>>> getWalletListFromNet(String phoneNumber, int pageNo, int pageSize) {
        return HttpClient.getInstance().createService(WalletService.class).getWalletList(phoneNumber, pageNo, pageSize)
                .onErrorResumeNext(new Single<Response<ApiResponse<List<Wallet>>>>() {
                    @Override
                    protected void subscribeActual(SingleObserver<? super Response<ApiResponse<List<Wallet>>>> observer) {
                        observer.onSuccess(Response.success(new ApiResponse<List<Wallet>>(ApiErrorCode.SYSTEM_ERROR, new ArrayList<Wallet>())));
                    }
                });
    }

    private Single<Response<ApiResponse<Void>>> recoverWallet(String phoneNumber, String walletId, String smsCode) {
        return HttpClient.getInstance().createService(WalletService.class).recoverWallet(ApiRequestBody.newBuilder()
                .put("phone", phoneNumber)
                .put("walletId", walletId)
                .put("smsCode", smsCode)
                .build());
    }

    private Single<Response<ApiResponse<GetPkReturn>>> getPk(String publicKey) {
        String rand = WalletUtil.generateRandom(8);
        Map<String, String> map = new HashMap<String, String>();
        map.put("pk", publicKey);
        map.put("rand", rand);
        return HttpClient.getInstance().createService(WalletService.class).getPk(ApiRequestBody.newBuilder()
                .put("data", JSONUtil.toJSONString(map))
                .put("appId", "1")
                .put("customerId", KYCManager.getInstance().getPhoneNumber())
                .build());
    }

    private Single<Response<ApiResponse<List<Wallet>>>> getWalletListFromDB() {
        return Flowable.fromIterable(WalletDao.getWalletEntityList()).map(new Function<WalletEntity, Wallet>() {

            @Override
            public Wallet apply(WalletEntity walletEntity) throws Exception {
                return walletEntity.parseWallet();
            }
        }).toList().flatMap(new Function<List<Wallet>, SingleSource<List<Wallet>>>() {
            @Override
            public SingleSource<List<Wallet>> apply(List<Wallet> walletList) throws Exception {
                return Single.just(walletList);
            }
        }).flatMap(new Function<List<Wallet>, SingleSource<? extends Response<ApiResponse<List<Wallet>>>>>() {
            @Override
            public SingleSource<Response<ApiResponse<List<Wallet>>>> apply(List<Wallet> walletList) throws Exception {
                return Single.just(Response.success(new ApiResponse(ApiErrorCode.SUCCESS, walletList)));
            }
        });
    }


}
