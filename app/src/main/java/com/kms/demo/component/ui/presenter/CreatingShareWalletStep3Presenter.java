package com.kms.demo.component.ui.presenter;

import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.CreatingShareWalletStep2Contract;
import com.kms.demo.component.ui.contract.CreatingShareWalletStep3Contract;

/**
 * @author matrixelement
 */
public class CreatingShareWalletStep3Presenter extends BasePresenter<CreatingShareWalletStep3Contract.View> implements CreatingShareWalletStep3Contract.Presenter {

    public CreatingShareWalletStep3Presenter(CreatingShareWalletStep3Contract.View view) {
        super(view);
    }
}
