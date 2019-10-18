package com.kms.demo.component.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.BaseDialogFragment;
import com.kms.demo.component.widget.ShadowDrawable;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class ConfirmDialogFragment extends BaseDialogFragment {


    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_confirm_notice)
    TextView tvConfirmNotice;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;

    private Unbinder unbinder;
    private OnConfirmBtnClickListener listener;

    public static ConfirmDialogFragment getInstance(String confirmNotice) {
        ConfirmDialogFragment dialogFragment = new ConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUNDLE_CONFIRM_NOTICE, confirmNotice);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public ConfirmDialogFragment setOnConfirmBtnClickListener(OnConfirmBtnClickListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_confirmation, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setGravity(Gravity.CENTER);
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

        String confirmation = getArguments().getString(Constants.Bundle.BUNDLE_CONFIRM_NOTICE);

        tvConfirmNotice.setText(confirmation);
    }

    @OnClick({R.id.btn_confirm, R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.btn_confirm:
                if (listener != null) {
                    dismiss();
                    listener.onConfirmBtnClick();
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
