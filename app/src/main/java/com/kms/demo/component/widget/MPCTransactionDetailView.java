package com.kms.demo.component.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kms.demo.R;
import com.kms.demo.entity.Transaction;
import com.kms.demo.entity.TransactionStatus;
import com.kms.demo.utils.BigDecimalUtil;
import com.kms.demo.utils.NumberParserUtil;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class MPCTransactionDetailView extends TransactionDetailView {

    @BindView(R.id.tv_status_desc)
    TextView tvStatusDesc;
    @BindView(R.id.tv_sum_amount)
    TextView tvSumAmount;
    @BindView(R.id.tv_to_address)
    TextView tvToAddress;
    @BindView(R.id.tv_send_amount)
    TextView tvSendAmount;
    @BindView(R.id.tv_fee_amount)
    TextView tvFeeAmount;
    @BindView(R.id.tv_note)
    TextView tvNote;
    @BindView(R.id.progressingAnimationLayout)
    ProgressingAnimationLayout progressingAnimationLayout;
    @BindView(R.id.iv_mpc_failed)
    ImageView ivMpcFailed;

    private Unbinder unbinder;
    private Context context;

    public MPCTransactionDetailView(Context context) {
        this(context, null, 0);
    }

    public MPCTransactionDetailView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MPCTransactionDetailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init(Context context) {
        this.context = context;
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_mpc_transaction_detail, this);
        unbinder = ButterKnife.bind(this, contentView);
    }

    @Override
    public void showDetail(Transaction transaction, String walletAddress) {

        double sendAmount = transaction.getSendAmount();
        double feeAmount = transaction.getFeeAmount();
        double sumAmount = BigDecimalUtil.add(sendAmount, feeAmount);
        String toAddress = transaction.getToAddress();
        String note = transaction.getNote();
        TransactionStatus transactionStatus = transaction.getTransactionStatus();

        ivMpcFailed.setVisibility(transactionStatus == TransactionStatus.MPC_FAILED ? VISIBLE : GONE);
        progressingAnimationLayout.setVisibility(transactionStatus == TransactionStatus.MPC_EXECUTING ? VISIBLE : GONE);
        tvStatusDesc.setText(transaction.getTransactionStatus() == TransactionStatus.MPC_FAILED ? context.getString(R.string.mpc_failed) : context.getString(R.string.mpc_executing_with_dot));
        tvToAddress.setText(toAddress);
        tvSumAmount.setText(NumberParserUtil.getPrettyBalance(sumAmount));
        tvSendAmount.setText(context.getString(R.string.amount_with_unit, NumberParserUtil.getPrettyBalance(sendAmount)));
        tvFeeAmount.setText(context.getString(R.string.amount_with_unit, NumberParserUtil.getPrettyBalance(feeAmount)));
        tvNote.setText(note);


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
