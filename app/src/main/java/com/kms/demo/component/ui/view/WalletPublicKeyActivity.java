package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.kms.appcore.utils.DensityUtil;
import com.kms.appcore.utils.RUtils;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.MVPBaseActivity;
import com.kms.demo.component.ui.contract.WalletPublicKeyContract;
import com.kms.demo.component.ui.dialog.SharePublicKeyDialogFragment;
import com.kms.demo.component.ui.presenter.WalletPublicKeyPresenter;
import com.kms.demo.component.widget.CommonTitleBar;
import com.kms.demo.component.widget.ShadowDrawable;
import com.kms.demo.config.PermissionConfigure;
import com.kms.demo.entity.ShareAppInfo;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.CommonUtil;
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
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class WalletPublicKeyActivity extends MVPBaseActivity<WalletPublicKeyPresenter> implements WalletPublicKeyContract.View {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.tv_wallet_avatar)
    TextView tvWalletAvatar;
    @BindView(R.id.iv_qr_code)
    ImageView ivQrCode;
    @BindView(R.id.btn_share_wallet_public_key)
    Button btnShareWalletPublicKey;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.layout_share_content)
    LinearLayout layoutShareContent;
    @BindView(R.id.layout_top_content)
    LinearLayout layoutTopContent;
    @BindView(R.id.layout_bottom_content)
    LinearLayout layoutBottomContent;

    private Unbinder unbinder;

    @Override
    protected WalletPublicKeyPresenter createPresenter() {
        return new WalletPublicKeyPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_public_key);
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
                , DensityUtil.dp2px(this, 20f),
                0,
                -DensityUtil.dp2px(this, 20f));

        ShadowDrawable.setShadowDrawableWithCornersMode(layoutBottomContent,
                ContextCompat.getColor(this, R.color.color_131520),
                DensityUtil.dp2px(this, 8f)
                , ShadowDrawable.CORNER_BOTTOM_LEFT | ShadowDrawable.CORNER_BOTTOM_RIGHT,
                ContextCompat.getColor(this, R.color.color_33050618)
                , DensityUtil.dp2px(this, 20f),
                0,
                DensityUtil.dp2px(this, 20f));

        RxView.clicks(btnShareWalletPublicKey).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                requestPermission(WalletPublicKeyActivity.this, 100, new PermissionConfigure.PermissionCallback() {
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
            }
        });
//        commonTitleBar.setRightImageOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DeleteWalletDialogFragment.getInstance(getXOffset(), getYOffset()).setOnDeleteWalletClickListener(new DeleteWalletDialogFragment.OnDeleteWalletClickListener() {
//                    @Override
//                    public void onDeleteWalletClick() {
//                        showLongToast("delete wallet");
//                    }
//                }).show(getSupportFragmentManager(), "deleteWallet");
//            }
//        });

        RxView.clicks(ivQrCode).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                CommonUtil.copyTextToClipboard(WalletPublicKeyActivity.this, mPresenter.getPk());
            }
        });
    }

    private void handleGetPermissionSuccess() {
        File imageFile = PhotoUtil.saveImageToAlbum(WalletPublicKeyActivity.this, layoutShareContent);
        if (imageFile != null) {
            showLongToast(R.string.save_image_tips);

            List<ShareAppInfo> shareAppInfoList = SystemUtil.getShareAppInfoList(this);

            if (!shareAppInfoList.isEmpty()) {
                SharePublicKeyDialogFragment.newInstance((ArrayList<ShareAppInfo>) shareAppInfoList).show(getSupportFragmentManager(), "sharePublicKey");
            }
        }
    }

    /**
     * 获取ivRight距离屏幕右边的距离
     *
     * @return
     */
    private int getXOffset() {
        return DensityUtil.dp2px(getContext(), 16f);
    }

    /**
     * 获取ivRight距离屏幕顶部的距离
     *
     * @return
     */
    private int getYOffset() {
        return ivRight.getTop() + DensityUtil.dp2px(getContext(), 17f);
    }

    @Override
    public Wallet getWalletFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void showWalletInfo(Wallet wallet) {
        tvWalletAvatar.setBackgroundResource(RUtils.drawable(wallet.getLargeAvatar()));
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
        Intent intent = new Intent(context, WalletPublicKeyActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, wallet);
        context.startActivity(intent);
    }
}
