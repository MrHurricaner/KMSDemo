package com.kms.demo.component.ui.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.kms.demo.R;
import com.kms.demo.app.LoadingTransformer;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.SendTransactionContract;
import com.kms.demo.component.ui.dialog.OnConfirmBtnClickListener;
import com.kms.demo.component.ui.dialog.SendTransactionConfirmDialogFragment;
import com.kms.demo.component.ui.dialog.SubmitPasswordDialogFragment;
import com.kms.demo.component.ui.view.TransactionDetailActivity;
import com.kms.demo.db.TransactionEntity;
import com.kms.demo.engine.TransactionManager;
import com.kms.demo.engine.WalletManager;
import com.kms.demo.entity.TransactionStatus;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.BigDecimalUtil;
import com.kms.demo.utils.NumberParserUtil;
import com.kms.demo.utils.WalletUtil;

import java.math.BigInteger;
import java.math.RoundingMode;

import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class SendTransactionPresenter extends BasePresenter<SendTransactionContract.View> implements SendTransactionContract.Presenter {

    private final static String TAG = SendTransactionPresenter.class.getSimpleName();
    private final static double MIN_GAS_PRICE_WEI = 1E9;
    private final static double MAX_GAS_PRICE_WEI = 1E10;
    private final static double D_GAS_PRICE_WEI = MAX_GAS_PRICE_WEI - MIN_GAS_PRICE_WEI;
    private final static long DEFAULT_GAS_LIMIT = 21000;

    private double gasPrice = MIN_GAS_PRICE_WEI;
    private long gasLimit = DEFAULT_GAS_LIMIT;
    private double feeAmount;

    private Wallet wallet;

    public SendTransactionPresenter(SendTransactionContract.View view) {
        super(view);
        wallet = view.getWalletFromIntent();
    }

    @Override
    public void loadData() {
        if (isViewAttached()) {
            if (wallet != null) {
                getView().setWalletInfo(wallet);
            }
        }
    }

    @Override
    public boolean checkReceiveAddress() {
        if (isViewAttached()) {

            String errMsg = null;

            String receiveAddress = getView().getReceiveAddress().trim();

            if (!WalletUtil.isValidAddress(receiveAddress)) {
                errMsg = string(R.string.invalid_receiving_address);
            }

            getView().showReceiveAddressError(errMsg);

            return TextUtils.isEmpty(errMsg);
        }

        return false;
    }

    @Override
    public boolean checkSendAmount() {
        if (isViewAttached()) {
            String errMsg = null;
            String sendAmount = getView().getSendAmount().trim();

            if (TextUtils.isEmpty(sendAmount)) {
                errMsg = string(R.string.send_amount_required);
            } else {
                if (!isBalanceEnough()) {
                    errMsg = string(R.string.insufficient_balance);
                }
            }

            getView().showSendAmountError(errMsg);

            return TextUtils.isEmpty(errMsg);

        }
        return false;
    }

    @Override
    public boolean checkNote() {
        if (isViewAttached()) {
            String errMsg = null;
            String note = getView().getTransactionNote().trim();
            if (note != null && note.length() > 30) {
                errMsg = string(R.string.exceed_30_characters);
            }

            getView().showTransactionNoteError(errMsg);

            return TextUtils.isEmpty(errMsg);
        }

        return false;
    }

    @Override
    public boolean checkSendTransactionBtn() {

        if (isViewAttached()) {

            String receiveAddress = getView().getReceiveAddress().trim();

            String sendAmount = getView().getSendAmount().trim();

            boolean isEnable = !TextUtils.isEmpty(receiveAddress) && !TextUtils.isEmpty(sendAmount) && isBalanceEnough();

            getView().setSendTransactionBtnEnable(isEnable);

            return isEnable;
        }

        return false;
    }

    @Override
    public void sendTransaction() {
        if (isViewAttached()) {
            if (checkNote() && checkSendAmount()) {

                String toAddress = getView().getReceiveAddress().trim();
                String note = getView().getTransactionNote().trim();
                String sendAmount = getView().getSendAmount().trim();

                SendTransactionConfirmDialogFragment.getInstance(toAddress, note, NumberParserUtil.parseDouble(sendAmount), feeAmount).setOnConfirmBtnClickListener(new OnConfirmBtnClickListener() {
                    @Override
                    public void onConfirmBtnClick() {

                        SubmitPasswordDialogFragment.getInstance(wallet, null).setOnConfirmPasswordBtnClickListener(new SubmitPasswordDialogFragment.OnConfirmPasswordBtnClickListener() {

                            @Override
                            public void OnConfirmPasswordBtnClick(SubmitPasswordDialogFragment dialogFragment, String password) {
                                WalletManager.getInstance().isWalletPasswordCorrect(getLifecycleProvider(), wallet, password)
                                        .compose(LoadingTransformer.bindToLifecycle((BaseActivity) getView()))
                                        .subscribe(new Consumer<String>() {
                                            @Override
                                            public void accept(String sk) throws Exception {
                                                if (!TextUtils.isEmpty(sk)) {
                                                    dialogFragment.dismiss();
                                                    TransactionEntity transactionEntity = new TransactionEntity.Builder()
                                                            .avatar(wallet.getAvatar())
                                                            .createTime(System.currentTimeMillis())
                                                            .feeAmount(feeAmount)
                                                            .sendAmount(NumberParserUtil.parseDouble(sendAmount))
                                                            .from(wallet.getWalletAddress())
                                                            .to(toAddress)
                                                            .note(note)
                                                            .walletName(wallet.getName())
                                                            .transactionStatusCode(TransactionStatus.MPC_EXECUTING.statusCode)
                                                            .build();
                                                    TransactionDetailActivity.actionStartWithExtra(getContext(), transactionEntity.parseTransaction(), wallet.getWalletAddress(), wallet.getSmallAvatar());
                                                    ((BaseActivity) getView()).finish();
                                                    TransactionManager.getInstance().send(wallet, transactionEntity, sk, toAddress, sendAmount, note, NumberParserUtil.parseLong(BigDecimalUtil.parseString((long) gasPrice)), gasLimit, transactionEntity.getId());
                                                } else {
                                                    dialogFragment.showWalletPasswordError(string(R.string.incorrect_wallet_password));
                                                }
                                            }
                                        });


                            }
                        }).show(((BaseActivity) getView()).getSupportFragmentManager(), "sendTransactionConfirmDialog");


                    }
                }).show(((BaseActivity) getContext()).getSupportFragmentManager(), "sendTransactionConfirm");
            }
        }
    }

    @Override
    public void sendAllBalance() {
        if (isViewAttached() && wallet != null) {
            getView().setSendAmount(NumberParserUtil.getPrettyBalance(BigDecimalUtil.sub(wallet.getBalance(), feeAmount)));
        }
    }

    @Override
    public void calculateFee() {

        if (isViewAttached() && wallet != null) {

            String from = wallet.getWalletAddress().trim();
            String to = getView().getReceiveAddress().trim();
            String note = getView().getTransactionNote().trim();

            TransactionManager.getInstance()
                    .getEstimateGas(getLifecycleProvider(), from, to, note)
                    .onErrorReturnItem(BigInteger.valueOf(DEFAULT_GAS_LIMIT))
                    .subscribe(new Consumer<BigInteger>() {
                        @Override
                        public void accept(BigInteger bigInteger) throws Exception {
                            gasLimit = bigInteger.longValue();
                            updateFeeAmount();
                        }
                    });
        }
    }

    @Override
    public void calculateFeeAndCheckSendAmount() {
        if (isViewAttached() && wallet != null) {

            String from = wallet.getWalletAddress().trim();
            String to = getView().getReceiveAddress().trim();
            String note = getView().getTransactionNote().trim();

            TransactionManager.getInstance()
                    .getEstimateGas(getLifecycleProvider(), from, to, note)
                    .onErrorReturnItem(BigInteger.valueOf(DEFAULT_GAS_LIMIT))
                    .subscribe(new Consumer<BigInteger>() {
                        @Override
                        public void accept(BigInteger bigInteger) throws Exception {
                            gasLimit = bigInteger.longValue();
                            updateFeeAmount();
                            checkSendAmount();
                        }
                    });
        }
    }

    @Override
    public void calculateFeeAndcheckSendTransactionBtn() {
        if (isViewAttached() && wallet != null) {

            String from = wallet.getWalletAddress().trim();
            String to = getView().getReceiveAddress().trim();
            String note = getView().getTransactionNote().trim();

            TransactionManager.getInstance()
                    .getEstimateGas(getLifecycleProvider(), from, to, note)
                    .onErrorReturnItem(BigInteger.valueOf(DEFAULT_GAS_LIMIT))
                    .subscribe(new Consumer<BigInteger>() {
                        @Override
                        public void accept(BigInteger bigInteger) throws Exception {
                            gasLimit = bigInteger.longValue();
                            updateFeeAmount();
                            checkSendTransactionBtn();
                        }
                    });
        }
    }

    private void updateFeeAmount() {

        if (isViewAttached()) {

            float percent = getView().getProgressFloat();
            double minFee = getMinFee();
            double maxFee = getMaxFee();
            double dValue = BigDecimalUtil.sub(maxFee, minFee);

            this.feeAmount = BigDecimalUtil.add(minFee, BigDecimalUtil.mul(percent, dValue), 8, RoundingMode.CEILING);
            this.gasPrice = BigDecimalUtil.add(MIN_GAS_PRICE_WEI, BigDecimalUtil.mul(percent, D_GAS_PRICE_WEI));

            Log.e(TAG, "percent is " + percent + "feeAmount" + feeAmount);

            getView().setFeeAmount(BigDecimalUtil.parseString(feeAmount));
        }
    }

    private boolean isBalanceEnough() {

        if (isViewAttached()) {

            String sendAmount = getView().getSendAmount().trim();
            double sumAmount = BigDecimalUtil.add(NumberParserUtil.parseDouble(sendAmount), feeAmount);

            Log.e(TAG, "feeAmount" + feeAmount + "sumAmount" + sumAmount);

            return wallet != null && sumAmount > 0 && wallet.getBalance() >= sumAmount;
        }
        return false;
    }


    private double getMinFee() {
        return BigDecimalUtil.div(BigDecimalUtil.mul(gasLimit, MIN_GAS_PRICE_WEI), 1E18);
    }

    private double getMaxFee() {
        return BigDecimalUtil.div(BigDecimalUtil.mul(gasLimit, MAX_GAS_PRICE_WEI), 1E18);
    }
}
