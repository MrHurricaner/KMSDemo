package com.kms.demo.component.adapter;

import android.content.Context;

import com.kms.demo.R;
import com.kms.demo.component.adapter.base.ViewHolder;
import com.kms.demo.entity.Transaction;
import com.kms.demo.entity.TransactionStatus;
import com.kms.demo.utils.DateUtil;
import com.kms.demo.utils.NumberParserUtil;

import java.util.List;

import androidx.core.content.ContextCompat;

/**
 * @author matrixelement
 */
public class TransactionListAdapter extends CommonAdapter<Transaction> {

    private String walletAddress;

    public TransactionListAdapter(int layoutId, List<Transaction> datas) {
        super(layoutId, datas);
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, Transaction transaction, int position) {
        if (transaction != null) {
            TransactionStatus transactionStatus = transaction.getTransactionStatus();
            boolean isReceiver = walletAddress != null && walletAddress.equals(transaction.getToAddress());
            viewHolder.setImageResource(R.id.iv_transation_status, transactionStatus.getStatusDrawable(isReceiver));
            viewHolder.setText(R.id.tv_transaction_time, DateUtil.format(transaction.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND));
            viewHolder.setText(R.id.tv_transation_status, transactionStatus.getTransactionStatusTitle(context, isReceiver));
            viewHolder.setText(R.id.tv_transation_status_desc, transactionStatus.getStatusDesc(context, transaction.getSignedBlockNumber(), 12));
            viewHolder.setTextColor(R.id.tv_transation_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
            viewHolder.setText(R.id.tv_transaction_amount, String.format("%s%s", isReceiver ? "+" : "-", context.getString(R.string.amount_with_unit, NumberParserUtil.getPrettyBalance(transaction.getSendAmount()))));
        }
    }

    public void notifyDataChanged(List<Transaction> transactionList, String walletAddress) {
        this.mDatas = transactionList;
        this.walletAddress = walletAddress;
        notifyDataSetChanged();
    }

    @Override
    public void updateItemView(Context context, int position, ViewHolder viewHolder) {

        super.updateItemView(context, position, viewHolder);

        Transaction transaction = mDatas.get(position);
        TransactionStatus transactionStatus = transaction.getTransactionStatus();

        viewHolder.setText(R.id.tv_transation_status_desc, transactionStatus.getStatusDesc(context, transaction.getSignedBlockNumber(), 12));
        viewHolder.setTextColor(R.id.tv_transation_status_desc, ContextCompat.getColor(context, transactionStatus.getStatusDescTextColor()));
    }
}
