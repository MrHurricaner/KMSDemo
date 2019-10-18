package com.kms.demo.component.ui.contract;

import com.kms.demo.component.ui.base.IPresenter;
import com.kms.demo.component.ui.base.IView;
import com.kms.demo.entity.Transaction;
import com.kms.demo.entity.TransactionStatus;

/**
 * @author matrixelement
 */
public class TransactionDetailContract {

    public interface View extends IView {

        Transaction getTransactionFromIntent();

        String getWalletAddress();

        String getWalletAvatar();

        void setTransactionWalletAvatar(String WalletName,String avatar);

        void showTransactionDetail(Transaction transaction,String walletAddress);

    }

    public interface Persenter extends IPresenter<View> {

        void loadTransactionDetail();

        void updateTransactionDetail(Transaction transaction);

        void refreshTransactionDetail();

    }
}
