package com.kms.demo.component.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kms.demo.R;
import com.kms.demo.component.ui.base.BaseFragment;
import com.kms.demo.component.ui.dialog.SwitchLanguageDialogFragment;
import com.kms.demo.component.widget.RoundedTextView;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SettingsFragment extends BaseFragment {

    @BindView(R.id.tv_key_server)
    TextView tvKeyServer;
    @BindView(R.id.tv_node_settings)
    TextView tvNodeSettings;
    @BindView(R.id.tv_node_language)
    TextView tvNodeLanguage;
    @BindView(R.id.rtv_logout)
    RoundedTextView rtvLogout;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick({R.id.tv_key_server, R.id.tv_node_settings, R.id.tv_node_language, R.id.rtv_logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_key_server:
                break;
            case R.id.tv_node_settings:
                NodeSettingsActivity.actionStart(getActivity());
                break;
            case R.id.tv_node_language:
                SwitchLanguageDialogFragment.getInstance().show(getChildFragmentManager(), "showSwitchLanguageDialog");
                break;
            case R.id.rtv_logout:
                ActivateAccountActivity.actionStart(getActivity());
                getActivity().finish();
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
