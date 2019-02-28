/*
package com.mapsoft.aftersale.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

*/
/**
 * Created by Administrator on 2017/12/29.
 *//*


public class RefreshListView extends ListView implements AbsListView.OnScrollListener{
    //头布局视图
    private View header;
    //头布局高度
    private int headerViewHeight;
    //按下时y的偏移量
    private int downY;
    //移动时y的偏移量
    private int moveY;
    //距离顶部的距离
    private int paddingTop;
    //listview第一个可见的item项
    private int firstVisibleItemPosition;
    //下拉刷新
    private final int PULL_DOWN_REFRESH = 0;
    //释放刷新
    private  final int RELEASE_REFRESH = 1;
    //正在刷新
    private final int REFRESHING = 2;
    //当前状态，默认为下拉刷新
    private int currentState = PULL_DOWN_REFRESH;
    //刷新列表构造函数
    private Context context;
    //刷新监听
    private OnRefreshListener mOnRefreshListener;
    private ImageView refresh;
    private RotateAnimation animation;
    //图片向上旋转
    private Animation upAnimation;
    //图片向下旋转
    private Animation downAnimation;
    //箭头
    private ImageView row;
    //提示文本
    private TextView header_tv;
    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化头布局
        initHeaderView();
        //滑动监听
        this.setOnScrollListener(this);
        this.context = context;
    }

    */
/**滚动状态改变时调用，一般用于列表视图和网格视图
     *
     * @param view
     * @param scrollState 有三种值，分别是SCROLL_STATE_IDLE,SCROLL_STATE_TOUCH_SCROLL,SCROLL_STATE_FLING
     *                    SCROLL_STATE_IDLE:当屏幕停止滚动时
     *                    SCROLL_STATE_TOUCH_SCROLL:当屏幕以触屏方式滚动并且手指还在屏幕上时
     *                    SCROLL_STATE_FLING:当用户之前滑动屏幕并抬起手指，屏幕以惯性滚动
     *//*

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState==SCROLL_STATE_IDLE||scrollState == SCROLL_STATE_FLING){

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        firstVisibleItemPosition = firstVisibleItem;
    }
//    public void setOnRefreshListener(OnRefreshListener listener){
//
//    }

    public void setOnRefreshListener(OnRefreshListener listener){
        mOnRefreshListener = listener;
    }

    private void initHeaderView(){
        //头布局文件
        header = LayoutInflater.from(getContext()).inflate(R.layout.header,null);
        //测量头布局，绘制一个视图一般经过measure,layout,draw
        header.measure(0,0);
        //头布局高度
        headerViewHeight = header.getMeasuredHeight();
        //设置间隔
        header.setPadding(0,-headerViewHeight,0,0);
        //加载头布局
        this.addHeaderView(header);
        refresh = (ImageView) header.findViewById(R.id.refresh);
        row = (ImageView) header.findViewById(R.id.row);
        header_tv = (TextView) header.findViewById(R.id.header_tv);
        initAnimation();

    }
    private void initAnimation(){
        animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,0.48f,Animation.RELATIVE_TO_SELF,0.47f);
        animation.setDuration(500);
        animation.setRepeatCount(5);

        upAnimation = new RotateAnimation(0f,180f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        upAnimation.setDuration(300);
        upAnimation.setFillAfter(true);

        downAnimation = new RotateAnimation(180f,360f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        downAnimation.setDuration(300);
        downAnimation.setFillAfter(true);
//        refresh.setAnimation(animation);

    }

    */
/**
     * 触摸事件
     * @param ev
     * @return
     *//*

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            //手指按下
            case MotionEvent.ACTION_DOWN:
                //记录按下时y的偏移量
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE://手指滑动
                //记录移动时的y值偏移
                moveY = (int) ev.getY();
                //可以看成头布局距离屏幕顶部的距离,这里除以2是控制手指滑动
                paddingTop = (moveY - downY)/2-headerViewHeight;
                if(firstVisibleItemPosition==0&&-headerViewHeight<paddingTop){//必须的条件
                    //如果paddingTop>0就说明完全显示了，但还要判断当前状态是否是下拉刷新状态，因为正在刷新状态也是完全显示
                    if(paddingTop>0&&currentState == PULL_DOWN_REFRESH){//完全显示
                        header_tv.setText("松开刷新");
                        //将当前状态置为释放刷新
                        currentState = RELEASE_REFRESH;
                        changeHeaderByViewState();
                    }else if(paddingTop<0&&currentState == RELEASE_REFRESH){//没有完全显示,currentState=RELEASE_REFRESH原因是可以先滑到完全显示后再往上滑到不完全显示
                        currentState = PULL_DOWN_REFRESH;
                        header_tv.setText("下拉刷新");
                        changeHeaderByViewState();
                    }
                    header.setPadding(0,paddingTop,0,0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(currentState == RELEASE_REFRESH){//完全显示
                    header.setPadding(0,0,0,0);
                    currentState = REFRESHING;
                    changeHeaderByViewState();
                    if (mOnRefreshListener!=null){
                        mOnRefreshListener.downPullRefresh();
                    }
                }else if(currentState == PULL_DOWN_REFRESH){//未完全显示
                    //当手指松开时，若头部未完全显示则隐藏头部
                    header.setPadding(0,-headerViewHeight,0,0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void changeHeaderByViewState(){
        switch (currentState){
            case PULL_DOWN_REFRESH:
                row.startAnimation(downAnimation);
                break;
            case RELEASE_REFRESH:
                row.startAnimation(upAnimation);
                break;
            case REFRESHING:
                row.clearAnimation();
                row.setVisibility(View.GONE);
                header_tv.setText("正在刷新");
                refresh.clearAnimation();
                refresh.setVisibility(View.VISIBLE);
                refresh.startAnimation(animation);
                break;
        }
    }
    public void hideHeaderView(){
        header.setPadding(0,-headerViewHeight,0,0);
        refresh.setVisibility(View.GONE);
        currentState = PULL_DOWN_REFRESH;
        header_tv.setText("下拉刷新");
        row.setVisibility(View.VISIBLE);

    }
}
*/
