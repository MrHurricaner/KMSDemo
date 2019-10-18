package com.kms.demo.component.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.kms.appcore.activity.CoreFragment;
import com.kms.appcore.log.Log;

import androidx.annotation.Nullable;

/**
 * viewPager+Fragment使用
 *
 * @author ziv
 */
public abstract class BaseViewPageFragment extends CoreFragment {

    private static final String TAG = BaseViewPageFragment.class.getSimpleName();
    private View mRootView;
    private boolean mVisibleToUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.debug(TAG, "onCreateView root is null !! [" + getClass().getSimpleName() + "]");
            mRootView = onCreatePage(inflater, container, savedInstanceState);
        } else {
            Log.debug(TAG, "onCreateView root reused !! [" + getClass().getSimpleName() + "]");
            ViewParent parent = mRootView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mRootView);
            }
        }
        return mRootView;
    }

    /**
     * 每次在视图可见的时候，用于加载数据，比如启动网络数据的加载.
     * 调用场景：
     * 1.切换到对应当前页签时
     * 2.从其他Activity返回到当前Activity时
     */
    protected abstract void onPageShow();

    protected void onPageHidden() {

    }

    /**
     * 创建视图
     */
    protected abstract View onCreatePage(LayoutInflater inflater, @Nullable ViewGroup container,
                                         @Nullable Bundle savedInstanceState);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mVisibleToUser = isVisibleToUser;
        Log.debug(TAG, "setUserVisibleHint isVisibleToUser=" + isVisibleToUser + ", [" + getClass().getSimpleName() + "]");
        if (isVisibleToUser) {
            if (mResumed) {
                Log.debug(TAG, "onPageShow !! [" + getClass().getSimpleName() + "]");
                onPageShow();
            }
        } else {
            if (mCreate) {
                Log.debug(TAG, "onPageHidden !! [" + getClass().getSimpleName() + "]");
                onPageHidden();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.debug(TAG, "onResume !! mVisibleToUser =" + mVisibleToUser + ", [" + getClass().getSimpleName() + "]");
        if (mVisibleToUser) {
            Log.debug(TAG, "onPageShow !! [" + getClass().getSimpleName() + "]");
            onPageShow();
        }
    }

}
