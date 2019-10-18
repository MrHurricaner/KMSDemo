package com.kms.demo.component.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.kms.appcore.utils.RUtils;
import com.kms.demo.R;
import com.kms.demo.component.adapter.base.ViewHolder;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.AddressFormatUtil;
import com.kms.demo.utils.HanziToPinyin;

import java.util.List;

/**
 * @author matrixelement
 */
public class WalletListAdapter extends CommonAdapter<Wallet> {

    private OnRestoreClickListener mListener;

    public WalletListAdapter(int layoutId, List<Wallet> datas) {
        super(layoutId, datas);
    }

    public void setOnRestoreClickListener(OnRestoreClickListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected void convert(Context context, ViewHolder viewHolder, Wallet wallet, int position) {
        if (wallet != null) {
            if (RUtils.drawable(wallet.getMediumAvatar()) != -1) {
                viewHolder.setBackgroundRes(R.id.tv_avatar, RUtils.drawable(wallet.getMediumAvatar()));
            }
            viewHolder.setVisible(R.id.rtv_restore_wallet, TextUtils.isEmpty(wallet.getKey()));
            viewHolder.setText(R.id.tv_avatar, HanziToPinyin.getFirstLetter(wallet.getName()));
            viewHolder.setText(R.id.tv_wallet_name, wallet.getName());
            viewHolder.setText(R.id.tv_wallet_address, AddressFormatUtil.formatAddress(wallet.getWalletAddress()));
            viewHolder.setOnClickListener(R.id.rtv_restore_wallet, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onRestoreClick(position);
                    }
                }
            });
        }
    }


    public interface OnRestoreClickListener {
        void onRestoreClick(int position);
    }
}
