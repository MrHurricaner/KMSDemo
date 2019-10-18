package com.kms.demo.component.adapter;

import android.content.Context;
import android.view.View;

import com.kms.demo.R;
import com.kms.demo.component.adapter.base.ItemViewDelegate;
import com.kms.demo.component.adapter.base.MultiItemTypeAdapter;
import com.kms.demo.component.adapter.base.ViewHolder;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.HanziToPinyin;

import java.util.List;

/**
 * @author matrixelement
 */
public class SharedOwnerListAdapter extends MultiItemTypeAdapter<Wallet> {

    private OnScanSharedOwnersClickListener mListener;

    public void setOnScanSharedOwnersClickListener(OnScanSharedOwnersClickListener mListener) {
        this.mListener = mListener;
    }

    public SharedOwnerListAdapter(List<Wallet> datas) {

        super(datas);

        addItemViewDelegate(new ItemViewDelegate<Wallet>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_shared_owner;
            }

            @Override
            public boolean isForViewType(Wallet item, int position) {
                return !item.isNull();
            }

            @Override
            public void convert(Context context, ViewHolder holder, Wallet wallet, int position) {
                if (wallet != null) {
                    holder.setText(R.id.tv_shared_owner, context.getString(R.string.owner, position));
                    holder.setText(R.id.tv_wallet_avatar, HanziToPinyin.getFirstLetter(wallet.getName()));
                    holder.setText(R.id.tv_wallet_name, wallet.getName());
                    holder.setText(R.id.tv_wallet_address, wallet.getWalletAddress());
                }
            }
        });

        addItemViewDelegate(new ItemViewDelegate<Wallet>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_empty_shared_owner;
            }

            @Override
            public boolean isForViewType(Wallet item, int position) {
                return item.isNull();
            }

            @Override
            public void convert(Context context, ViewHolder holder, Wallet wallet, int position) {
                holder.setText(R.id.tv_shared_owner, context.getString(R.string.owner, position));
                holder.setOnClickListener(R.id.tv_scan_shared_owner, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onScanSharedOwnersClick(position);
                        }
                    }
                });
            }
        });
    }

    public interface OnScanSharedOwnersClickListener {

        void onScanSharedOwnersClick(int position);
    }
}
