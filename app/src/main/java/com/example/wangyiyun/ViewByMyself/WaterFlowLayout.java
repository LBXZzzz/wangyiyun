package com.example.wangyiyun.ViewByMyself;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class WaterFlowLayout extends ViewGroup {
    /**
     * 内部list为每行的view集合
     */
    private List<List<View>> mAllViews = new ArrayList<>();
    /**
     * 所有行高的集合
     */
    private List<Integer> mLineHeights = new ArrayList<>();


    public WaterFlowLayout(Context context) {
        this(context, null);
    }

    public WaterFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        int childCount = getChildCount();
        //记录控件的总宽度和总高度
        int width = 0;
        int height = 0;
        //记录每一行的宽度和高度
        int lineWidth = 0;
        int lineHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            //测量子View的宽高
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            //得到layoutParams
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            //获取子view的宽度
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            //获取子view的高度
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > widthSize-getPaddingLeft()-getPaddingRight()) {
                //换行
                width = Math.max(width, lineWidth);
                //重置行宽开辟一个新行
                lineWidth = childWidth;
                //记录行高
                height += lineHeight;
                lineHeight = childHeight;
            } else {//不换行
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

            if (i == childCount - 1) {
                //对比最后一行的宽度与记录的宽度
                width = Math.max(width, lineWidth);
                //加上最后一行的高度
                height += lineHeight;

            }
        }
        setMeasuredDimension(widthMode == MeasureSpec.AT_MOST ? width+getPaddingLeft()+getPaddingRight() : widthSize, heightMode == MeasureSpec.AT_MOST ? height+getPaddingTop()+getPaddingBottom() : heightSize);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeights.clear();

        //获取当前viewGroup的宽度
        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        List<View> lineViews = new ArrayList<>();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {

            View childView = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            if (lineWidth + childWidth + lp.leftMargin + lp.rightMargin > width-getPaddingLeft()-getPaddingRight()) {
                //记录行高
                mLineHeights.add(lineHeight);
                //记录该行所有view
                mAllViews.add(lineViews);
                //重置行宽和行高
                lineWidth = 0;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                //重置行的view集合
                lineViews = new ArrayList<>();
            }

            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(childView);
        }
        //处理最后一行
        mAllViews.add(lineViews);
        mLineHeights.add(lineHeight);

        //确定子view的位置

        int left = getPaddingLeft();
        int top = getPaddingTop();
        int lineNum = mAllViews.size();
        for (int i = 0; i < lineNum; i++) {
            //当前行所有的view
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeights.get(i);

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();
                //为子view布局
                child.layout(lc, tc, rc, bc);
                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            //换行是left清0 top累加行高
            left = getPaddingLeft();
            top += lineHeight;
        }
    }
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}


