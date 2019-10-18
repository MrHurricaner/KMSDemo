package com.kms.demo.component.ui.presenter;

import com.kms.demo.component.ui.base.BasePresenter;
import com.kms.demo.component.ui.contract.ShareWalletContract;

/**
 * @author matrixelement
 */
public class ShareWalletPresenter extends BasePresenter<ShareWalletContract.View> implements ShareWalletContract.Presenter {

    
    public ShareWalletPresenter(ShareWalletContract.View view) {
        super(view);
    }
}
