package com.kms.demo.component.ui.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.R;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.config.AppSettings;
import com.kms.demo.utils.CommonUtil;

import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * @author matrixelement
 */
public class SplashActivity extends BaseActivity {

    private ImageView mIvLaunchLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FIX: 以下代码是为了解决Android自level 1以来的[安装完成点击“Open”后导致的应用被重复启动]的Bug
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
                    intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                finish();
            }
        }

        setContentView(R.layout.activity_splash);

        initViews();
    }

    private void initViews() {

        mIvLaunchLogo = findViewById(R.id.iv_launch_logo);

        int marginTop;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            marginTop = DensityUtil.dp2px(this, 130);
        } else {
            marginTop = DensityUtil.dp2px(this, 130) - CommonUtil.getStatusBarHeight(this);
        }

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mIvLaunchLogo.getLayoutParams();

        layoutParams.topMargin = marginTop;

        mIvLaunchLogo.setLayoutParams(layoutParams);

        mDecorView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AppSettings.getInstance().getFirstEnter()) {
                    WelcomeActivity.actionStart(SplashActivity.this);
                    AppSettings.getInstance().setFirstEnter(false);
                } else {
                    ActivateAccountActivity.actionStart(SplashActivity.this);
                }

                finish();

            }
        }, 3000);

    }
}
