package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.kms.demo.R;
import com.kms.demo.component.adapter.CommonAdapter;
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.CreatingShareWalletStep3Contract;
import com.kms.demo.component.ui.presenter.CreatingShareWalletStep3Presenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class CreatingShareWalletStep3Activity extends MVPBaseActivity<CreatingShareWalletStep3Presenter> implements CreatingShareWalletStep3Contract.View {

    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.list_shared_owner)
    ListView listSharedOwner;

    private Unbinder unbinder;

    @Override
    protected CreatingShareWalletStep3Presenter createPresenter() {
        return new CreatingShareWalletStep3Presenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_share_wallet_step3);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CreatingShareWalletStep3Activity.class);
        context.startActivity(intent);
    }
}
