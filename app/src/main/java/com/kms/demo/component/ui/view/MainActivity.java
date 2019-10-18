package com.kms.demo.component.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabWidget;

import com.kms.demo.R;
import com.kms.demo.component.ui.base.BaseActivity;
import com.kms.demo.component.widget.FragmentTabHost;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author ziv
 */
public class MainActivity extends BaseActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String TAG_ESCROW_WALLET = "trust_wallet";
    private final static String TAG_SHARE_WALLET = "share_wallet";
    private final static String TAG_SETTINGS = "settings";

    @BindView(R.id.realTabContent)
    FrameLayout realTabContent;
    @BindView(android.R.id.tabhost)
    FragmentTabHost tabhost;

    private Unbinder unbinder;
    private int mCurIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {

        tabhost.setup(this, getSupportFragmentManager(), R.id.realTabContent);
        TabWidget tabWidget = findViewById(android.R.id.tabs);
        tabWidget.setDividerDrawable(null);

        tabhost.addTab(tabhost.newTabSpec(TAG_ESCROW_WALLET).setIndicator(getIndicatorView(TAG_ESCROW_WALLET, R.drawable.bg_nav_escrow_wallet)), CustodianWalletFragment.class, null);
        tabhost.addTab(tabhost.newTabSpec(TAG_SHARE_WALLET).setIndicator(getIndicatorView(TAG_SHARE_WALLET, R.drawable.bg_nav_share_wallet)), ShareWalletFragment.class, null);
        tabhost.addTab(tabhost.newTabSpec(TAG_SETTINGS).setIndicator(getIndicatorView(TAG_SETTINGS, R.drawable.bg_nav_settings)), SettingsFragment.class, null);

        tabhost.setCurrentTab(mCurIndex);

        tabWidget.getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLongToast(R.string.comming_soon);
            }
        });
    }

    private View getIndicatorView(String tag, int drawableResId) {

        LinearLayout rootView = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_main_tab_indicator, tabhost, false);
        rootView.setLayoutParams(new LinearLayout.LayoutParams(0, rootView.getLayoutParams().height, 1.0f));

        ImageView imageView = rootView.findViewById(R.id.iv_navigation);
        imageView.setImageResource(drawableResId);

        return rootView;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void actionStartWithClearTask(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void restart(Context context) {
        ((Activity) context).finish();
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
