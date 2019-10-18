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
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.JoinShareWalletContract;
import com.kms.demo.component.ui.presenter.JoinShareWalletPresenter;
import com.kms.demo.component.widget.CustomUnderlineEditText;

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
public class JoinShareWalletActivity extends MVPBaseActivity<JoinShareWalletPresenter> implements JoinShareWalletContract.View {

    @BindView(R.id.et_wallet_name)
    CustomUnderlineEditText etWalletName;
    @BindView(R.id.et_signed_user)
    CustomUnderlineEditText etSignedUser;
    @BindView(R.id.et_public_key)
    CustomUnderlineEditText etPublicKey;
    @BindView(R.id.et_wallet_password)
    CustomUnderlineEditText etWalletPassword;
    @BindView(R.id.tv_wallet_password_errorr)
    TextView tvWalletPasswordErrorr;
    @BindView(R.id.et_repeat_wallet_password)
    CustomUnderlineEditText etRepeatWalletPassword;
    @BindView(R.id.tv_repeat_wallet_password_error)
    TextView tvRepeatWalletPasswordError;
    @BindView(R.id.btn_join_shared_wallet)
    Button btnJoinSharedWallet;

    private Unbinder unbinder;

    @Override
    protected JoinShareWalletPresenter createPresenter() {
        return new JoinShareWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_share_wallet);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        RxView.focusChanges(etWalletPassword).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
                if (!hasFocus) {
                    mPresenter.checkWalletPassword();
                }
            }
        });

        RxView.focusChanges(etRepeatWalletPassword).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
                if (!hasFocus) {
                    mPresenter.checkRepeatWalletPassword();
                }
            }
        });

        Observable.combineLatest(RxTextView.textChanges(etWalletPassword).skipInitialValue(), RxTextView.textChanges(etRepeatWalletPassword).skipInitialValue(), new BiFunction<CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence, CharSequence charSequence2) throws Exception {
                return mPresenter.checkJoinShareWalletBtnEnable();
            }
        }).subscribe(new Consumer<Boolean>() {

            @Override
            public void accept(Boolean enable) throws Exception {
                btnJoinSharedWallet.setEnabled(enable);
            }
        });

        RxView.clicks(btnJoinSharedWallet).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                mPresenter.joinShareWallet();
            }
        });
    }

    @Override
    public String getWalletPassword() {
        return etWalletPassword.getText().toString();
    }

    @Override
    public String getRepeatWalletPassword() {
        return etRepeatWalletPassword.getText().toString();
    }

    @Override
    public void showWalletPasswordError(String errorMsg) {
        tvWalletPasswordErrorr.setVisibility(TextUtils.isEmpty(errorMsg) ? View.GONE : View.VISIBLE);
        tvWalletPasswordErrorr.setText(errorMsg);
        etWalletPassword.setStatus(TextUtils.isEmpty(errorMsg) ? CustomUnderlineEditText.Status.NORMAL : CustomUnderlineEditText.Status.ERROR);
    }

    @Override
    public void showRepeatWalletPasswordError(String errorMsg) {
        tvRepeatWalletPasswordError.setVisibility(TextUtils.isEmpty(errorMsg) ? View.GONE : View.VISIBLE);
        tvRepeatWalletPasswordError.setText(errorMsg);
        etRepeatWalletPassword.setStatus(TextUtils.isEmpty(errorMsg) ? CustomUnderlineEditText.Status.NORMAL : CustomUnderlineEditText.Status.ERROR);
    }

    @Override
    public void setJoinShareWalletBtnEnable(boolean enable) {
        btnJoinSharedWallet.setEnabled(enable);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, JoinShareWalletActivity.class);
        context.startActivity(intent);
    }
}
