package com.kms.demo.component.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.kms.demo.R;
import com.kms.demo.entity.Transaction;
import com.kms.demo.utils.DateUtil;
import com.kms.demo.utils.NumberParserUtil;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class TransactionPendingDetailView extends TransactionDetailView {

    private final static int SUM_CONFIRMED_BLOCKNUMBER = 12;

    @BindView(R.id.tv_status_desc)
    TextView tvStatusDesc;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_send_amount)
    TextView tvSendAmount;
    @BindView(R.id.tv_fee_amount)
    TextView tvFeeAmount;
    @BindView(R.id.tv_send_time)
    TextView tvSendTime;
    @BindView(R.id.tv_finish_time)
    TextView tvFinishTime;
    @BindView(R.id.tv_note)
    TextView tvNote;
    @BindView(R.id.tv_transaction_hash)
    TextView tvTransactionHash;
    @BindView(R.id.tv_from_address)
    TextView tvFromAddress;
    @BindView(R.id.tv_to_address)
    TextView tvToAddress;
    @BindView(R.id.tv_sender_wallet_name)
    TextView tvSenderWalletName;

    private Context context;
    private Unbinder unbinder;

    public TransactionPendingDetailView(Context context) {
        this(context, null, 0);
    }

    public TransactionPendingDetailView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransactionPendingDetailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void init(Context context) {
        this.context = context;
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_transaction_pending, this);
        unbinder = ButterKnife.bind(this, contentView);
    }

    @Override
    public void showDetail(Transaction transaction, String walletAddress) {

        double sendAmount = transaction.getSendAmount();
        double feeAmount = transaction.getFeeAmount();
        String note = transaction.getNote();
        String hash = transaction.getHash();
        String fromAddress = transaction.getFromAddress();
        String toAddress = transaction.getToAddress();
        String processingWithSignedNumberText = context.getString(R.string.processing_with_signed_number, String.format("%d/%d", transaction.getSignedBlockNumber(), SUM_CONFIRMED_BLOCKNUMBER));
        boolean isReceiver = transaction.isReceiver(walletAddress);

        tvType.setText(isReceiver ? context.getString(R.string.receiving) : context.getString(R.string.sending));
        tvSendAmount.setText(String.format("%s%s", isReceiver ? "+" : "-", context.getString(R.string.amount_with_unit, NumberParserUtil.getPrettyBalance(sendAmount))));
        tvFeeAmount.setText(String.format("%s%s", isReceiver ? "" : "-", context.getString(R.string.amount_with_unit, NumberParserUtil.getPrettyBalance(feeAmount))));
        tvSendTime.setText(DateUtil.format(transaction.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND));
        tvNote.setText(note);
        tvTransactionHash.setText(hash);
        tvFromAddress.setText(fromAddress);
        tvToAddress.setText(toAddress);
        tvStatusDesc.setText(processingWithSignedNumberText);
        tvSenderWalletName.setText(transaction.getWalletName());
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
