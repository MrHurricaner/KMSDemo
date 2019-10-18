package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kms.appcore.networkstate.NetState;
import com.kms.appcore.utils.RUtils;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.TransactionDetailContract;
import com.kms.demo.component.ui.presenter.TransactionDetailPresenter;
import com.kms.demo.component.widget.CommonTitleBar;
import com.kms.demo.component.widget.MPCTransactionDetailView;
import com.kms.demo.component.widget.TransactionDetailView;
import com.kms.demo.component.widget.TransactionFinishedDetailView;
import com.kms.demo.component.widget.TransactionPendingDetailView;
import com.kms.demo.entity.Transaction;
import com.kms.demo.entity.TransactionStatus;
import com.kms.demo.event.Event;
import com.kms.demo.event.EventPublisher;
import com.kms.demo.utils.HanziToPinyin;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class TransactionDetailActivity extends MVPBaseActivity<TransactionDetailPresenter> implements TransactionDetailContract.View {

    private static final String TAG = TransactionDetailActivity.class.getSimpleName();

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;

    private Unbinder unbinder;

    @Override
    protected TransactionDetailPresenter createPresenter() {
        return new TransactionDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        unbinder = ButterKnife.bind(this);
        mPresenter.loadTransactionDetail();
        EventPublisher.getInstance().register(this);
    }

    @Override
    public Transaction getTransactionFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_TRANSACTION);
    }

    @Override
    public String getWalletAddress() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_ADDRESS);
    }

    @Override
    public String getWalletAvatar() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_AVATAR);
    }

    @Override
    public void setTransactionWalletAvatar(String walletName, String avatar) {
        commonTitleBar.setRightText(HanziToPinyin.getFirstLetter(walletName));
        commonTitleBar.setRightTextColor(ContextCompat.getColor(this, R.color.color_ffffff));
        if (RUtils.drawable(avatar) != -1) {
            commonTitleBar.setRightTextBackground(RUtils.drawable(avatar));
        }
    }

    @Override
    public void showTransactionDetail(Transaction transaction, String walletAddress) {

        TransactionStatus transactionStatus = transaction.getTransactionStatus();

        TransactionDetailView transactionDetailView;

        if (getContentView().getChildCount() == 2) {
            getContentView().removeViewAt(1);
        }
        transactionDetailView = createTransactionDetailView(transactionStatus);

        if (transactionDetailView != null) {

            getContentView().addView(transactionDetailView);

            transactionDetailView.showDetail(transaction, walletAddress);
        }

    }

    private TransactionDetailView createTransactionDetailView(TransactionStatus transactionStatus) {

        TransactionDetailView transactionDetailView = null;

        switch (transactionStatus) {
            case MPC_EXECUTING:
            case MPC_FAILED:
                transactionDetailView = new MPCTransactionDetailView(this);
                break;
            case PENDING:
                transactionDetailView = new TransactionPendingDetailView(this);
                break;
            case FAILED:
            case COMPLETED:
            case PENDING_FAILED:
                transactionDetailView = new TransactionFinishedDetailView(this);
                break;
        }

        return transactionDetailView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransactionStatusChangedEvent(Event.TransactionStatusChangedEvent event) {
        mPresenter.updateTransactionDetail(event.transaction);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkStateChangedEvent(Event.NetWorkStateChangedEvent event) {
        NetState netState = event.netState;
        if (netState == NetState.CONNECTED) {
            mPresenter.refreshTransactionDetail();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventPublisher.getInstance().unRegister(this);
    }

    public static void actionStartWithExtra(Context context, Transaction transaction, String walletAddress, String walletAvatar) {
        Intent intent = new Intent(context, TransactionDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_TRANSACTION, transaction);
        intent.putExtra(Constants.Extra.EXTRA_ADDRESS, walletAddress);
        intent.putExtra(Constants.Extra.EXTRA_AVATAR, walletAvatar);
        context.startActivity(intent);
    }


}
