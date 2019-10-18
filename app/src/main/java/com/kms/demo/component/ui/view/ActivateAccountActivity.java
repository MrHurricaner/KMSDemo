package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.ActivateAccountContract;
import com.kms.demo.component.ui.presenter.ActivateAccountPresenter;
import com.kms.demo.component.widget.CountDownTimer;

import java.math.BigDecimal;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class ActivateAccountActivity extends MVPBaseActivity<ActivateAccountPresenter> implements ActivateAccountContract.View {

    private final static String TAG = ActivateAccountActivity.class.getSimpleName();

    @BindView(R.id.et_phone_number)
    EditText etPhoneNumber;
    @BindView(R.id.tv_phone_number_error)
    TextView tvPhoneNumberError;
    @BindView(R.id.et_sms_code)
    EditText etSmsCode;
    @BindView(R.id.tv_sms_code_error)
    TextView tvSmsCodeError;
    @BindView(R.id.btn_activate)
    Button btnActivate;
    @BindView(R.id.tv_country_code)
    TextView tvCountryCode;
    @BindView(R.id.layout_phone_number)
    LinearLayout layoutPhoneNumber;
    @BindView(R.id.tv_count_down)
    TextView tvCountDown;
    @BindView(R.id.tv_resend)
    TextView tvResend;
    @BindView(R.id.layout_sms_code)
    LinearLayout layoutSmsCode;
    @BindColor(R.color.color_ff3030)
    int error;
    @BindColor(R.color.color_373c51)
    int normal;
    @BindView(R.id.tv_get_sms_code)
    TextView tvGetSmsCode;

    private static final long MILLIS_IN_FUTURE_TIME = 60 * 1000;
    private static final long COUNT_DOWN_INTERVAL_TIME = 1000;

    private Unbinder unbinder;
    private GradientDrawable layoutPhoneNumberGradientDrawable;
    private GradientDrawable layoutSmsCodeberGradientDrawable;
    private CountDownTimer countDownTimer;

    @Override
    protected ActivateAccountPresenter createPresenter() {
        return new ActivateAccountPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_account);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.loadCurrentCountryCode();
    }

    private void initViews() {

        countDownTimer = new CountDownTimer(MILLIS_IN_FUTURE_TIME, COUNT_DOWN_INTERVAL_TIME) {
            @Override
            public void onTick(long millisUntilFinished) {
                int leftMillisUntilFinished = BigDecimal.valueOf(millisUntilFinished).divide(BigDecimal.valueOf(1000), BigDecimal.ROUND_HALF_UP).intValue();
                tvCountDown.setText(String.format("%ds", leftMillisUntilFinished));
            }

            @Override
            public void onFinish() {
                tvCountDown.setVisibility(View.GONE);
                tvResend.setEnabled(true);
            }
        };

        layoutPhoneNumberGradientDrawable = (GradientDrawable) ((LayerDrawable) layoutPhoneNumber.getBackground()).findDrawableByLayerId(R.id.shape);
        layoutSmsCodeberGradientDrawable = (GradientDrawable) ((LayerDrawable) layoutSmsCode.getBackground()).findDrawableByLayerId(R.id.shape);

        layoutPhoneNumberGradientDrawable.setStroke(DensityUtil.dp2px(this, 1f), normal);
        layoutSmsCodeberGradientDrawable.setStroke(DensityUtil.dp2px(this, 1f), normal);

        RxView.focusChanges(etPhoneNumber).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
                if (!hasFocus) {
                    mPresenter.checkPhoneNumber();
                }
            }
        });

        RxView.focusChanges(etSmsCode).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean hasFocus) throws Exception {
                if (!hasFocus) {
                    mPresenter.checkSMSCode();
                }
            }
        });

        RxTextView.textChanges(etPhoneNumber).skipInitialValue().subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                mPresenter.checkActivateButton();
            }
        });

        RxTextView.textChanges(etSmsCode).skipInitialValue().subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                mPresenter.checkActivateButton();
            }
        });
    }


    @OnClick({R.id.tv_country_code, R.id.btn_activate, R.id.tv_get_sms_code, R.id.tv_resend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_country_code:
                SelectRegionNumberActivity.actionStartForResult(this, Constants.RequestCode.REQUEST_CODE_GET_REGION_NUMBER);
                break;
            case R.id.btn_activate:
                mPresenter.activateAccount();
                break;
            case R.id.tv_resend:
            case R.id.tv_get_sms_code:
                mPresenter.getSMSCode();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == Constants.RequestCode.REQUEST_CODE_GET_REGION_NUMBER) {
                mPresenter.updateCountryCode(String.valueOf(data.getLongExtra(Constants.Extra.EXTRA_COUNTRY_CODE, 86)));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }

        countDownTimer.cancel();
    }

    @Override
    public String getPhoneNumber() {
        return etPhoneNumber.getText().toString();
    }

    @Override
    public String getSMSCode() {
        return etSmsCode.getText().toString();
    }

    @Override
    public String getCountryCode() {
        return tvCountryCode.getText().toString();
    }

    @Override
    public void showPhoneNumberError(String errorMsg) {
        tvPhoneNumberError.setVisibility(TextUtils.isEmpty(errorMsg) ? View.GONE : View.VISIBLE);
        tvPhoneNumberError.setText(errorMsg);
        layoutPhoneNumberGradientDrawable.setStroke(DensityUtil.dp2px(this, 1f), TextUtils.isEmpty(errorMsg) ? normal : error);
    }

    @Override
    public void showSMSCodeError(String errorMsg) {
        tvSmsCodeError.setVisibility(TextUtils.isEmpty(errorMsg) ? View.GONE : View.VISIBLE);
        tvSmsCodeError.setText(errorMsg);
        layoutSmsCodeberGradientDrawable.setStroke(DensityUtil.dp2px(this, 1f), TextUtils.isEmpty(errorMsg) ? normal : error);
    }

    @Override
    public void setActivateButtonEnable(boolean enable) {
        btnActivate.setEnabled(enable);
    }

    @Override
    public void updateCountryCode(String countryCode) {
        tvCountryCode.setText(countryCode);
    }

    @Override
    public void startCountDown() {
        countDownTimer.start();
        tvCountDown.setVisibility(View.VISIBLE);
        tvResend.setVisibility(View.VISIBLE);
        tvResend.setEnabled(false);
        tvGetSmsCode.setVisibility(View.GONE);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ActivateAccountActivity.class);
        context.startActivity(intent);
    }
}
