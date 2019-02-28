package com.mapsoft.aftersale.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.mapsoft.aftersale.R;

import java.text.DateFormat;
import java.util.Date;

/**
 * 下拉刷新
 */
public class MyRefreshListView extends ListView implements OnScrollListener {

    private static final String TAG = "listView";

    private final static int RELEASE_To_REFRESH = 0;//释放并刷新视图
    private final static int PULL_To_REFRESH = 1;//显示动画效果
    private final static int REFRESHING = 2;//
    private final static int DONE = 3;
    private final static int LOADING = 4;

    // 实际的padding的距离与界面上偏移距离的比例
    private final static int RATIO = 3;

    private LinearLayout headView;

    private TextView tipsTextView; //刷新提示
    private TextView lastUpdatedTextView;//最近更新时间
    private ImageView arrowImageView;//箭头
    private ProgressBar progressBar;//加载圆圈


    private RotateAnimation animation, reverseAnimation;
//    private ValueAnimator scalaAnim;



    private int headContentWidth;//头部宽度
    private int headContentHeight;//头部高度

    private int startY;
    private int firstItemIndex;
    private int state;
    // 用于保证startY的值在一个完整的touch事件中只被记录一次
    private boolean isRecorded;
    private boolean isBack;

    private OnRefreshListener refreshListener;

    private boolean mPullLoading;//是否开启下拉刷新功能

    public MyRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public MyRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public int getHeaderViewsCount() {
        return super.getHeaderViewsCount();
    }

    /**
     * 初始化控件
     *
     * @param context
     */
    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        headView = (LinearLayout) inflater.inflate(R.layout.refresh_head, null);

        arrowImageView = (ImageView) headView
                .findViewById(R.id.head_arrowImageView);//箭头
        arrowImageView.setMinimumWidth(70);
        arrowImageView.setMinimumHeight(50);
        progressBar = (ProgressBar) headView
                .findViewById(R.id.head_progressBar);//进度条
        tipsTextView = (TextView) headView.findViewById(R.id.head_tipsTextView);//提示
        lastUpdatedTextView = (TextView) headView
                .findViewById(R.id.head_lastUpdatedTextView);//最近更新

        measureView(headView);//测量高度和宽度
        // 测量出来的高度和宽度
        headContentHeight = headView.getMeasuredHeight();
        headContentWidth = headView.getMeasuredWidth();

        headView.setPadding(0, -1 * headContentHeight, 0, 0);
        headView.invalidate();

        Log.i("size", "width:" + headContentWidth + " height:"
                + headContentHeight);

        addHeaderView(headView, null, false);
        setOnScrollListener(this);


        animation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);


        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);

        state = DONE;
        mPullLoading = false;
    }


    //将第一条可见item的index取出来
    public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
                         int arg3) {
        firstItemIndex = firstVisiableItem;
    }

    public void onScrollStateChanged(AbsListView arg0, int arg1) {
    }

    public boolean onTouchEvent(MotionEvent event) {

        if (mPullLoading) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //当前listview的第一个可见item正好是第一条，
                    if (firstItemIndex == 0 && !isRecorded) {
                        isRecorded = true;
                        startY = (int) event.getY();
                        Log.i(TAG, "在down时候记录当前位置‘");
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int tempY = (int) event.getY();
                    Log.e("Y的值:", tempY + "");
                    if (!isRecorded && firstItemIndex == 0) {
                        Log.i(TAG, "在move时候记录下位置");
                        isRecorded = true;
                        startY = tempY;
                    }
                    if (state != REFRESHING && isRecorded && state != LOADING) {
                        // 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，
                        // 当在上推的时候，列表会同时进行滚动
                        // 可以松手去刷新了
                        if (state == RELEASE_To_REFRESH) {
                            setSelection(0);
                            // 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
                            if (((tempY - startY) / RATIO < headContentHeight)
                                    && (tempY - startY) > 0) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState();
                                Log.i(TAG, "由松开刷新状态转变到下拉刷新状态");
                            }
                            // 一下子推到顶了
                            else if (tempY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();
                                Log.i(TAG, "由松开刷新状态转变到done状态");
                            }
                            // 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
                            else {
                                // 不用进行特别的操作，只用更新paddingTop的值就行了
                            }
                        }
                        // 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
                        if (state == PULL_To_REFRESH) {
                            setSelection(0);
                            // 下拉到可以进入RELEASE_TO_REFRESH的状态
                            if ((tempY - startY) / RATIO >= headContentHeight) {
                                state = RELEASE_To_REFRESH;
                                isBack = true;
                                changeHeaderViewByState();
                                Log.i(TAG, "由done或者下拉刷新状态转变到松开刷新");
                            }
                            // 上推到顶了
                            else if (tempY - startY <= 0) {
                                state = DONE;
                                changeHeaderViewByState();
                                Log.i(TAG, "由DOne或者下拉刷新状态转变到done状态");
                            }
                        }
                        // done状态下
                        if (state == DONE) {
                            if (tempY - startY > 0) {
                                state = PULL_To_REFRESH;
                                changeHeaderViewByState();
                                Log.i(TAG, "done状态直接重置0,state=" + state);
                            }
                        }
                        // 更新headView的size
                        if (state == PULL_To_REFRESH) {
                            // if ((tempY - startY) >= ViewConfigurationCompat.getScaledPagingTouchSlop(ViewConfiguration.get(context))) {
                            int x1 = headContentHeight / 2;
                            headView.setPadding(0, (-1 * headContentHeight
                                    + (tempY - startY) / RATIO) / 2, 0, (-1 * headContentHeight
                                    + (tempY - startY) / RATIO) / 2);
                            //  }
                        }
                        // 更新headView的paddingTop
                        if (state == RELEASE_To_REFRESH) {
                            int x = headContentHeight / 2;
                            headView.setPadding(0, ((tempY - startY) / RATIO
                                    - headContentHeight) / 2, 0, ((tempY - startY) / RATIO
                                    - headContentHeight) / 2);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.e(TAG, "异常状态0，刷新/加载");
                    if (state != REFRESHING && state != LOADING) {
                        if (state == DONE) {
                            // 什么都不做
                            changeHeaderViewByState();
                            Log.i(TAG, "done状态直接重置");
                        }
                        if (state == PULL_To_REFRESH) {
                            state = DONE;
                            changeHeaderViewByState();
                            Log.i(TAG, "由下拉刷新状态，到done状态");
                        }
                        if (state == RELEASE_To_REFRESH) {
                            state = REFRESHING;
                            changeHeaderViewByState();
                            onRefresh();
                            Log.i(TAG, "由松开刷新状态，到done状态");
                        }
                    } else {
                        Log.e(TAG, "异常状态，刷新/加载");
                    }
                    isRecorded = false;
                    isBack = false;
                    break;
                default:
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    // 当状态改变时候，调用该方法，以更新界面
    private void changeHeaderViewByState() {
        switch (state) {
            case RELEASE_To_REFRESH:
                tipsTextView.setVisibility(View.VISIBLE);
                lastUpdatedTextView.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.GONE);
                arrowImageView.setVisibility(View.VISIBLE);
                arrowImageView.clearAnimation();
                arrowImageView.startAnimation(animation);


                tipsTextView.setText("松开刷新");

                Log.i(TAG, "当前状态，松开刷新");
                break;
            case PULL_To_REFRESH:
                tipsTextView.setVisibility(View.VISIBLE);
                lastUpdatedTextView.setVisibility(View.VISIBLE);

                progressBar.setVisibility(View.GONE);
                arrowImageView.clearAnimation();
                arrowImageView.setVisibility(View.VISIBLE);
//                 是由RELEASE_To_REFRESH状态转变来的
                if (isBack) {
                    isBack = false;
                    arrowImageView.clearAnimation();
                    arrowImageView.startAnimation(reverseAnimation);
                    tipsTextView.setText("下拉刷新");
                } else {
                    tipsTextView.setText("下拉刷新");
                }
                Log.i(TAG, "当前状态，下拉刷新");
                break;

            case REFRESHING:
                headView.setPadding(0, 0, 0, 0);
                progressBar.setVisibility(View.VISIBLE);
                arrowImageView.clearAnimation();
                arrowImageView.setVisibility(View.GONE);
                tipsTextView.setText("正在刷新...");
                lastUpdatedTextView.setVisibility(View.VISIBLE);

                Log.i(TAG, "当前状态,正在刷新...");
                break;
            case DONE:
                headView.setPadding(0, -1 * headContentHeight, 0, 0);
                arrowImageView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                arrowImageView.clearAnimation();
                arrowImageView.setImageResource(R.drawable.refresh_down);
                tipsTextView.setText("下拉刷新");
                lastUpdatedTextView.setVisibility(View.VISIBLE);
                Log.i(TAG, "当前状态，done");
                break;
        }
    }

    public void setonRefreshListener(OnRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
        mPullLoading = true;
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public void onRefreshComplete() {
        state = DONE;
        lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
        changeHeaderViewByState();
    }

    private void onRefresh() {
        if (refreshListener != null) {
            refreshListener.onRefresh();
        }
    }

    // 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public void setAdapter(BaseAdapter adapter) {
//        lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
        lastUpdatedTextView.setText("最近更新:" + DateFormat.getDateTimeInstance().format(new Date()));

        super.setAdapter(adapter);
    }

}
