package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kms.appcore.utils.DensityUtil;
import com.kms.appcore.utils.RUtils;
import com.kms.demo.R;
import com.kms.demo.app.Constants;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.widget.CommonTitleBar;
import com.kms.demo.component.widget.ShadowDrawable;
import com.kms.demo.entity.Wallet;
import com.kms.demo.utils.DateUtil;
import com.kms.demo.utils.HanziToPinyin;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class ManageWalletActivity extends BaseActivity {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.tv_wallet_avatar)
    TextView tvWalletAvatar;
    @BindView(R.id.tv_sub_wallet_name)
    TextView tvSubWalletName;
    @BindView(R.id.tv_wallet_mode)
    TextView tvWalletMode;
    @BindView(R.id.tv_wallet_create_time)
    TextView tvWalletCreateTime;
    @BindView(R.id.btn_backup_wallet)
    Button btnBackupWallet;
    @BindView(R.id.btn_view_wallet_public_key)
    Button btnViewWalletPublicKey;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.layout_wallet_short_info)
    LinearLayout layoutWalletShortInfo;
    @BindView(R.id.layout_wallet_info)
    LinearLayout layoutWalletInfo;

    private Unbinder unbinder;
    private Wallet wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_wallet);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {

        ShadowDrawable.setShadowDrawableWithCornersMode(layoutWalletShortInfo,
                ContextCompat.getColor(this, R.color.color_131520),
                DensityUtil.dp2px(this, 8f)
                , ShadowDrawable.CORNER_TOP_LEFT | ShadowDrawable.CORNER_TOP_RIGHT,
                ContextCompat.getColor(this, R.color.color_33050618)
                , DensityUtil.dp2px(this, 20f),
                0,
                -DensityUtil.dp2px(this, 20f));

        ShadowDrawable.setShadowDrawableWithCornersMode(layoutWalletInfo,
                ContextCompat.getColor(this, R.color.color_131520),
                DensityUtil.dp2px(this, 8f)
                , ShadowDrawable.CORNER_BOTTOM_LEFT | ShadowDrawable.CORNER_BOTTOM_RIGHT,
                ContextCompat.getColor(this, R.color.color_33050618)
                , DensityUtil.dp2px(this, 20f),
                0,
                DensityUtil.dp2px(this, 20f));

        wallet = getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);

        if (wallet != null) {
            tvWalletAvatar.setBackgroundResource(RUtils.drawable(wallet.getLargeAvatar()));
            tvWalletAvatar.setText(HanziToPinyin.getFirstLetter(wallet.getName()));
            tvWalletName.setText(wallet.getName());
            tvWalletAddress.setText(wallet.getWalletAddress());
            tvSubWalletName.setText(wallet.getName());
            tvWalletCreateTime.setText(DateUtil.format(wallet.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND));
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

    @OnClick({R.id.btn_backup_wallet, R.id.btn_view_wallet_public_key})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_backup_wallet:
                BackupWalletActivity.actionStart(this, wallet);
                break;
            case R.id.btn_view_wallet_public_key:
                WalletPublicKeyActivity.actionStartWithExtra(ManageWalletActivity.this, wallet);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStartWithExtra(Context context, Wallet wallet) {
        Intent intent = new Intent(context, ManageWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, wallet);
        context.startActivity(intent);
    }
}
