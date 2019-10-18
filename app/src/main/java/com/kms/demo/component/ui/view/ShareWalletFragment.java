package com.kms.demo.component.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxAdapterView;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.adapter.WalletListAdapter;
import com.kms.demo.component.ui.base.MVPBaseFragment;
import com.kms.demo.component.ui.contract.ShareWalletContract;
import com.kms.demo.component.ui.presenter.ShareWalletPresenter;
import com.kms.demo.component.widget.RoundedTextView;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class ShareWalletFragment extends MVPBaseFragment<ShareWalletPresenter> implements ShareWalletContract.View {

    @BindView(R.id.layout_create_wallet)
    LinearLayout layoutCreateWallet;
    @BindView(R.id.layout_restore_wallet)
    LinearLayout layoutRestoreWallet;
    @BindView(R.id.layout_join_wallet)
    LinearLayout layoutJoinWallet;
    @BindView(R.id.list_share_wallet)
    ListView listShareWallet;
    @BindView(R.id.rtv_create_share_wallet)
    RoundedTextView rtvCreateShareWallet;
    @BindView(R.id.layout_empty)
    LinearLayout layoutEmpty;
    Unbinder unbinder;

    private WalletListAdapter shareWalletAdapter;

    @Override
    protected void onFragmentPageStart() {

    }

    @Override
    protected ShareWalletPresenter createPresenter() {
        return new ShareWalletPresenter(this);
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_share_wallet, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        return rootView;
    }

    private void initViews() {

        shareWalletAdapter = new WalletListAdapter(R.layout.item_wallet, null);

        listShareWallet.setAdapter(shareWalletAdapter);
        listShareWallet.setEmptyView(layoutEmpty);

        RxAdapterView.itemClicks(listShareWallet).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                WalletDetailActivity.actionStartWithExtra(getActivity(), shareWalletAdapter.getList().get(integer.intValue()));
            }
        });

        RxView.clicks(layoutCreateWallet).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                CreateSharedWalletActivity.actionStart(getActivity());
            }
        });

        RxView.clicks(layoutRestoreWallet).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                ScanQRCodeActivity.actionStartForResult(ShareWalletFragment.this, Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE);
            }
        });

        RxView.clicks(layoutJoinWallet).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                JoinShareWalletActivity.actionStart(getActivity());
            }
        });

        RxView.clicks(rtvCreateShareWallet).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                CreateSharedWalletActivity.actionStart(getActivity());
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

}
