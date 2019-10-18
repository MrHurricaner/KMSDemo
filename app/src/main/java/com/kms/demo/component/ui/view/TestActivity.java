package com.kms.demo.component.ui.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.R;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.widget.ShadowDrawable;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

/**
 * @author matrixelement
 */
public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fragment_send_transaction_confirm);

        ConstraintLayout layoutContent = findViewById(R.id.layout_content);

        ShadowDrawable.setShadowDrawable(layoutContent,
                ContextCompat.getColor(this, R.color.color_28000000),
                DensityUtil.dp2px(this, 8f),
                Color.parseColor("#ccff4747")
                , DensityUtil.dp2px(this, 12f),
                0,
                DensityUtil.dp2px(this, 2));
    }
}
