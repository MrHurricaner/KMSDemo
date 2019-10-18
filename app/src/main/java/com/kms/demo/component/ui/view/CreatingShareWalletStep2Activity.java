package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.adapter.SharedOwnerListAdapter;
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.CreatingShareWalletStep2Contract;
import com.kms.demo.component.ui.dialog.SubmitPasswordDialogFragment;
import com.kms.demo.component.ui.presenter.CreatingShareWalletStep2Presenter;
import com.kms.demo.component.widget.ListViewForScrollView;
import com.kms.demo.entity.Wallet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class CreatingShareWalletStep2Activity extends MVPBaseActivity<CreatingShareWalletStep2Presenter> implements CreatingShareWalletStep2Contract.View {

    @BindView(R.id.list_shared_owner)
    ListViewForScrollView listSharedOwner;
    @BindView(R.id.btn_create_wallet)
    Button btnCreateWallet;
    @BindView(R.id.tv_back)
    TextView tvBack;

    private Unbinder unbinder;
    private SharedOwnerListAdapter sharedOwnerListAdapter;

    @Override
    protected CreatingShareWalletStep2Presenter createPresenter() {
        return new CreatingShareWalletStep2Presenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_share_wallet_step2);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    @OnClick({R.id.btn_create_wallet, R.id.tv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create_wallet:
//                SubmitPasswordDialogFragment.getInstance(null,null).setOnConfirmPasswordBtnClickListener(new SubmitPasswordDialogFragment.OnConfirmPasswordBtnClickListener() {
//                    @Override
//                    public void OnConfirmPasswordBtnClick(SubmitPasswordDialogFragment,String password) {
//                        CreatingShareWalletStep3Activity.actionStart(CreatingShareWalletStep2Activity.this);
//                    }
//                }).show(getSupportFragmentManager(), "submitWalletPassword");
                break;
            case R.id.tv_back:
                finish();
                break;
        }
    }

    private void initViews() {

        sharedOwnerListAdapter = new SharedOwnerListAdapter(null);
        listSharedOwner.setAdapter(sharedOwnerListAdapter);

        sharedOwnerListAdapter.notifyDataChanged(getWalletList());
        sharedOwnerListAdapter.setOnScanSharedOwnersClickListener(new SharedOwnerListAdapter.OnScanSharedOwnersClickListener() {
            @Override
            public void onScanSharedOwnersClick(int position) {
                ScanQRCodeActivity.actionStartForResult(CreatingShareWalletStep2Activity.this, Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE);
            }
        });
    }

    private List<Wallet> getWalletList() {

        List<Wallet> walletList = new ArrayList<>();

        walletList.add(new Wallet.Builder("3")
                .walletName("Biganxin")
                .build());

        return walletList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CreatingShareWalletStep2Activity.class);
        context.startActivity(intent);
    }
}
