package com.kms.demo.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kms.appcore.utils.DensityUtil;
import com.kms.appcore.utils.RUtils;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.adapter.CommonAdapter;
import com.kms.demo.component.adapter.base.ViewHolder;
import com.kms.demo.component.ui.base.BaseDialogFragment;
import com.kms.demo.component.widget.ShadowDrawable;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.AddressFormatUtil;
import com.kms.demo.utils.HanziToPinyin;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class ChooseWaittingRestoredWalletDialogFragment extends BaseDialogFragment {

    @BindView(R.id.list_invalid_wallet)
    ListView listInvalidWallet;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;

    private Unbinder unbinder;
    private OnItemSelectedListener listener;

    public static ChooseWaittingRestoredWalletDialogFragment getInstance(List<Wallet> walletList) {
        ChooseWaittingRestoredWalletDialogFragment dialogFragment = new ChooseWaittingRestoredWalletDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.Bundle.BUNDLE_WALLET_LIST, (ArrayList<? extends Parcelable>) walletList);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public ChooseWaittingRestoredWalletDialogFragment setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_choose_restore_wallet, null, false);
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

        List<Wallet> walletList = getArguments().getParcelableArrayList(Constants.Bundle.BUNDLE_WALLET_LIST);

        listInvalidWallet.setAdapter(new CommonAdapter<Wallet>(R.layout.item_invalid_wallet, walletList) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, Wallet item, int position) {
                if (item != null) {
                    if (RUtils.drawable(item.getMediumAvatar()) != -1) {
                        viewHolder.setBackgroundRes(R.id.tv_avatar, RUtils.drawable(item.getMediumAvatar()));
                    }
                    viewHolder.setText(R.id.tv_avatar, HanziToPinyin.getFirstLetter(item.getName()));
                    viewHolder.setText(R.id.tv_wallet_name, item.getName());
                    viewHolder.setText(R.id.tv_wallet_address, AddressFormatUtil.formatAddress(item.getWalletAddress()));
                    viewHolder.setVisible(R.id.iv_selected, listInvalidWallet.getCheckedItemPosition() == position);
                }
            }
        });

        listInvalidWallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((BaseAdapter) listInvalidWallet.getAdapter()).notifyDataSetChanged();

                listInvalidWallet.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            dismiss();
                            listener.onItemSelected((Wallet) parent.getAdapter().getItem(position));
                        }
                    }
                }, 1000);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public interface OnItemSelectedListener {

        void onItemSelected(Wallet wallet);
    }
}
