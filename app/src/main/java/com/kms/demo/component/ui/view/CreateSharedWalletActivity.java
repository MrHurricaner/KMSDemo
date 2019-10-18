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
import com.kms.demo.component.ui.contract.CreateSharedWalletContract;
import com.kms.demo.component.ui.presenter.CreateSharedWalletPresenter;
import com.kms.demo.component.widget.CustomUnderlineEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class CreateSharedWalletActivity extends MVPBaseActivity<CreateSharedWalletPresenter> implements CreateSharedWalletContract.View {

    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.et_wallet_name)
    CustomUnderlineEditText etWalletName;
    @BindView(R.id.tv_wallet_name_error)
    TextView tvWalletNameError;
    @BindView(R.id.tv_signed_user)
    TextView tvSignedUser;
    @BindView(R.id.et_signed_user)
    CustomUnderlineEditText etSignedUser;
    @BindView(R.id.tv_wallet_password)
    TextView tvWalletPassword;
    @BindView(R.id.et_wallet_password)
    CustomUnderlineEditText etWalletPassword;
    @BindView(R.id.tv_wallet_password_error)
    TextView tvWalletPasswordError;
    @BindView(R.id.tv_repeat_wallet_password)
    TextView tvRepeatWalletPassword;
    @BindView(R.id.et_repeat_wallet_password)
    CustomUnderlineEditText etRepeatWalletPassword;
    @BindView(R.id.tv_repeat_wallet_password_error)
    TextView tvRepeatWalletPasswordError;
    @BindView(R.id.btn_create_wallet)
    Button btnCreateWallet;

    private Unbinder unbinder;

    @Override
    protected CreateSharedWalletPresenter createPresenter() {
        return new CreateSharedWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shared_wallet);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {

        RxView.clicks(btnCreateWallet).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                mPresenter.createSharedWallet();
            }
        });

        RxView.focusChanges(etWalletName).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
                if (!hasFocus) {
                    mPresenter.checkWalletName();
                }
            }
        });

        RxView.focusChanges(etWalletPassword).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
                if (!hasFocus) {
                    mPresenter.checkPassword();
                }
            }
        });

        RxView.focusChanges(etRepeatWalletPassword).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
                if (!hasFocus) {
                    mPresenter.checkRepeatPassword();
                }
            }
        });

        Observable<CharSequence> walletName = RxTextView.textChanges(etWalletName).skipInitialValue();
        Observable<CharSequence> walletPassword = RxTextView.textChanges(etWalletPassword).skipInitialValue();
        Observable<CharSequence> repeatWalletPassword = RxTextView.textChanges(etRepeatWalletPassword).skipInitialValue();

        Observable.combineLatest(walletName, walletPassword, repeatWalletPassword, new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) throws Exception {
                return mPresenter.checkCreateWalletButton();
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                setCreateWalletButtonEnable(aBoolean);
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

    @Override
    public String getWalletName() {
        return etWalletName.getText().toString();
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
    public String getSignedUserNumber() {
        return etSignedUser.getText().toString();
    }

    @Override
    public void showWalletNameError(String errorMsg) {
        tvWalletNameError.setVisibility(TextUtils.isEmpty(errorMsg) ? View.GONE : View.VISIBLE);
        tvWalletNameError.setText(errorMsg);
        etWalletName.setStatus(TextUtils.isEmpty(errorMsg) ? CustomUnderlineEditText.Status.NORMAL : CustomUnderlineEditText.Status.ERROR);
    }

    @Override
    public void showWalletPasswordError(String errorMsg) {
        tvWalletPasswordError.setVisibility(TextUtils.isEmpty(errorMsg) ? View.GONE : View.VISIBLE);
        tvWalletPasswordError.setText(errorMsg);
        etWalletPassword.setStatus(TextUtils.isEmpty(errorMsg) ? CustomUnderlineEditText.Status.NORMAL : CustomUnderlineEditText.Status.ERROR);
    }

    @Override
    public void showRepeatWalletPasswordError(String errorMsg) {
        tvRepeatWalletPasswordError.setVisibility(TextUtils.isEmpty(errorMsg) ? View.GONE : View.VISIBLE);
        tvRepeatWalletPasswordError.setText(errorMsg);
        etRepeatWalletPassword.setStatus(TextUtils.isEmpty(errorMsg) ? CustomUnderlineEditText.Status.NORMAL : CustomUnderlineEditText.Status.ERROR);
    }

    @Override
    public void setCreateWalletButtonEnable(boolean enable) {
        btnCreateWallet.setEnabled(enable);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CreateSharedWalletActivity.class);
        context.startActivity(intent);
    }
}
