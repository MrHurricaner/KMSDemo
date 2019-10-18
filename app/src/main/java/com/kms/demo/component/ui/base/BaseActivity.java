package com.kms.demo.component.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kms.appcore.activity.CoreFragmentActivity;
import com.kms.demo.R;
import com.kms.demo.component.ui.BaseContextImpl;
import com.kms.demo.component.ui.CustomContextWrapper;
import com.kms.demo.component.ui.IContext;
import com.kms.demo.config.ImmersiveBarConfigure;
import com.kms.demo.config.PermissionConfigure;
import com.kms.demo.utils.LanguageUtil;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;


/**
 * @author ziv
 */
public abstract class BaseActivity extends CoreFragmentActivity implements IContext, LifecycleProvider<ActivityEvent> {

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();
    private InputMethodManager mInputMethodManager;
    protected View mDecorView;
    protected ViewGroup mRootView;
    protected int mDefaultStatusBarColor = R.color.color_101119;

    @Override
    @NonNull
    @CheckResult
    public final Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = CustomContextWrapper.wrap(newBase, LanguageUtil.getLocale(newBase));
        super.attachBaseContext(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mDecorView = getWindow().getDecorView();
        mRootView = mDecorView.findViewById(android.R.id.content);
        if (immersiveBarInitEnabled()) {
            if (immersiveBarViewEnabled()) {
                setStatusBarView(getStatusBarView());
            } else {
                setStatusBarColor(getStatusBarColor());
            }
        }
    }

    protected View getStatusBarView() {
        View view = new View(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        view.setLayoutParams(layoutParams);
        return view;
    }

    protected void setStatusBarView(View view) {
        ImmersiveBarConfigure.statusBarView(this, view);
    }

    protected void setStatusBarColor(int colorRes) {
        ImmersiveBarConfigure.statusBarColor(this, colorRes);
    }

    protected int getStatusBarColor() {
        return mDefaultStatusBarColor;
    }

    public ViewGroup getContentView() {
        return (ViewGroup) mRootView.getChildAt(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        super.onDestroy();
        if (immersiveBarInitEnabled()) {
            ImmersiveBarConfigure.destroy(this);
        }
    }

    protected void onShowKeyboard() {

    }

    protected void onHideKeyboard() {

    }

    protected boolean immersiveBarInitEnabled() {
        return true;
    }

    protected boolean immersiveBarViewEnabled() {
        return false;
    }

    @Override
    public Context getContext() {
        return mContextImpl.getContext();
    }

    @Override
    public BaseActivity currentActivity() {
        return mContextImpl.currentActivity();
    }

    @Override
    public String string(int resId, Object... formatArgs) {
        return mContextImpl.string(resId, formatArgs);
    }

    @Override
    public void showShortToast(String text) {
        mContextImpl.showShortToast(text);
    }

    @Override
    public void showLongToast(String text) {
        mContextImpl.showLongToast(text);
    }

    @Override
    public void showShortToast(int resId) {
        mContextImpl.showShortToast(resId);
    }

    @Override
    public void showLongToast(int resId) {
        mContextImpl.showLongToast(resId);
    }


    @Override
    public void dismissLoadingDialogImmediately() {
        mContextImpl.dismissLoadingDialogImmediately();
    }

    @Override
    public void showLoadingDialog() {
        mContextImpl.showLoadingDialog();
    }

    @Override
    public void showLoadingDialog(int resId) {
        mContextImpl.showLoadingDialog(resId);
    }

    @Override
    public void showLoadingDialog(String text) {
        mContextImpl.showLoadingDialog(text);
    }

    @Override
    public void showLoadingDialogWithCancelable(String text) {
        mContextImpl.showLoadingDialogWithCancelable(text);
    }

    @Override
    public void requestPermission(BaseActivity activity, int what, PermissionConfigure.PermissionCallback callback, String... permissions) {
        mContextImpl.requestPermission(activity, what, callback, permissions);
    }

    private BaseContextImpl mContextImpl = new BaseContextImpl() {
        @Override
        public Context getContext() {
            return BaseActivity.this;
        }

        @Override
        public BaseActivity currentActivity() {
            return BaseActivity.this;
        }
    };

    /**
     * 隐藏软键盘
     */
    public void hideSoftInput() {
        if (mInputMethodManager != null && mDecorView != null) {
            mInputMethodManager.hideSoftInputFromWindow(mDecorView.getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInput(final EditText editText) {
        if (mInputMethodManager != null && mDecorView != null) {
            editText.requestFocus();
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mInputMethodManager.showSoftInput(editText, 0);
                }
            }, 100);
        }
    }

    public void toggleSoftInput(View view) {
        if (mInputMethodManager != null && mDecorView != null) {
            mInputMethodManager.toggleSoftInput(0, 0);
        }
    }
}
