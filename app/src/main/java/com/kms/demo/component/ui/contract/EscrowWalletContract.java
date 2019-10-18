package com.kms.demo.component.ui.contract;

import com.kms.demo.component.ui.base.IPresenter;
import com.kms.demo.component.ui.base.IView;
import com.kms.demo.entity.Wallet;

import java.util.List;

/**
 * @author matrixelement
 */
public class EscrowWalletContract {

    public interface View extends IView {

        void notifyDataSetChanged(List<Wallet> walletList);

    }

    public interface Presenter extends IPresenter<View> {

        void loadWalletList();

    }
}
