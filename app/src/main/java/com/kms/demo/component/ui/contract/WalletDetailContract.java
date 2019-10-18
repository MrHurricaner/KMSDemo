package com.kms.demo.component.ui.contract;

import com.kms.demo.component.ui.base.IPresenter;
import com.kms.demo.component.ui.base.IView;
import com.kms.demo.entity.Transaction;
import com.kms.demo.entity.Wallet;

import java.util.List;

/**
 * @author matrixelement
 */
public class WalletDetailContract {

    public interface View extends IView {

        Wallet getWalletFromIntent();

        void showWalletInfo(Wallet wallet);

        void setWalletBalance(String balance);

        void notifyDataChanged(List<Transaction> transactionList, String walletAddress);

        void updateItem(Transaction transaction);

    }

    public interface Presenter extends IPresenter<View> {

        void showWalletInfo();

        void getWalletTransactions();

        void getWalletBalance();

        void enterManageWalletPage();

        void enterReceiveTransactionPage();

        void enterSendTransactionPage();

        void enterTransactionDetailPage(Transaction transaction);

        void updateWalletTransactions(Transaction transaction);
    }
}
