package com.kms.demo.component.ui.presenter;

import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.WaitingCreateShareWalletContract;

/**
 * @author matrixelement
 */
public class WaitingCreateShareWalletPresenter extends BasePresenter<WaitingCreateShareWalletContract.View> implements WaitingCreateShareWalletContract.Presenter {

    public WaitingCreateShareWalletPresenter(WaitingCreateShareWalletContract.View view) {
        super(view);
    }
}
