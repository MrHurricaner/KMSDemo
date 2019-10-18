package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kms.appcore.utils.DensityUtil;
import com.kms.appcore.utils.RUtils;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.ReceiveTransactionContract;
import com.kms.demo.component.ui.dialog.SharePublicKeyDialogFragment;
import com.kms.demo.component.ui.presenter.ReceiveTransactionPresenter;
import com.kms.demo.component.widget.ShadowDrawable;
import com.kms.demo.config.PermissionConfigure;
import com.kms.demo.entity.ShareAppInfo;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.HanziToPinyin;
import com.kms.demo.utils.PhotoUtil;
import com.kms.demo.utils.SystemUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class ReceiveTransactionActivity extends MVPBaseActivity<ReceiveTransactionPresenter> implements ReceiveTransactionContract.View {

    @BindView(R.id.tv_wallet_avatar)
    TextView tvWalletAvatar;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.btn_share_qr_code)
    Button btnShareQrCode;
    @BindView(R.id.layout_share_content)
    LinearLayout layoutShareContent;
    @BindView(R.id.layout_top_content)
    RelativeLayout layoutTopContent;
    @BindView(R.id.layout_bottom_content)
    LinearLayout layoutBottomContent;

    Unbinder unbinder;

    @Override
    protected ReceiveTransactionPresenter createPresenter() {
        return new ReceiveTransactionPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_transaction);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.showWalletInfo();
    }

    private void initViews() {

        ShadowDrawable.setShadowDrawableWithCornersMode(layoutTopContent,
                ContextCompat.getColor(this, R.color.color_131520),
                DensityUtil.dp2px(this, 8f)
                , ShadowDrawable.CORNER_TOP_LEFT | ShadowDrawable.CORNER_TOP_RIGHT,
                ContextCompat.getColor(this, R.color.color_33050618)
                , DensityUtil.dp2px(this, 8f),
                0,
                -DensityUtil.dp2px(this, 8f));

        ShadowDrawable.setShadowDrawableWithCornersMode(layoutBottomContent,
                ContextCompat.getColor(this, R.color.color_131520),
                DensityUtil.dp2px(this, 8f)
                , ShadowDrawable.CORNER_BOTTOM_LEFT | ShadowDrawable.CORNER_BOTTOM_RIGHT,
                ContextCompat.getColor(this, R.color.color_33050618)
                , DensityUtil.dp2px(this, 20f),
                0,
                DensityUtil.dp2px(this, 20f));

    }

    @OnClick({R.id.iv_qr_code, R.id.btn_share_qr_code})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_qr_code:
                mPresenter.copyPublickWalletAddress();
                break;
            case R.id.btn_share_qr_code:
                requestPermission(ReceiveTransactionActivity.this, 100, new PermissionConfigure.PermissionCallback() {
                    @Override
                    public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                        handleGetPermissionSuccess();
                    }

                    @Override
                    public void onHasPermission(int what) {
                        handleGetPermissionSuccess();
                    }

                    @Override
                    public void onFail(int what, @NonNull List<String> deniedPermissions) {

                    }
                }, Constants.Permission.WRITE_STORAG);
                break;
            default:
                break;
        }
    }

    private void handleGetPermissionSuccess() {
        File imageFile = PhotoUtil.saveImageToAlbum(ReceiveTransactionActivity.this, layoutShareContent);
        if (imageFile != null) {
            showLongToast(R.string.save_image_tips);
            List<ShareAppInfo> shareAppInfoList = SystemUtil.getShareAppInfoList(this);
            if (!shareAppInfoList.isEmpty()) {
                SharePublicKeyDialogFragment.newInstance((ArrayList<ShareAppInfo>) shareAppInfoList).show(getSupportFragmentManager(), "sharePublicKeyDialog");
            }
        }
    }

    @Override
    public Wallet getWalletFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void showWalletInfo(Wallet wallet) {
        tvWalletAvatar.setBackgroundResource(RUtils.drawable(wallet.getMediumAvatar()));
        tvWalletAvatar.setText(HanziToPinyin.getFirstLetter(wallet.getName()));
        tvWalletName.setText(wallet.getName());
        tvWalletAddress.setText(wallet.getWalletAddress());
    }

    @Override
    public void showWalletQrCode(Bitmap bitmap) {
        ivQrCode.setImageBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStartWithExtra(Context context, Wallet wallet) {
        Intent intent = new Intent(context, ReceiveTransactionActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, wallet);
        context.startActivity(intent);
    }
}
