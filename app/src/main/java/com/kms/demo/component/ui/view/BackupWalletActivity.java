package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.BackupWalletContract;
import com.kms.demo.component.ui.presenter.BackupWalletPresenter;
import com.kms.demo.component.widget.RoundedTextView;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.HanziToPinyin;

import androidx.constraintlayout.widget.Group;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class BackupWalletActivity extends MVPBaseActivity<BackupWalletPresenter> implements BackupWalletContract.View {

    @BindView(R.id.tv_wallet_avatar)
    TextView tvWalletAvatar;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.rtv_dispaly_qr_code)
    RoundedTextView rtvDispalyQrCode;
    @BindView(R.id.group)
    Group group;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;

    private Unbinder unbinder;

    @Override
    protected BackupWalletPresenter createPresenter() {
        return new BackupWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_wallet);
        unbinder = ButterKnife.bind(this);
        mPresenter.showWalletInfo();
    }

    @OnClick({R.id.rtv_dispaly_qr_code, R.id.iv_qr_code, R.id.btn_backup_wallet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rtv_dispaly_qr_code:
                ivQrCode.setVisibility(View.VISIBLE);
                group.setVisibility(View.GONE);
                break;
            case R.id.iv_qr_code:
                mPresenter.copyWalletPrivateKey();
                break;
            case R.id.btn_backup_wallet:
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
    }

    @Override
    public Wallet getWalletFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void showWalletInfo(Wallet wallet) {
        tvWalletAvatar.setText(HanziToPinyin.getFirstLetter(wallet.getName()));
        tvWalletName.setText(wallet.getName());
        tvWalletAddress.setText(wallet.getWalletAddress());
    }

    @Override
    public void showWalletQrCode(Bitmap bitmap) {
        ivQrCode.setImageBitmap(bitmap);
    }

    public static void actionStart(Context context, Wallet wallet) {
        Intent intent = new Intent(context, BackupWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, wallet);
        context.startActivity(intent);
    }
}
