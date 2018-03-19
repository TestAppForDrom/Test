package com.drom.test.features.search;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceLineItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;
    private final Paint paint;
    private final int leftRightMargin;
    private final int spacingCenter;

    public SpaceLineItemDecoration(int spacing, int leftRightMargin, @ColorInt int color) {
        this.spacing = spacing;
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(color);
        this.leftRightMargin = leftRightMargin;
        spacingCenter = spacing >> 1;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        for (int i = 0, childCount = parent.getChildCount(); i < childCount - 1; ++i) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = child.getBottom() + params.bottomMargin + spacingCenter;
            c.drawLine(child.getLeft() + leftRightMargin, position, child.getRight() - leftRightMargin, position, paint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = spacing;
    }
}
