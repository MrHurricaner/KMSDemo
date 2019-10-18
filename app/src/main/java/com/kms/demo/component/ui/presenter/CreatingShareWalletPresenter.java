package com.kms.demo.component.ui.presenter;

import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.CreatingShareWalletContract;

/**
 * @author matrixelement
 */
public class CreatingShareWalletPresenter extends BasePresenter<CreatingShareWalletContract.View> implements CreatingShareWalletContract.Presenter {

    
    public CreatingShareWalletPresenter(CreatingShareWalletContract.View view) {
        super(view);
    }
}
