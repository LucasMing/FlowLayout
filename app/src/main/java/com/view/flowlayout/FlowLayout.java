package com.view.flowlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLayout extends ViewGroup {

    private static String TAG="FlowLayout";
    //每个item横向间距
    private int mHorizontalSpacing=DisplayUtil.dp2px(16);
    //每个item纵向间距
    private int mVerticalSpacing=DisplayUtil.dp2px(8);
    private List<List<View>>allLines;//记录所有的行，一行一行存储 用于onLayout
    private List<Integer>allHeights;//记录所有的行高 用于onLayout

    public FlowLayout(Context context) {
        super(context);
    }

    //反射
    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //主题style
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initMeasureParams(){
        allLines=new ArrayList<>();
        allHeights=new ArrayList<>();
    }


    /**
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     * 度量:具体规划
     * 确定子view大小，确定子view坐标，确定自己的大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG,"onMeasure");
        initMeasureParams();
        //度量子view
        int childCount=getChildCount();
        int paddingLeft=getPaddingLeft();
        int paddingRight=getPaddingRight();
        int paddingTop=getPaddingTop();
        int paddingBottom=getPaddingBottom();

        List<View>lineViews=new ArrayList<>();//保存一行中所有的view
        int lineWidthUsed=0;//记录一行已经使用的宽度
        int lineHeight=0;//一行的高度

        int selfWidth=MeasureSpec.getSize(widthMeasureSpec);
        int selfHeight=MeasureSpec.getSize(heightMeasureSpec);

        int parentNeedWidth=0;//measure过程中子view要求的父ViewGroup的宽度
        int parentNeedHeight=0;//measure过程中子view要求的父ViewGroup的高度

        for(int i=0;i<childCount;i++){
            View childView=getChildAt(i);
            LayoutParams childLP=childView.getLayoutParams();
            //度量子view宽度
            int childWidthMeasureSpec=getChildMeasureSpec(widthMeasureSpec,
                    paddingLeft+paddingRight,childLP.width);
            //度量子view高度
            int childHeightMeasureSpec=getChildMeasureSpec(heightMeasureSpec,
                    paddingTop+paddingBottom,childLP.height);
            //递归度量
            childView.measure(childWidthMeasureSpec,childHeightMeasureSpec);
            //获取子view宽度
            int childMeasureWidth=childView.getMeasuredWidth();
            //获取子view高度
            int childMeasureHeight=childView.getMeasuredHeight();

            //需要换行
            if(childMeasureWidth+lineWidthUsed+mHorizontalSpacing>selfWidth){
                allLines.add(lineViews);
                allHeights.add(lineHeight);
                //一旦换行需要判断当前行需要的宽和高了，并记录下来
                parentNeedWidth=Math.max(parentNeedWidth,lineWidthUsed+mHorizontalSpacing);
                parentNeedHeight=parentNeedHeight+lineHeight+mVerticalSpacing;
                lineViews=new ArrayList<>();
                lineWidthUsed=0;
                lineHeight=0;
            }

            lineViews.add(childView);
            lineWidthUsed=lineWidthUsed+childMeasureWidth+mHorizontalSpacing;
            lineHeight=Math.max(lineHeight,childMeasureHeight);

            //处理最后一行数据
            if(i==childCount-1){
                allLines.add(lineViews);
                allHeights.add(lineHeight);
                parentNeedWidth=Math.max(parentNeedWidth,lineWidthUsed+mHorizontalSpacing);
                parentNeedHeight=parentNeedHeight+lineHeight+mVerticalSpacing;
            }
        }

        //判断父View是否设置的有宽高，如果有采用设置值，如果没有采用度量的
        int widthModel=MeasureSpec.getMode(widthMeasureSpec);
        int heightModel=MeasureSpec.getMode(heightMeasureSpec);
        int realWidth=(widthModel==MeasureSpec.EXACTLY)?selfWidth:parentNeedWidth;
        int realHeight=(heightModel==MeasureSpec.EXACTLY)?selfHeight:parentNeedHeight;

        //确定自己的大小
        setMeasuredDimension(realWidth,realHeight);

    }

    //布局：所有子view进行布局
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG,"onLayout");
        int curL=getPaddingLeft();
        int curT=getPaddingTop();
        int lineCount=allLines.size();
        for(int i=0;i<lineCount;i++){
            List<View>lineViews=allLines.get(i);
            int lineHeight=allHeights.get(i);
            for(int j=0;j<lineViews.size();j++){
                View view=lineViews.get(j);
                int left=curL;
                int top=curT;
                int right=left+view.getMeasuredWidth();
                int bottom=top+view.getMeasuredHeight();
                view.layout(left,top,right,bottom);
                curL=right+mHorizontalSpacing;
            }
            curL=getPaddingLeft();
            curT=curT+lineHeight+mVerticalSpacing;
        }
    }

    //动画
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG,"onDraw");
    }
}
