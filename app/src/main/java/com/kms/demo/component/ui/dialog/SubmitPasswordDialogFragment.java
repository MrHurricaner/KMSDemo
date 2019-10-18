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

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.kms.appcore.utils.DensityUtil;
import com.kms.appcore.utils.RUtils;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.BaseDialogFragment;
import com.kms.demo.component.widget.CustomUnderlineEditText;
import com.kms.demo.component.widget.ShadowDrawable;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.HanziToPinyin;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class SubmitPasswordDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_wallet_avatar)
    TextView tvWalletAvatar;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.et_wallet_password)
    CustomUnderlineEditText etWalletPassword;
    @BindView(R.id.tv_wallet_password_error)
    TextView tvWalletPasswordError;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;

    private Unbinder unbinder;
    private OnConfirmPasswordBtnClickListener mListener;

    public static SubmitPasswordDialogFragment getInstance(Wallet wallet, String password) {
        SubmitPasswordDialogFragment dialogFragment = new SubmitPasswordDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Extra.EXTRA_WALLET, wallet);
        bundle.putString(Constants.Extra.EXTRA_PASSWORD, password);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public SubmitPasswordDialogFragment setOnConfirmPasswordBtnClickListener(OnConfirmPasswordBtnClickListener mListener) {
        this.mListener = mListener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_submit_password, null, false);
        baseDialog.setContentView(contentView);
        setGravity(Gravity.BOTTOM);
        setAnimation(R.style.Animation_slide_in_bottom);
        setFullWidthEnable(true);
        setHorizontalMargin(DensityUtil.dp2px(getContext(), 12f));
        setyOffset(DensityUtil.dp2px(getContext(), 2f));
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        initData();
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
    }

    private void initData() {

        RxView.clicks(tvCancel).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                dismiss();
            }
        });

        RxView.clicks(btnConfirm).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                if (checkWalletPassword()) {
                    if (mListener != null) {
                        mListener.OnConfirmPasswordBtnClick(SubmitPasswordDialogFragment.this, etWalletPassword.getText().toString());
                    }
                }
            }
        });

        RxTextView.textChanges(etWalletPassword).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                btnConfirm.setEnabled(!TextUtils.isEmpty(charSequence.toString()) && charSequence.toString().length() >= 6);
            }
        });

        Wallet wallet = getArguments().getParcelable(Constants.Extra.EXTRA_WALLET);

        String password = getArguments().getString(Constants.Extra.EXTRA_PASSWORD);

        etWalletPassword.setText(password);
        if (!TextUtils.isEmpty(password)) {
            etWalletPassword.setSelection(password.length());
        }

        if (wallet != null) {
            tvWalletAvatar.setBackgroundResource(RUtils.drawable(wallet.getMediumAvatar()));
            tvWalletAvatar.setText(HanziToPinyin.getFirstLetter(wallet.getName()));
            tvWalletAddress.setText(wallet.getWalletAddress());
            tvWalletName.setText(wallet.getName());
        }
    }

    private boolean checkWalletPassword() {
        String errorMsg = null;
        String walletPassword = etWalletPassword.getText().toString();
        if (TextUtils.isEmpty(walletPassword)) {
            errorMsg = getString(R.string.wallet_password_required);
        } else {
            if (walletPassword.length() < 6) {
                errorMsg = getString(R.string.wallet_password_bit_error);
            }
        }

        showWalletPasswordError(errorMsg);

        return TextUtils.isEmpty(errorMsg);
    }

    public void showWalletPasswordError(String errMsg) {
        tvWalletPasswordError.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tvWalletPasswordError.setText(errMsg);
        etWalletPassword.setStatus(TextUtils.isEmpty(errMsg) ? CustomUnderlineEditText.Status.NORMAL : CustomUnderlineEditText.Status.ERROR);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public interface OnConfirmPasswordBtnClickListener {

        void OnConfirmPasswordBtnClick(SubmitPasswordDialogFragment dialogFragment, String password);
    }
}
