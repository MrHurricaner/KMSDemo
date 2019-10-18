package com.kms.demo.component.ui.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.kms.appcore.utils.DensityUtil;
import com.kms.appcore.utils.RUtils;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.SendTransactionContract;
import com.kms.demo.component.ui.presenter.SendTransactionPresenter;
import com.kms.demo.component.widget.CustomUnderlineEditText;
import com.kms.demo.component.widget.ObservableScrollView;
import com.kms.demo.component.widget.PointLengthFilter;
import com.kms.demo.component.widget.RoundedTextView;
import com.kms.demo.component.widget.bubbleSeekBar.BubbleSeekBar;
import com.kms.demo.config.PermissionConfigure;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.HanziToPinyin;
import com.kms.demo.utils.NumberParserUtil;
import com.kms.demo.utils.WalletUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class SendTransactionActivity extends MVPBaseActivity<SendTransactionPresenter> implements SendTransactionContract.View {

    @BindView(R.id.scrollView_container)
    ObservableScrollView scrollView;
    @BindView(R.id.tv_wallet_avatar)
    TextView tvWalletAvatar;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.et_receive_wallet_address)
    CustomUnderlineEditText etReceiveWalletAddress;
    @BindView(R.id.tv_receive_wallet_address_error)
    TextView tvReceiveWalletAddressError;
    @BindView(R.id.et_send_amount)
    EditText etSendAmount;
    @BindView(R.id.layout_send_amount)
    LinearLayout layoutSendAmount;
    @BindView(R.id.rtv_all)
    RoundedTextView rtvAll;
    @BindView(R.id.tv_send_amount_error)
    TextView tvSendAmountError;
    @BindView(R.id.tv_available_amount)
    TextView tvAvailableAmount;
    @BindView(R.id.et_note)
    CustomUnderlineEditText etNote;
    @BindView(R.id.tv_note_error)
    TextView tvNoteError;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.bubbleSeekBar)
    BubbleSeekBar bubbleSeekBar;
    @BindView(R.id.btn_send_transaction)
    Button btnSendTransaction;
    @BindString(R.string.cheaper)
    String cheaperSectionText;
    @BindString(R.string.recommend)
    String recommendSectionText;
    @BindString(R.string.faster)
    String fasterSectionText;
    @BindColor(R.color.color_ff3030)
    int error;
    @BindColor(R.color.color_373c51)
    int normal;

    private Unbinder unbinder;
    private GradientDrawable layoutSendAmountGradientDrawable;

    @Override
    protected SendTransactionPresenter createPresenter() {
        return new SendTransactionPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_transaction);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.loadData();
    }

    private void initViews() {

        layoutSendAmountGradientDrawable = (GradientDrawable) ((LayerDrawable) layoutSendAmount.getBackground()).findDrawableByLayerId(R.id.shape);
        layoutSendAmountGradientDrawable.setStroke(DensityUtil.dp2px(this, 1f), normal);

        scrollView.setOnScrollChangedListener(new ObservableScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                bubbleSeekBar.correctOffsetWhenContainerOnScrolling();
            }
        });

        bubbleSeekBar.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
            @NonNull
            @Override
            public SparseArray<String> onCustomize(int sectionCount, @NonNull SparseArray<String> array) {
                array.clear();
                array.put(0, cheaperSectionText);
                array.put(2, recommendSectionText);
                array.put(3, fasterSectionText);
                return array;
            }
        });

        bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                mPresenter.calculateFeeAndCheckSendAmount();
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });

        etReceiveWalletAddress.setDrawableClickListener(new CustomUnderlineEditText.DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                requestPermission(SendTransactionActivity.this, 100, new PermissionConfigure.PermissionCallback() {
                    @Override
                    public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                        ScanQRCodeActivity.actionStartForResult(SendTransactionActivity.this, Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE);
                    }

                    @Override
                    public void onHasPermission(int what) {
                        ScanQRCodeActivity.actionStartForResult(SendTransactionActivity.this, Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE);
                    }

                    @Override
                    public void onFail(int what, @NonNull List<String> deniedPermissions) {

                    }
                }, Manifest.permission.CAMERA);
            }
        });

        etSendAmount.setFilters(new InputFilter[]{new PointLengthFilter()});


        RxView.clicks(rtvAll).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                mPresenter.sendAllBalance();
            }
        });

        RxView.clicks(btnSendTransaction).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                mPresenter.sendTransaction();
            }
        });

        RxView.focusChanges(etReceiveWalletAddress).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean focus) throws Exception {
                if (!focus) {
                    mPresenter.checkReceiveAddress();
                }
            }
        });

        RxView.focusChanges(etSendAmount).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean focus) throws Exception {
                if (!focus) {
                    mPresenter.checkSendAmount();
                }
            }
        });

        RxView.focusChanges(etNote).skipInitialValue().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean focus) throws Exception {
                if (!focus) {
                    mPresenter.checkNote();
                }
            }
        });

        RxTextView.textChanges(etNote).skipInitialValue().subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                mPresenter.calculateFee();
            }
        });

        Observable<CharSequence> sendAmountObs = RxTextView.textChanges(etSendAmount).skipInitialValue();
        Observable<CharSequence> receiveAddressObs = RxTextView.textChanges(etReceiveWalletAddress).skipInitialValue();

        receiveAddressObs.subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                mPresenter.calculateFeeAndcheckSendTransactionBtn();
            }
        });

        sendAmountObs.subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                mPresenter.checkSendTransactionBtn();
            }
        });

    }

    @Override
    public Wallet getWalletFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void setWalletInfo(Wallet wallet) {
        tvWalletName.setText(wallet.getName());
        tvWalletAddress.setText(wallet.getWalletAddress());
        tvWalletAvatar.setText(HanziToPinyin.getFirstLetter(wallet.getName()));
        tvWalletAvatar.setBackgroundResource(RUtils.drawable(wallet.getMediumAvatar()));
        tvAvailableAmount.setText(string(R.string.amount_with_unit, NumberParserUtil.getPrettyBalance(wallet.getBalance())));
    }

    @Override
    public void setSendAmount(String sendAmount) {
        etSendAmount.setText(sendAmount);
    }

    @Override
    public void setFeeAmount(String feeAmount) {
        tvFee.setText(string(R.string.amount_with_unit, feeAmount));
    }

    @Override
    public void setSendTransactionBtnEnable(boolean enable) {
        btnSendTransaction.setEnabled(enable);
    }

    @Override
    public void showReceiveAddressError(String errMsg) {
        tvReceiveWalletAddressError.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tvReceiveWalletAddressError.setText(errMsg);
        etReceiveWalletAddress.setStatus(TextUtils.isEmpty(errMsg) ? CustomUnderlineEditText.Status.NORMAL : CustomUnderlineEditText.Status.ERROR);
    }

    @Override
    public void showSendAmountError(String errMsg) {
        tvSendAmountError.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tvSendAmountError.setText(errMsg);
        layoutSendAmountGradientDrawable.setStroke(DensityUtil.dp2px(this, 1f), TextUtils.isEmpty(errMsg) ? normal : error);
    }

    @Override
    public void showTransactionNoteError(String errMsg) {
        tvNoteError.setVisibility(TextUtils.isEmpty(errMsg) ? View.GONE : View.VISIBLE);
        tvNoteError.setText(errMsg);
        etNote.setStatus(TextUtils.isEmpty(errMsg) ? CustomUnderlineEditText.Status.NORMAL : CustomUnderlineEditText.Status.ERROR);
    }

    @Override
    public String getSendAmount() {
        return etSendAmount.getText().toString();
    }

    @Override
    public String getReceiveAddress() {
        return etReceiveWalletAddress.getText().toString();
    }

    @Override
    public String getTransactionNote() {
        return etNote.getText().toString();
    }

    @Override
    public float getProgressFloat() {
        return bubbleSeekBar.getProgressFloat();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE) {
                String receiveAddress = data.getStringExtra(Constants.Extra.EXTRA_SCAN_QRCODE_DATA);
                if (!WalletUtil.isValidAddress(receiveAddress)) {
                    showLongToast(R.string.invalid_qr_code);
                } else {
                    etReceiveWalletAddress.setText(receiveAddress);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, Wallet wallet) {
        Intent intent = new Intent(context, SendTransactionActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, wallet);
        context.startActivity(intent);
    }
}
