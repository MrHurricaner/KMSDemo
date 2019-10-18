package com.kms.demo.component.ui.base;

import android.os.Bundle;

/**
 * @author ziv
 */
public abstract class MVPBaseActivity<T extends BasePresenter> extends BaseActivity {

    /**
     * presenter对象
     */
    protected T mPresenter;

    protected abstract T createPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPresenter = createPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

}
