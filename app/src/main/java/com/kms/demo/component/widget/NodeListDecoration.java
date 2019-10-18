package com.kms.demo.component.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.kms.appcore.utils.DensityUtil;
import com.kms.demo.R;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author ziv
 */
public class NodeListDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private Context mContext;
    private int mSize;

    public NodeListDecoration(Context context) {
        this.mContext = context;
        this.mSize = DensityUtil.dp2px(mContext, 1f);
        this.mDivider = new ColorDrawable(ContextCompat.getColor(context, R.color.color_1a1b28));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, mSize);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        super.onDraw(c, parent, state);
        int top;
        int bottom;
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            //获得child的布局信息
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            top = child.getBottom() + params.bottomMargin;
            bottom = top + mSize;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
