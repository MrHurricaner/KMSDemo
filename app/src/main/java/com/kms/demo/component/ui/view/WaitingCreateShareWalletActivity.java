package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kms.demo.R;
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.WaitingCreateShareWalletContract;
import com.kms.demo.component.ui.dialog.SharePublicKeyDialogFragment;
import com.kms.demo.component.ui.presenter.WaitingCreateShareWalletPresenter;
import com.kms.demo.entity.Wallet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class WaitingCreateShareWalletActivity extends MVPBaseActivity<WaitingCreateShareWalletPresenter> implements WaitingCreateShareWalletContract.View {


    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;

    private Unbinder unbinder;

    @Override
    protected WaitingCreateShareWalletPresenter createPresenter() {
        return new WaitingCreateShareWalletPresenter(this);
    }

    @OnClick({R.id.iv_right, R.id.iv_qr_code, R.id.btn_backup_wallet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_right:
                SharePublicKeyDialogFragment.newInstance(null).show(getSupportFragmentManager(), "sharePublicKey");
                break;
            case R.id.iv_qr_code:
                break;
            case R.id.btn_backup_wallet:
                BackupWalletActivity.actionStart(this, new Wallet.Builder("5")
                        .walletName("Biganxin")
//                        .privateKeyPortion("asdasdafas21asda98das9d8as98d9as8duas9duasaasdasdafas21asda98das9d8as98d9as8duas9dua...")
                        .build());
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_create_share_wallet);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, WaitingCreateShareWalletActivity.class);
        context.startActivity(intent);
    }
}
