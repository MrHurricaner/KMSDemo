package com.kms.demo.component.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.R;
import com.kms.demo.component.ui.base.BaseActivity;

import androidx.core.content.ContextCompat;


/**
 * @author matrixelement
 */
public class CommonTitleBar extends LinearLayout {

    private final static int DEFAULT_TITLE_SIZE = 16;
    private final static int DEFAULT_RIGHT_TITLE_SIZE = 11;
    private TextView tvTitle;
    private ImageView ivLeft;
    private ImageView ivRight;
    private TextView tvRight;
    private Context context;
    private CharSequence title;
    private Drawable leftDrawable;
    private Drawable rightDrawable;
    private int titleColor;
    private float titleSize;
    private CharSequence rightText;
    private int rightTextColor;
    private int rightTextBg;
    private float rightTextSize;

    public CommonTitleBar(Context context) {
        this(context, null, 0);
        init(context);
    }

    public CommonTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public CommonTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CommonTitleBar, defStyleAttr, 0);

        title = ta.getText(R.styleable.CommonTitleBar_ctb_title);
        leftDrawable = ta.getDrawable(R.styleable.CommonTitleBar_ctb_left_drawable);
        rightDrawable = ta.getDrawable(R.styleable.CommonTitleBar_ctb_right_drawable);
        titleColor = ta.getColor(R.styleable.CommonTitleBar_ctb_title_color, ContextCompat.getColor(context, R.color.color_ffffff));
        titleSize = ta.getDimensionPixelSize(R.styleable.CommonTitleBar_ctb_title_size, DensityUtil.sp2px(context, DEFAULT_TITLE_SIZE));
        rightText = ta.getText(R.styleable.CommonTitleBar_ctb_right_text);
        rightTextBg = ta.getResourceId(R.styleable.CommonTitleBar_ctb_right_text_background, -1);
        rightTextColor = ta.getColor(R.styleable.CommonTitleBar_ctb_right_text_color, ContextCompat.getColor(context, R.color.color_a9adca));
        rightTextSize = ta.getDimensionPixelSize(R.styleable.CommonTitleBar_ctb_right_text_size, DensityUtil.sp2px(context, DEFAULT_RIGHT_TITLE_SIZE));

        ta.recycle();

        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.layout_title_bar, this);
        tvTitle = findViewById(R.id.tv_title);
        ivLeft = findViewById(R.id.iv_left);
        ivRight = findViewById(R.id.iv_right);
        tvRight = findViewById(R.id.tv_right);

        setTitle(title);
        setTitleColor(titleColor);
        setTitleSize(titleSize);
        setLeftDrawable(leftDrawable);
        setRightDrawable(rightDrawable);
        setRightText(rightText);
        setRightTextSize(rightTextSize);
        setRightTextBackground(rightTextBg);
        setRightTextColor(rightTextColor);
    }

    public void setRightTextColor(int rightTextColor) {
        if (tvRight.getVisibility() == VISIBLE) {
            tvRight.setTextColor(rightTextColor);
        }
    }

    public CommonTitleBar setRightTextSize(float size) {
        if (tvRight.getVisibility() == VISIBLE) {
            tvRight.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
        return this;
    }

    public void setRightTextBackground(int rightTextBg) {
        if (rightTextBg != -1) {
            tvRight.setBackgroundResource(rightTextBg);
        }
    }

    public CommonTitleBar setTitle(String middleTitle) {
        if (TextUtils.isEmpty(middleTitle)) {
            tvTitle.setVisibility(GONE);
        } else {
            tvTitle.setText(middleTitle);
            tvTitle.setVisibility(VISIBLE);
        }

        return this;
    }

    public CommonTitleBar setTitle(CharSequence middleTitle) {
        if (TextUtils.isEmpty(middleTitle)) {
            tvTitle.setVisibility(GONE);
        } else {
            tvTitle.setText(middleTitle);
            tvTitle.setVisibility(VISIBLE);
        }

        tvTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof Activity) {
                    BaseActivity baseActivity = (BaseActivity) context;
                    baseActivity.hideSoftInput();
                    baseActivity.finish();
                }
            }
        });

        return this;
    }

    public CommonTitleBar setRightText(CharSequence rightText) {

        if (TextUtils.isEmpty(rightText)) {
            tvRight.setVisibility(GONE);
        } else {
            tvRight.setText(rightText);
            tvRight.setVisibility(VISIBLE);
        }

        return this;
    }

    public CommonTitleBar setRightText(CharSequence rightText, OnClickListener listener) {

        tvRight.setOnClickListener(listener);

        if (TextUtils.isEmpty(rightText)) {
            tvRight.setVisibility(GONE);
        } else {
            tvRight.setText(rightText);
            tvRight.setVisibility(VISIBLE);
        }

        return this;
    }

    public CommonTitleBar setTitleColor(int color) {
        if (tvTitle.getVisibility() == VISIBLE) {
            tvTitle.setTextColor(color);
        }
        return this;
    }

    public CommonTitleBar setTitleSize(float size) {
        if (tvTitle.getVisibility() == VISIBLE) {
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
        return this;
    }

    public CommonTitleBar setLeftDrawable(int leftImage) {

        ivLeft.setOnClickListener(v -> {
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        });

        if (leftImage == -1) {
            ivLeft.setVisibility(GONE);
        } else {
            ivLeft.setImageResource(leftImage);
            ivLeft.setVisibility(VISIBLE);
        }

        return this;
    }

    public void setLeftDrawable(Drawable leftDrawable) {

        ivLeft.setOnClickListener(v -> {
            if (context instanceof BaseActivity) {
                BaseActivity baseActivity = (BaseActivity) context;
                baseActivity.hideSoftInput();
                baseActivity.finish();
            }
        });

        if (leftDrawable == null) {
            ivLeft.setVisibility(GONE);
        } else {
            ivLeft.setBackgroundDrawable(leftDrawable);
            ivLeft.setVisibility(VISIBLE);
        }
    }

    public void setRightDrawable(Drawable rightDrawable) {
        if (rightDrawable == null) {
            ivRight.setVisibility(GONE);
        } else {
            ivRight.setBackgroundDrawable(rightDrawable);
            ivRight.setVisibility(VISIBLE);
        }
    }

    public CommonTitleBar setRightDrawable(int rightImage, OnClickListener listener) {

        ivRight.setOnClickListener(listener);

        if (rightImage == -1) {
            ivRight.setVisibility(GONE);
        } else {
            ivRight.setImageResource(rightImage);
            ivRight.setVisibility(VISIBLE);
        }

        return this;
    }

    public void setLeftImageOnClickListener(OnClickListener listener) {
        if (ivLeft.getVisibility() == VISIBLE) {
            ivLeft.setOnClickListener(listener);
        }
    }

    public void setRightImageOnClickListener(OnClickListener listener) {
        if (ivRight.getVisibility() == VISIBLE) {
            ivRight.setOnClickListener(listener);
        }
    }

    public void setRightTextOnClickListener(OnClickListener listener) {
        if (tvRight.getVisibility() == VISIBLE) {
            tvRight.setOnClickListener(listener);
        }
    }

    public void build() {
        if (context instanceof BaseActivity) {
            ((BaseActivity) context).getContentView().addView(this, 0);
        }
    }

}
