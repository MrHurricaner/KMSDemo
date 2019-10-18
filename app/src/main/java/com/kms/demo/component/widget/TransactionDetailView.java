package com.kms.demo.component.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.kms.demo.entity.Transaction;

import androidx.annotation.Nullable;

/**
 * @author matrixelement
 */
public abstract class TransactionDetailView extends LinearLayout {

    private Context context;

    public TransactionDetailView(Context context) {
        this(context, null, 0);
    }

    public TransactionDetailView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransactionDetailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
    }

    public abstract void showDetail(Transaction transaction, String currentWalletAddress);
}
