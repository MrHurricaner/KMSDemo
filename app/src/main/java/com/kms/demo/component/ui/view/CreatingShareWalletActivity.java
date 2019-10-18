package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kms.demo.R;
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.CreatingShareWalletContract;
import com.kms.demo.component.ui.dialog.ConfirmDialogFragment;
import com.kms.demo.component.ui.dialog.SharePublicKeyDialogFragment;
import com.kms.demo.component.ui.presenter.CreatingShareWalletPresenter;
import com.kms.demo.component.widget.CommonTitleBar;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class CreatingShareWalletActivity extends MVPBaseActivity<CreatingShareWalletPresenter> implements CreatingShareWalletContract.View {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindString(R.string.notice_cancel_create_shared_wallet)
    String cancelCreateSharedWalletNotice;

    private Unbinder unbinder;

    @Override
    protected CreatingShareWalletPresenter createPresenter() {
        return new CreatingShareWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_share_wallet);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    @OnClick({R.id.tv_cancel, R.id.btn_next, R.id.iv_qr_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                ConfirmDialogFragment.getInstance(cancelCreateSharedWalletNotice).show(getSupportFragmentManager(), "confirm");
                break;
            case R.id.btn_next:
                CreatingShareWalletStep2Activity.actionStart(this);
                break;
            case R.id.iv_qr_code:
                break;
            default:
                break;
        }
    }

    private void initViews() {
        commonTitleBar.setRightImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePublicKeyDialogFragment.newInstance(null).show(getSupportFragmentManager(), "sharePublicKey");
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

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CreatingShareWalletActivity.class);
        context.startActivity(intent);
    }
}
