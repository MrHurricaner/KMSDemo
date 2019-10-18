package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jakewharton.rxbinding3.widget.RxTextView;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.adapter.TransactionListAdapter;
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.WalletDetailContract;
import com.kms.demo.component.ui.presenter.WalletDetailPresenter;
import com.kms.demo.component.widget.CommonTitleBar;
import com.kms.demo.entity.Transaction;
import com.kms.demo.entity.Wallet;
import com.kms.demo.event.Event;
import com.kms.demo.event.EventPublisher;
import com.kms.demo.utils.CommonUtil;
import com.kms.demo.utils.NumberParserUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class WalletDetailActivity extends MVPBaseActivity<WalletDetailPresenter> implements WalletDetailContract.View {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.tv_balance_amount)
    TextView tvBalanceAmount;
    @BindView(R.id.tv_balance_unit)
    TextView tvBalanceUnit;
    @BindView(R.id.iv_copy)
    ImageView ivCopy;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.list_transactions)
    ListView listTransactions;
    @BindView(R.id.btn_send_transaction)
    Button btnSendTransaction;
    @BindView(R.id.btn_receive_transaction)
    Button btnReceiveTransaction;
    @BindView(R.id.layout_empty)
    LinearLayout layoutEmpty;

    private Unbinder unbinder;
    private TransactionListAdapter transactionListAdapter;

    @Override
    protected WalletDetailPresenter createPresenter() {
        return new WalletDetailPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_detail);
        unbinder = ButterKnife.bind(this);
        EventPublisher.getInstance().register(this);
        initViews();
        mPresenter.showWalletInfo();
    }

    private void initViews() {

        commonTitleBar.setRightImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.enterManageWalletPage();
            }
        });

        transactionListAdapter = new TransactionListAdapter(R.layout.item_transactions, null);
        listTransactions.setEmptyView(layoutEmpty);
        listTransactions.setAdapter(transactionListAdapter);

        RxTextView.textChanges(tvBalanceAmount).skipInitialValue().subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                btnSendTransaction.setEnabled(NumberParserUtil.parseDouble(charSequence.toString()) > 0);
            }
        });
    }

    @OnItemClick({R.id.list_transactions})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPresenter.enterTransactionDetailPage((Transaction) parent.getAdapter().getItem(position));
    }

    @OnClick({R.id.iv_copy, R.id.tv_wallet_address, R.id.btn_send_transaction, R.id.btn_receive_transaction})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_copy:
            case R.id.tv_wallet_address:
                CommonUtil.copyTextToClipboard(this, tvWalletAddress.getText().toString());
                break;
            case R.id.btn_send_transaction:
                mPresenter.enterSendTransactionPage();
                break;
            case R.id.btn_receive_transaction:
                mPresenter.enterReceiveTransactionPage();
                break;
            default:
                break;
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

    @Override
    public Wallet getWalletFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void showWalletInfo(Wallet wallet) {
        tvBalanceAmount.setText(NumberParserUtil.getPrettyDetailBalance(wallet.getBalance()));
        tvWalletAddress.setText(wallet.getWalletAddress());
        commonTitleBar.setTitle(wallet.getName());
    }

    @Override
    public void setWalletBalance(String balance) {
        tvBalanceAmount.setText(balance);
    }

    @Override
    public void notifyDataChanged(List<Transaction> transactionList, String walletAddress) {
        Collections.sort(transactionList);
        transactionListAdapter.notifyDataChanged(transactionList, walletAddress);
    }

    @Override
    public void updateItem(Transaction transaction) {
        transactionListAdapter.updateItem(this, listTransactions, transaction);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTransactionStatusChangedEvent(Event.TransactionStatusChangedEvent event) {
        mPresenter.updateWalletTransactions(event.transaction);
    }

    public static void actionStartWithExtra(Context context, Wallet wallet) {
        Intent intent = new Intent(context, WalletDetailActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, wallet);
        context.startActivity(intent);
    }
}
