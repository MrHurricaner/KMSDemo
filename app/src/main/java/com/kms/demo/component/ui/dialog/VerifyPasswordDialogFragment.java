package com.kms.demo.component.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.BaseDialogFragment;
import com.kms.demo.component.widget.CustomUnderlineEditText;
import com.kms.demo.component.widget.ShadowDrawable;
import com.kms.demo.entity.Wallet;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class VerifyPasswordDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.et_wallet_password)
    CustomUnderlineEditText etWalletPassword;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_wallet_password_error)
    TextView tvWalletPasswordError;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;

    private Unbinder unbinder;
    private OnConfirmPasswordBtnClickListener mListener;

    public static VerifyPasswordDialogFragment getInstance(Wallet wallet) {
        VerifyPasswordDialogFragment dialogFragment = new VerifyPasswordDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Bundle.BUNDLE_WALLET, wallet);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public VerifyPasswordDialogFragment setOnConfirmPasswordBtnClickListener(OnConfirmPasswordBtnClickListener mListener) {
        this.mListener = mListener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_verify_password, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setGravity(Gravity.CENTER);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        ShadowDrawable.setShadowDrawable(layoutContent,
                ContextCompat.getColor(getContext(), R.color.color_1b1e2c),
                DensityUtil.dp2px(getContext(), 8f),
                ContextCompat.getColor(getContext(), R.color.color_8006061c)
                , DensityUtil.dp2px(getContext(), 12f),
                0,
                DensityUtil.dp2px(getContext(), 2));

        Wallet wallet = getArguments().getParcelable(Constants.Bundle.BUNDLE_WALLET);
        if (wallet != null) {
            tvWalletName.setText(wallet.getName());
        }

    }

    private boolean checkPassword() {

        String errorMsg = null;
        String password = etWalletPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            errorMsg = getResources().getString(R.string.wallet_password_required);
        } else {

        }

        tvWalletPasswordError.setVisibility(TextUtils.isEmpty(errorMsg) ? View.GONE : View.VISIBLE);
        tvWalletPasswordError.setText(errorMsg);

        return TextUtils.isEmpty(errorMsg);
    }

    public void showWalletPasswordError(String errMsg) {
        tvWalletPasswordError.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tvWalletPasswordError.setText(errMsg);
        etWalletPassword.setStatus(TextUtils.isEmpty(errMsg) ? CustomUnderlineEditText.Status.NORMAL : CustomUnderlineEditText.Status.ERROR);
    }

    @OnClick({R.id.btn_confirm, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                if (checkPassword()) {
                    if (mListener != null) {
                        mListener.OnConfirmPasswordBtnClick(this, etWalletPassword.getText().toString());
                    }
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public interface OnConfirmPasswordBtnClickListener {

        void OnConfirmPasswordBtnClick(VerifyPasswordDialogFragment dialogFragment, String password);
    }

}
