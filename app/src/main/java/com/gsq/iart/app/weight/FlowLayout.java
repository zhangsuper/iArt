package com.gsq.iart.app.weight;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.gsq.iart.R;

import java.util.List;

public class FlowLayout extends ViewGroup {

    /**
     * 水平距离
     */
    private int mHorizontalSpacing = SizeUtils.dp2px(8);

    private static final int MAX_LINE = 3;//从0开始计数
    private static final int MIN_LINE = 1;//从0开始计数
    private FlowContentLayout mFlowContentLayout;
    private boolean foldState = true;
    private View upFoldView;
    private View downFoldView;
    private int mWidth;
    private int textViewHeight;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFlowContentLayout(FlowContentLayout mFlowContentLayout) {
        this.mFlowContentLayout = mFlowContentLayout;
    }

    public void setFoldState(boolean foldState) {
        this.foldState = foldState;
    }

    public void setUpFoldView(View upFoldView) {
        this.upFoldView = upFoldView;
    }

    public void setDownFoldView(View downFoldView) {
        this.downFoldView = downFoldView;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取mode 和 size
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        final int layoutWidth = widthSize - getPaddingLeft() - getPaddingRight();
        //判断如果布局宽度抛去左右padding小于0，也不能处理了
        if (layoutWidth <= 0) {
            return;
        }

        //这里默认宽高默认值默认把左右，上下padding加上
        int width = getPaddingLeft() + getPaddingRight();
        int height = getPaddingTop() + getPaddingBottom();

        //初始一行的宽度
        int lineWidth = 0;
        //初始一行的高度
        int lineHeight = 0;

        //测量子View
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int[] wh = null;
        int childWidth, childHeight;
        //行数
        int line = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View view = getChildAt(i);
            //这里需要先判断子view是否被设置了GONE
            if (view.getVisibility() == GONE) {
                continue;
            }
            childWidth = view.getMeasuredWidth();
            childHeight = view.getMeasuredHeight();

            //第一行
            if (i == 0) {
                lineWidth = getPaddingLeft() + getPaddingRight() + childWidth;
                lineHeight = childHeight;
            } else {
                //判断是否需要换行
                //换行
                if (lineWidth + mHorizontalSpacing + childWidth > widthSize) {
                    line++;//行数增加
                    // 取最大的宽度
                    width = Math.max(lineWidth, width);
                    //重新开启新行，开始记录
                    lineWidth = getPaddingLeft() + getPaddingRight() + childWidth;
                    //叠加当前高度，
                    height += lineHeight;
                    //开启记录下一行的高度
                    lineHeight = childHeight;
                    if(mFlowContentLayout != null){
                        if(foldState && line > MIN_LINE){
                            callBack(foldState,i-1, true,lineWidth);
                            break;
                        }else if(!foldState && line > MAX_LINE){
                            callBack(foldState,i-1, true,lineWidth);
                            break;
                        }
                    }
                }
                //不换行
                else {
                    lineWidth = lineWidth + mHorizontalSpacing + childWidth;
                    lineHeight = Math.max(lineHeight, childHeight);
                }
            }
            // 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
            if (i == count - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }
        //根据计算的值重新设置
        if(mFlowContentLayout == null){
            setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                    heightMode == MeasureSpec.EXACTLY ? heightSize : height);
        }else{
            setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                    0);
        }

        if(foldState && (line >= 0 && line <= MIN_LINE)){
            callBack(foldState,getChildCount(),false,lineWidth);
        }
        if(!foldState && (line >= 0 && line <= MAX_LINE)){
            if(mFlowContentLayout != null){
                int upViewWidth = mFlowContentLayout.getUpViewWidth() + mHorizontalSpacing;
                if(lineWidth > (mWidth - upViewWidth) && line == MAX_LINE){
                    callBack(foldState,getChildCount() - 1,true,lineWidth);
                }else{
                    callBack(foldState,getChildCount(),true,lineWidth);
                }
            }else{
                callBack(foldState,getChildCount(),true,lineWidth);
            }

        }
    }

    /**
     * 超过最大数的回调
     * @param foldState
     * @param index 最大数的位置。
     * @param b
     * @param lineWidthUsed
     */
    private void callBack(boolean foldState, int index, boolean b, int lineWidthUsed) {
        if(mFlowContentLayout != null){
            mFlowContentLayout.foldIndex(foldState,index,b,lineWidthUsed);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int layoutWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        if (layoutWidth <= 0) {
            return;
        }
        int childWidth, childHeight;
        //需要加上top padding
        int top = getPaddingTop();
        final int[] wh = getMaxWidthHeight();
        int lineHeight = 0;
        int line = 0;
        //左对齐
        //左侧需要先加上左边的padding
        int left = getPaddingLeft();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View view = getChildAt(i);
            //这里一样判断下显示状态
            if (view.getVisibility() == GONE) {
                continue;
            }
            //自适宽高
            childWidth = view.getMeasuredWidth();
            childHeight = view.getMeasuredHeight();
            //第一行开始摆放
            if (i == 0) {
                view.layout(left, top, left + childWidth, top + childHeight);
                lineHeight = childHeight;
            } else {
                //判断是否需要换行
                if (left + mHorizontalSpacing + childWidth > layoutWidth + getPaddingLeft()) {
                    line++;
                    //重新起行
                    left = getPaddingLeft();
                    top = top + lineHeight;
                    lineHeight = childHeight;
                } else {
                    left = left + mHorizontalSpacing;
                    lineHeight = Math.max(lineHeight, childHeight);
                }
                view.layout(left, top, left + childWidth, top + childHeight);
            }
            //累加left
            left += childWidth;
        }
    }

    /**
     * 取最大的子view的宽度和高度
     *
     * @return
     */
    private int[] getMaxWidthHeight() {
        int maxWidth = 0;
        int maxHeight = 0;
        for (int i = 0, count = getChildCount(); i < count; i++) {
            final View view = getChildAt(i);
            if (view.getVisibility() == GONE) {
                continue;
            }
            maxWidth = Math.max(maxWidth, view.getMeasuredWidth());
            maxHeight = Math.max(maxHeight, view.getMeasuredHeight());
        }
        return new int[]{maxWidth, maxHeight};
    }

    public void addViews(List<String> list){
        removeAllViews();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        for (int x = 0; x< list.size(); x++) {
            String s = list.get(x);
            if(TextUtils.equals("@@",s)){
                if(foldState){
                    if(downFoldView != null){
                        removeView(downFoldView);
//                        Utils.removeFromParent(downFoldView);
                        layoutParams.gravity = Gravity.CENTER;
                        addView(downFoldView,layoutParams);
                    }
                }else{
                    if(upFoldView != null){
                        removeView(upFoldView);
//                        Utils.removeFromParent(upFoldView);
                        layoutParams.gravity = Gravity.CENTER;
                        addView(upFoldView,layoutParams);
                    }
                }
            }else{
                addTextView(s,layoutParams);
            }

        }
    }



    private void addTextView(String s,LinearLayout.LayoutParams layoutParams){
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setPadding(0,SizeUtils.dp2px(8),0,0);
        linearLayout.setLayoutParams(layoutParams);
        TextView tv = new TextView(getContext());
        tv.setPadding(SizeUtils.dp2px(12), SizeUtils.dp2px(8), SizeUtils.dp2px(12),SizeUtils.dp2px(8));
        tv.setText(s);
        tv.setSingleLine();
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        tv.setTextColor(ColorUtils.getColor(R.drawable.selector_dictionary_tag_textcolor));
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setBackgroundResource(R.drawable.bg_dictionary_tag_selector);
        linearLayout.addView(tv,new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        addView(linearLayout,layoutParams);
//        textViewHeight = tv.getLayoutParams().height;
        textViewHeight = tv.getHeight();
    }
}
