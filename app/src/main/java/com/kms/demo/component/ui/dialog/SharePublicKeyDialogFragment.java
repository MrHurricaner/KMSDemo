package com.kms.demo.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.adapter.CommonAdapter;
import com.kms.demo.component.adapter.base.ViewHolder;
import com.kms.demo.component.ui.base.BaseDialogFragment;
import com.kms.demo.entity.ShareAppInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class SharePublicKeyDialogFragment extends BaseDialogFragment {

    Unbinder unbinder;
    @BindView(R.id.gridview)
    GridView gridview;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;

    public static SharePublicKeyDialogFragment newInstance(ArrayList<ShareAppInfo> shareAppInfos) {
        SharePublicKeyDialogFragment dialogFragment = new SharePublicKeyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.Bundle.BUNDLE_SHARE_APPINFO_LIST, shareAppInfos);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_share_public_key, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setAnimation(R.style.Animation_slide_in_bottom);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        List<ShareAppInfo> shareAppInfoList = getArguments().getParcelableArrayList(Constants.Bundle.BUNDLE_SHARE_APPINFO_LIST);

        RxView.clicks(tvCancel).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                dismiss();
            }
        });

        gridview.setAdapter(new CommonAdapter<ShareAppInfo>(R.layout.item_share, shareAppInfoList) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, ShareAppInfo item, int position) {
                viewHolder.setText(R.id.tv_share, getString(item.getTitleRes()));
                viewHolder.setImageResource(R.id.iv_icon, item.getIconRes());
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShareAppInfo shareAppInfo = (ShareAppInfo) parent.getAdapter().getItem(position);
                if (shareAppInfo.actionStart(context)) {
                    dismiss();
                }
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
