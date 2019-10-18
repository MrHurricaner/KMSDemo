package com.kms.demo.component.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class DeleteWalletDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_delete_wallet)
    TextView tvDeleteWallet;

    private Unbinder unbinder;
    private OnDeleteWalletClickListener onDeleteWalletClickListener;

    public static DeleteWalletDialogFragment getInstance(int xOffset, int yOffset) {
        DeleteWalletDialogFragment dialogFragment = new DeleteWalletDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.Bundle.BUNDLE_X_OFFSET, xOffset);
        bundle.putInt(Constants.Bundle.BUNDLE_Y_OFFSET, yOffset);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public DeleteWalletDialogFragment setOnDeleteWalletClickListener(OnDeleteWalletClickListener listener) {
        this.onDeleteWalletClickListener = listener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        int xOffset = getArguments().getInt(Constants.Bundle.BUNDLE_X_OFFSET);
        int yOffset = getArguments().getInt(Constants.Bundle.BUNDLE_Y_OFFSET);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_delete_wallet, null);
        unbinder = ButterKnife.bind(this, contentView);
        baseDialog.setContentView(contentView);
        setGravity(Gravity.TOP | Gravity.RIGHT);
        setFullWidthEnable(false);
        setAnimation(R.style.Animation_CommonDialog);
        setxOffset(xOffset);
        setyOffset(yOffset);
        return baseDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        RxView.clicks(tvDeleteWallet).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                dismiss();
                if (onDeleteWalletClickListener != null) {
                    onDeleteWalletClickListener.onDeleteWalletClick();
                }
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

    public interface OnDeleteWalletClickListener {

        void onDeleteWalletClick();
    }
}
