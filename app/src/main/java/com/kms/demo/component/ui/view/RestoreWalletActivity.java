package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.RestoreWalletContract;
import com.kms.demo.component.ui.presenter.RestoreWalletPresenter;
import com.kms.demo.component.widget.CommonTitleBar;
import com.kms.demo.component.widget.CustomUnderlineEditText;
import com.kms.demo.entity.Wallet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class RestoreWalletActivity extends MVPBaseActivity<RestoreWalletPresenter> implements RestoreWalletContract.View {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.et_wallet_name)
    CustomUnderlineEditText etWalletName;
    @BindView(R.id.et_wallet_address)
    CustomUnderlineEditText etWalletAddress;
    @BindView(R.id.et_wallet_password)
    CustomUnderlineEditText etWalletPassword;
    @BindView(R.id.tv_wallet_password_error)
    TextView tvWalletPasswordError;
    @BindView(R.id.et_repeat_wallet_password)
    CustomUnderlineEditText etRepeatWalletPassword;
    @BindView(R.id.tv_repeat_wallet_password_error)
    TextView tvRepeatWalletPasswordError;
    @BindView(R.id.btn_restore_wallet)
    Button btnRestoreWallet;

    private Unbinder unbinder;

    @Override
    protected RestoreWalletPresenter createPresenter() {
        return new RestoreWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_wallet);
        unbinder = ButterKnife.bind(this);
        iniViews();
        mPresenter.showWalletInfo();
    }

    private void iniViews() {

        RxView.focusChanges(etWalletPassword).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean focus) throws Exception {
                if (!focus) {
                    mPresenter.checkPassword();
                }
            }
        });

        RxView.focusChanges(etRepeatWalletPassword).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean focus) throws Exception {
                if (!focus) {
                    mPresenter.checkRepeatPassword();
                }
            }
        });

        Observable<CharSequence> walletPassword = RxTextView.textChanges(etWalletPassword).skipInitialValue();
        Observable<CharSequence> repeatWalletPassword = RxTextView.textChanges(etRepeatWalletPassword).skipInitialValue();

        Observable.combineLatest(walletPassword, repeatWalletPassword, new BiFunction<CharSequence, CharSequence, Boolean>() {

            @Override
            public Boolean apply(CharSequence charSequence, CharSequence charSequence2) throws Exception {
                return !TextUtils.isEmpty(charSequence) && !TextUtils.isEmpty(charSequence2) && charSequence.length() >= 6 && charSequence2.length() >= 6;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean enable) throws Exception {
                setRestoreWalletBtnEnable(enable);
            }
        });

        RxView.clicks(btnRestoreWallet).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                mPresenter.restoreWallet();
            }
        });
    }

    @Override
    public Wallet getWalletFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public String getPassword() {
        return etWalletPassword.getText().toString();
    }

    @Override
    public String getRepeatPassword() {
        return etRepeatWalletPassword.getText().toString();
    }

    @Override
    public void showPasswordErrMsg(String errMsg) {
        tvWalletPasswordError.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tvWalletPasswordError.setText(errMsg);
        etWalletPassword.setStatus(TextUtils.isEmpty(errMsg) ? CustomUnderlineEditText.Status.NORMAL : CustomUnderlineEditText.Status.ERROR);
    }

    @Override
    public void showRepeatPasswordErrMsg(String errMsg) {
        tvRepeatWalletPasswordError.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tvRepeatWalletPasswordError.setText(errMsg);
        etRepeatWalletPassword.setStatus(TextUtils.isEmpty(errMsg) ? CustomUnderlineEditText.Status.NORMAL : CustomUnderlineEditText.Status.ERROR);
    }

    @Override
    public void setRestoreWalletBtnEnable(boolean enable) {
        btnRestoreWallet.setEnabled(enable);
    }

    @Override
    public void setWalletInfo(Wallet wallet) {
        etWalletName.setText(wallet.getName());
        etWalletAddress.setText(wallet.getWalletAddress());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStartWithExtra(Context context, Wallet wallet) {
        Intent intent = new Intent(context, RestoreWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, wallet);
        context.startActivity(intent);
    }
}
