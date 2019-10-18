package com.kms.demo.component.ui.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxAdapterView;
import com.kms.appcore.networkstate.NetState;
import com.kms.demo.R;
import com.kms.demo.component.adapter.WalletListAdapter;
import com.kms.demo.component.ui.base.MVPBaseFragment;
import com.kms.demo.component.ui.contract.EscrowWalletContract;
import com.kms.demo.component.ui.dialog.ChooseWaittingRestoredWalletDialogFragment;
import com.kms.demo.component.ui.presenter.EscrowWalletPresenter;
import com.kms.demo.component.widget.RoundedTextView;
import com.kms.demo.engine.WalletManager;
import com.kms.demo.entity.Wallet;
import com.kms.demo.event.Event;
import com.kms.demo.event.EventPublisher;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class CustodianWalletFragment extends MVPBaseFragment<EscrowWalletPresenter> implements EscrowWalletContract.View {

    private final static String TAG = CustodianWalletFragment.class.getSimpleName();

    @BindView(R.id.layout_create_wallet)
    LinearLayout layoutCreateWallet;
    @BindView(R.id.layout_restore_wallet)
    LinearLayout layoutRestoreWallet;
    @BindView(R.id.list_escrow_wallet)
    ListView listEscrowWallet;
    @BindView(R.id.rtv_create_escrow_wallet)
    RoundedTextView rtvCreateEscrowWallet;
    @BindView(R.id.layout_empty)
    LinearLayout layoutEmpty;

    Unbinder unbinder;
    WalletListAdapter escrowWalletListAdapter;

    @Override
    protected EscrowWalletPresenter createPresenter() {
        return new EscrowWalletPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.loadWalletList();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custodian_wallet, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        initViews();
        return rootView;
    }

    private void initViews() {

        escrowWalletListAdapter = new WalletListAdapter(R.layout.item_wallet, null);

        listEscrowWallet.setAdapter(escrowWalletListAdapter);
        listEscrowWallet.setEmptyView(layoutEmpty);

        escrowWalletListAdapter.setOnRestoreClickListener(new WalletListAdapter.OnRestoreClickListener() {
            @Override
            public void onRestoreClick(int position) {
                RestoreWalletActivity.actionStartWithExtra(getActivity(), escrowWalletListAdapter.getList().get(position));
            }
        });

        RxAdapterView.itemClicks(listEscrowWallet).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Wallet wallet = escrowWalletListAdapter.getList().get(integer.intValue());
                if (TextUtils.isEmpty(wallet.getKey())) {
                    RestoreWalletActivity.actionStartWithExtra(getActivity(), wallet);
                } else {
                    WalletDetailActivity.actionStartWithExtra(getActivity(), wallet);
                }
            }
        });

        RxView.clicks(layoutCreateWallet).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                CreateEscrowWalletActivity.actionStart(getActivity());
            }
        });

        RxView.clicks(layoutRestoreWallet).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {

                ArrayList<Wallet> walletList = (ArrayList<Wallet>) WalletManager.getInstance().getWaittingRestoredWalletList();

                ArrayList<Wallet> copyWalletList = (ArrayList<Wallet>) walletList.clone();

                if (copyWalletList.isEmpty()) {
                    showLongToast(R.string.no_valid_wallet);
                } else {
                    Collections.sort(copyWalletList);
                    if (copyWalletList.size() == 1) {
                        RestoreWalletActivity.actionStartWithExtra(getContext(), copyWalletList.get(0));
                    } else {
                        ChooseWaittingRestoredWalletDialogFragment.getInstance(copyWalletList).setOnItemSelectedListener(new ChooseWaittingRestoredWalletDialogFragment.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(Wallet wallet) {
                                RestoreWalletActivity.actionStartWithExtra(getContext(), wallet);
                            }
                        }).show(getChildFragmentManager(), "showChooseWaittingRestoreWalletDialog");
                    }
                }
            }
        });

        RxView.clicks(rtvCreateEscrowWallet).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                CreateEscrowWalletActivity.actionStart(getActivity());
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkStateChangedEvent(Event.NetWorkStateChangedEvent event) {
        NetState netState = event.netState;
        if (netState == NetState.CONNECTED) {
            mPresenter.loadWalletList();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }

        EventPublisher.getInstance().unRegister(this);
    }

    @Override
    public void notifyDataSetChanged(List<Wallet> walletList) {
        Collections.sort(walletList);
        escrowWalletListAdapter.notifyDataChanged(walletList);
    }
}
