package com.kms.demo.component.ui.contract;

import com.kms.demo.component.ui.base.IPresenter;
import com.kms.demo.component.ui.base.IView;
import com.kms.demo.entity.Wallet;

/**
 * @author matrixelement
 */
public class SendTransactionContract {

    public interface View extends IView {

        Wallet getWalletFromIntent();

        void setWalletInfo(Wallet wallet);

        void setSendAmount(String sendAmount);

        void setFeeAmount(String feeAmount);

        void setSendTransactionBtnEnable(boolean enable);

        void showReceiveAddressError(String errMsg);

        void showSendAmountError(String errMsg);

        void showTransactionNoteError(String errMsg);

        String getSendAmount();

        String getReceiveAddress();

        String getTransactionNote();

        float getProgressFloat();

    }

    public interface Presenter extends IPresenter<View> {

        void loadData();

        boolean checkReceiveAddress();

        boolean checkSendAmount();

        boolean checkNote();

        boolean checkSendTransactionBtn();

        void sendTransaction();

        void sendAllBalance();

        void calculateFee();

        void calculateFeeAndCheckSendAmount();

        void calculateFeeAndcheckSendTransactionBtn();

    }
}
