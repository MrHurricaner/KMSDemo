package com.kms.demo.engine;

import android.util.Log;

import com.juzix.kms.HexUtil;
import com.juzix.sdk.ComputeEcdsaSign;
import com.juzix.sdk.MpcSdk;
import com.kms.appcore.apiservice.ApiErrorCode;
import com.kms.appcore.apiservice.ApiRequestBody;
import com.kms.appcore.apiservice.ApiResponse;
import com.kms.appcore.apiservice.HttpClient;
import com.kms.appcore.utils.MapUtils;
import com.kms.demo.engine.api.MPCService;
import com.kms.demo.entity.TransactionStatus;
import com.kms.demo.utils.JSONUtil;

import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import retrofit2.Response;

/**
 * @author matrixelement
 */
public class MPCManager {

    private final static String TAG = MPCManager.getInstance().toString();

    private MPCManager() {

    }

    private static class SingletonHolder {
        private static MPCManager MPC_MANAGER = new MPCManager();
    }

    public static MPCManager getInstance() {
        return SingletonHolder.MPC_MANAGER;
    }

    public Single<String> signMessage(RawTransaction rawTransaction, String phoneNumber, String walletId, String publicKey, String sk, String multPk, String multSk, String transactionId) {

        byte[] messageHash = Hash.sha3(MPCTransactionEncoder.encode(rawTransaction));

        Log.e(TAG, messageHash + ":" + messageHash.length);

        String hash = Numeric.toHexString(messageHash, 0, messageHash.length, false);

        return applySign(phoneNumber, walletId, hash)
                .filter(new Predicate<Response<ApiResponse<String>>>() {
                    @Override
                    public boolean test(Response<ApiResponse<String>> response) throws Exception {
                        return response.isSuccessful() && response.body().getResult() == ApiErrorCode.SUCCESS;
                    }
                })
                .switchIfEmpty(new Single<Response<ApiResponse<String>>>() {
                    @Override
                    protected void subscribeActual(SingleObserver<? super Response<ApiResponse<String>>> observer) {
                        observer.onSuccess(null);
                    }
                })
                .map(new Function<Response<ApiResponse<String>>, Map<String, Map<String, Object>>>() {
                    @Override
                    public Map<String, Map<String, Object>> apply(Response<ApiResponse<String>> response) throws Exception {
                        String getToOther = response.body().getData();
                        return MpcSdk.getMpcSdk(ComputeEcdsaSign.PARTY_P1).nextStep(JSONUtil.parseObject(getToOther, Map.class), buildParam(hash, sk, multPk, multSk)).getToOthers();
                    }
                })
                .flatMap(new Function<Map<String, Map<String, Object>>, SingleSource<Response<ApiResponse<String>>>>() {
                    @Override
                    public SingleSource<Response<ApiResponse<String>>> apply(Map<String, Map<String, Object>> getToOthers) throws Exception {
                        return signMpcStep1(getToOthers);
                    }
                })
                .filter(new Predicate<Response<ApiResponse<String>>>() {
                    @Override
                    public boolean test(Response<ApiResponse<String>> response) throws Exception {
                        return response.isSuccessful() && response.body().getResult() == ApiErrorCode.SUCCESS;
                    }
                })
                .switchIfEmpty(new Single<Response<ApiResponse<String>>>() {
                    @Override
                    protected void subscribeActual(SingleObserver<? super Response<ApiResponse<String>>> observer) {
                        observer.onError(null);
                    }
                })
                .map(new Function<Response<ApiResponse<String>>, Map<String, Map<String, Object>>>() {

                    @Override
                    public Map<String, Map<String, Object>> apply(Response<ApiResponse<String>> response) throws Exception {
                        String getToOther = response.body().getData();
                        return MpcSdk.getMpcSdk(ComputeEcdsaSign.PARTY_P1).nextStep(JSONUtil.parseObject(getToOther, Map.class), new HashMap<>()).getToOthers();
                    }
                })
                .flatMap(new Function<Map<String, Map<String, Object>>, SingleSource<Response<ApiResponse<String>>>>() {
                    @Override
                    public SingleSource<Response<ApiResponse<String>>> apply(Map<String, Map<String, Object>> getToOthers) throws Exception {
                        return signMpcStep2(getToOthers);
                    }
                })
                .map(new Function<Response<ApiResponse<String>>, Map<String, Object>>() {

                    @Override
                    public Map<String, Object> apply(Response<ApiResponse<String>> response) throws Exception {
                        String getToOther = response.body().getData();
                        return MpcSdk.getMpcSdk(ComputeEcdsaSign.PARTY_P1).nextStep(JSONUtil.parseObject(getToOther, Map.class), new HashMap<>()).getResult();
                    }
                })
                .map(new Function<Map<String, Object>, Sign.SignatureData>() {
                    @Override
                    public Sign.SignatureData apply(Map<String, Object> map) throws Exception {
                        return generateSignMessageAndVerify(map, messageHash, publicKey);
                    }
                })
                .map(new Function<Sign.SignatureData, String>() {
                    @Override
                    public String apply(Sign.SignatureData signatureData) throws Exception {
                        return Numeric.toHexString(MPCTransactionEncoder.encode(rawTransaction, signatureData));
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        TransactionManager.getInstance().updateTransaction(transactionId, null, TransactionStatus.MPC_FAILED);
                    }
                });
    }

    private Sign.SignatureData generateSignMessageAndVerify(Map<String, Object> map, byte[] messageHash, String publicKey) {

        BigInteger[] components = new BigInteger[2];
        components[0] = new BigInteger(1, HexUtil.hexToByteArray(MapUtils.getString(map, ComputeEcdsaSign.SIGN_R)));
        components[1] = new BigInteger(1, HexUtil.hexToByteArray(MapUtils.getString(map, ComputeEcdsaSign.SIGN_S)));

        ECDSASignature signature = new ECDSASignature(components[0], components[1]).toCanonicalised();

        return MPCSign.recoverFromSignature(signature, messageHash, new BigInteger(1, HexUtil.hexToByteArray(publicKey)));
    }

    private Map<String, Object> buildParam(String hash, String sk, String multPk, String multSk) {
        Map<String, Object> p1_input = new HashMap<>();
        p1_input.put(ComputeEcdsaSign.DATA_HASH, hash);
        p1_input.put(ComputeEcdsaSign.P1_SK, sk);
        p1_input.put(ComputeEcdsaSign.MULT_PK, multPk);
        p1_input.put(ComputeEcdsaSign.MULT_SK, multSk);
        return p1_input;
    }

    private Single<Response<ApiResponse<String>>> applySign(String phoneNumber, String walletId, String messageHash) {
        return HttpClient.getInstance().createService(MPCService.class).applySign(ApiRequestBody.newBuilder()
                .put("phone", phoneNumber)
                .put("walletId", walletId)
                .put("hash", messageHash)
                .build());
    }

    private Single<Response<ApiResponse<String>>> signMpcStep1(Map<String, Map<String, Object>> getToOthers) {
        return HttpClient.getInstance().createService(MPCService.class).signMpcStep1(ApiRequestBody.newBuilder()
                .put("getToOthers", JSONUtil.toJSONString(getToOthers))
                .build());
    }

    private Single<Response<ApiResponse<String>>> signMpcStep2(Map<String, Map<String, Object>> getToOthers) {
        return HttpClient.getInstance().createService(MPCService.class).signMpcStep2(ApiRequestBody.newBuilder()
                .put("getToOthers", JSONUtil.toJSONString(getToOthers))
                .build());
    }
}
