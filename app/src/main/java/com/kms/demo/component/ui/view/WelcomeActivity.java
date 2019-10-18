package com.kms.demo.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jakewharton.rxbinding3.view.RxView;
import com.kms.demo.R;
import com.kms.demo.component.ui.base.BaseActivity;
import com.opensource.svgaplayer.SVGACallback;
import com.opensource.svgaplayer.SVGAImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class WelcomeActivity extends BaseActivity {

    private final static String TAG = WelcomeActivity.class.getSimpleName();

    @BindView(R.id.btn_activate)
    Button btnActivate;
    @BindView(R.id.animation_view)
    SVGAImageView animationView;
    @BindView(R.id.iv_default_guide_page)
    ImageView ivDefaultGuidePage;

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        unbinder = ButterKnife.bind(this);

        animationView.setLayerType(View.LAYER_TYPE_SOFTWARE, new Paint());

        RxView.clicks(btnActivate).subscribe(new Consumer<Unit>() {
            @Override
            public void accept(Unit unit) throws Exception {
                ActivateAccountActivity.actionStart(WelcomeActivity.this);
            }
        });

        animationView.setCallback(new SVGACallback() {
            @Override
            public void onPause() {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onRepeat() {

            }

            @Override
            public void onStep(int i, double v) {
                if (ivDefaultGuidePage.getVisibility() == View.VISIBLE) {
                    ivDefaultGuidePage.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, WelcomeActivity.class);
        context.startActivity(intent);
    }
}
