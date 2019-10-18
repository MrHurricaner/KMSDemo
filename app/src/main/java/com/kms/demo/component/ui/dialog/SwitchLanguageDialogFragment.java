package com.kms.demo.component.ui.dialog;

import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.R;
import com.kms.demo.component.ui.base.BaseDialogFragment;
import com.kms.demo.component.widget.ShadowDrawable;
import com.kms.demo.utils.LanguageUtil;

import java.util.Locale;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SwitchLanguageDialogFragment extends BaseDialogFragment {


    @BindView(R.id.tv_switch_chinese)
    TextView tvSwitchChinese;
    @BindView(R.id.tv_switch_english)
    TextView tvSwitchEnglish;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;

    private Unbinder unbinder;
    private Locale locale;

    public static SwitchLanguageDialogFragment getInstance() {
        SwitchLanguageDialogFragment dialogFragment = new SwitchLanguageDialogFragment();
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_switch_language, null, false);
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

        locale = LanguageUtil.getLocale(getContext());

        setSelectedLanguage(locale);
    }

    private void setSelectedLanguage(Locale locale) {
        if (Locale.CHINESE.getLanguage().equals(locale.getLanguage())) {
            tvSwitchChinese.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_selected, 0);
            tvSwitchEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            tvSwitchChinese.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            tvSwitchEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_selected, 0);
        }
    }

    @OnClick({R.id.tv_switch_chinese, R.id.tv_switch_english})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_switch_chinese:
                locale = Locale.CHINA;
                setSelectedLanguage(locale);
                LanguageUtil.switchLanguage(getContext(), locale);
                dismiss();
                break;
            case R.id.tv_switch_english:
                dismiss();
                locale = Locale.US;
                setSelectedLanguage(locale);
                LanguageUtil.switchLanguage(getContext(), locale);
                dismiss();
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
