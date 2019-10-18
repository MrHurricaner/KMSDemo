package com.kms.demo.component.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.R;
import com.kms.demo.app.BusinessApiSingleObserver;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.BaseDialogFragment;
import com.kms.demo.component.widget.ShadowDrawable;
import com.kms.demo.component.widget.VerificationCodeInput;
import com.kms.demo.engine.KYCManager;
import com.kms.demo.engine.VerificationCodeManager;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class AuthenticationDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_phone_number)
    TextView tvPhoneNumber;
    @BindView(R.id.verificationCodeInput)
    VerificationCodeInput verificationCodeInput;
    @BindView(R.id.tv_resend)
    TextView tvResend;
    @BindView(R.id.tv_count_down)
    TextView tvCountDown;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;


    private Unbinder unbinder;
    private OnVerificationCodeCompleteListener onVerificationCodeCompleteListener;

    public AuthenticationDialogFragment setOnVerificationCodeCompleteListener(OnVerificationCodeCompleteListener onVerificationCodeCompleteListener) {
        this.onVerificationCodeCompleteListener = onVerificationCodeCompleteListener;
        return this;
    }

    public static AuthenticationDialogFragment getInstance(String phoneNumber, String countryCode, KYCManager.KYCOperation operation) {
        AuthenticationDialogFragment dialogFragment = new AuthenticationDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUNDLE_PHONE_NUMBER, phoneNumber);
        bundle.putString(Constants.Bundle.BUNDLE_COUNTRY_CODE, countryCode);
        bundle.putParcelable(Constants.Bundle.BUNDLE_KYC_OPERATION, operation);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_authentication, null, false);
        baseDialog.setContentView(contentView);
        setGravity(Gravity.BOTTOM);
        setAnimation(R.style.Animation_slide_in_bottom);
        setFullWidthEnable(true);
        setHorizontalMargin(DensityUtil.dp2px(getContext(), 12f));
        setyOffset(DensityUtil.dp2px(getContext(), 2f));
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        ShadowDrawable.setShadowDrawable(layoutContent,
                ContextCompat.getColor(getContext(), R.color.color_1b1e2c),
                DensityUtil.dp2px(getContext(), 8f),
                ContextCompat.getColor(getContext(), R.color.color_8006061c)
                , DensityUtil.dp2px(getContext(), 12f),
                0,
                DensityUtil.dp2px(getContext(), 2));

        String phoneNumber = getArguments().getString(Constants.Bundle.BUNDLE_PHONE_NUMBER);
        String countryCode = getArguments().getString(Constants.Bundle.BUNDLE_COUNTRY_CODE);
        KYCManager.KYCOperation operation = getArguments().getParcelable(Constants.Bundle.BUNDLE_KYC_OPERATION);

        tvPhoneNumber.setText(String.format("%s %s", countryCode, phoneNumber));

        VerificationCodeManager.getInstance().setOnCountDownTimerListener(new VerificationCodeManager.OnCountDownTimerListener() {
            @Override
            public void onTick(int millisLeft) {

                if (tvResend != null) {
                    tvResend.setEnabled(millisLeft == 0);
                }

                if (tvCountDown != null) {
                    tvCountDown.setText(String.format("%ds", millisLeft));
                    tvCountDown.setVisibility(millisLeft > 0 ? View.VISIBLE : View.GONE);
                }
            }
        });

        RxView.clicks(tvResend).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {

                VerificationCodeManager.getInstance()
                        .sendSmsCode(AuthenticationDialogFragment.this, phoneNumber, operation.toString())
                        .subscribe(new BusinessApiSingleObserver(getContext()) {
                            @Override
                            public void onApiSuccess(Object o) {
                                super.onApiSuccess(o);
                            }
                        });
            }
        });

        RxView.clicks(btnCancel).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                dismiss();
            }
        });

        verificationCodeInput.setOnCompleteListener(new VerificationCodeInput.Listener() {
            @Override
            public void onComplete(String content) {
                if (onVerificationCodeCompleteListener != null) {
                    onVerificationCodeCompleteListener.onComplete(AuthenticationDialogFragment.this, content);
                }
            }
        });
    }

    public void clearVerificationCode() {
        verificationCodeInput.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        VerificationCodeManager.getInstance().removeOnCountDownTimerListener();
    }

    public interface OnVerificationCodeCompleteListener {
        void onComplete(AuthenticationDialogFragment baseDialogFragment, String smsCode);
    }

}
