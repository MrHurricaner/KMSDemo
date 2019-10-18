package com.kms.demo.component.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.R;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.ui.base.BaseDialog;
import com.kms.demo.config.PermissionConfigure;
import com.kms.demo.utils.ToastUtil;

/**
 * @author ziv
 */
public abstract class BaseContextImpl implements IContext {

    private BaseDialog mProgressDialog;

    @Override
    public void showShortToast(String text) {
        ToastUtil.showShortToast(getContext(), text);
    }

    @Override
    public void showLongToast(String text) {
        ToastUtil.showLongToast(getContext(), text);
    }

    @Override
    public void showShortToast(int resId) {
        ToastUtil.showShortToast(getContext(), resId);
    }

    @Override
    public void showLongToast(int resId) {
        ToastUtil.showLongToast(getContext(), resId);
    }

    @Override
    public void dismissLoadingDialogImmediately() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showLoadingDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = createProgressDialog("");
        mProgressDialog.show();
    }

    @Override
    public void showLoadingDialog(int resId) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = createProgressDialog(string(resId));
        mProgressDialog.show();
    }

    @Override
    public void showLoadingDialog(String text) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = createProgressDialog(text);
        mProgressDialog.show();
    }

    @Override
    public void showLoadingDialogWithCancelable(String text) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = createProgressDialog(text);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    @Override
    public String string(int resId, Object... formatArgs) {
        return getContext().getString(resId, formatArgs);
    }

    @Override
    public void requestPermission(BaseActivity activity, int what, PermissionConfigure.PermissionCallback callback, String... permissions) {
        PermissionConfigure.request(activity, what, callback, permissions);
    }

    private BaseDialog createProgressDialog(String msg) {
        BaseDialog dialog = new BaseDialog(getContext(), R.style.Dialog_FullScreen);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_progress_dialog, null);
        TextView messageView = view.findViewById(R.id.tv_content);
        if (!TextUtils.isEmpty(msg)) {
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(msg);
        } else {
            messageView.setVisibility(View.GONE);
        }
        dialog.setContentView(view);
        dialog.getWindow().setLayout(DensityUtil.dp2px(getContext(), 100), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        return dialog;
    }
}
