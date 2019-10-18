package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.widget.CommonTitleBar;
import com.kms.demo.entity.Wallet;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class CreateOrRestoreEscrowWalletSucceedActivity extends BaseActivity {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.btn_view_wallet_details)
    Button btnViewWalletDetails;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindString(R.string.create_success)
    String createWalletSuccess;
    @BindString(R.string.restore_success)
    String restoreWalletSuccess;
    @BindString(R.string.create_custodian_wallet)
    String createEscrowWallet;
    @BindString(R.string.restore_custodian_wallet)
    String restoreEscrowWallet;

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_restore_escrow_wallet_succeed);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {

        Wallet wallet = getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
        String action = getIntent().getAction();

        commonTitleBar.setTitle(Constants.Action.ACTION_CREATE_ESCROW_WALLET.equals(action) ? createEscrowWallet : restoreEscrowWallet);
        tvResult.setText(Constants.Action.ACTION_CREATE_ESCROW_WALLET.equals(action) ? createWalletSuccess : restoreWalletSuccess);

        if (wallet != null) {
            tvWalletName.setText(wallet.getName());
            tvWalletAddress.setText(wallet.getWalletAddress());
        }

        RxView.clicks(btnViewWalletDetails).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                WalletDetailActivity.actionStartWithExtra(CreateOrRestoreEscrowWalletSucceedActivity.this, wallet);
                CreateOrRestoreEscrowWalletSucceedActivity.this.finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, Wallet wallet, String action) {
        Intent intent = new Intent(context, CreateOrRestoreEscrowWalletSucceedActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, wallet);
        intent.setAction(action);
        context.startActivity(intent);
    }
}
