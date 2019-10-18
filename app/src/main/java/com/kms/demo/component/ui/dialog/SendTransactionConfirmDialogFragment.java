package com.kms.demo.component.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.BaseDialogFragment;
import com.kms.demo.component.widget.ShadowDrawable;
import com.kms.demo.utils.BigDecimalUtil;
import com.kms.demo.utils.NumberParserUtil;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SendTransactionConfirmDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_sum_amount)
    TextView tvSumAmount;
    @BindView(R.id.tv_recipient)
    TextView tvRecipient;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.tv_note)
    TextView tvNote;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.layout_content)
    ConstraintLayout layoutContent;

    private OnConfirmBtnClickListener mListener;
    private Unbinder unbinder;

    public static SendTransactionConfirmDialogFragment getInstance(String toAddress, String note, double sendAmount, double feeAmount) {
        SendTransactionConfirmDialogFragment dialogFragment = new SendTransactionConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Extra.EXTRA_ADDRESS, toAddress);
        bundle.putString(Constants.Extra.EXTRA_NOTE, note);
        bundle.putDouble(Constants.Extra.EXTRA_SEND_AMOUNT, sendAmount);
        bundle.putDouble(Constants.Extra.EXTRA_FEE_AMOUNT, feeAmount);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public SendTransactionConfirmDialogFragment setOnConfirmBtnClickListener(OnConfirmBtnClickListener listener) {
        this.mListener = listener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_send_transaction_confirm, null, false);
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

        String toAddress = getArguments().getString(Constants.Extra.EXTRA_ADDRESS);
        String note = getArguments().getString(Constants.Extra.EXTRA_NOTE);
        double sendAmount = getArguments().getDouble(Constants.Extra.EXTRA_SEND_AMOUNT);
        double feeAmount = getArguments().getDouble(Constants.Extra.EXTRA_FEE_AMOUNT);

        tvSumAmount.setText(NumberParserUtil.getPrettyBalance(BigDecimalUtil.add(sendAmount, feeAmount)));
        tvRecipient.setText(toAddress);
        tvAmount.setText(getString(R.string.amount_with_unit, NumberParserUtil.getPrettyBalance(sendAmount)));
        tvFee.setText(getString(R.string.amount_with_unit, BigDecimalUtil.parseString(feeAmount)));
        tvNote.setText(note);
    }

    @OnClick({R.id.tv_cancel, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                if (mListener != null) {
                    mListener.onConfirmBtnClick();
                    dismiss();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
