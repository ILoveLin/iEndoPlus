package com.company.iendo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleViewDivider extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private int mDividerHeight = 1;//分割线高度，默认为1px
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    /**
     * 自定义分割线
     *
     * @param context
     * @param dividerHeight 分割线高度
     * @param drawableId    分割线颜色
     */
    public RecycleViewDivider(Context context, int dividerHeight, int drawableId) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
        mDivider = ContextCompat.getDrawable(context, drawableId);
        mDividerHeight = dividerHeight;
    }

    //获取分割线尺寸
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, mDividerHeight);
    }
    //绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawDecoration(c, parent);
    }
    //绘制横向 item 分割线
    private void drawDecoration(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
        }
    }
}