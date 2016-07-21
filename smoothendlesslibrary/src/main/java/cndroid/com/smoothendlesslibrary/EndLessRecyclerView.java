package cndroid.com.smoothendlesslibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;


/**
 * Created by jinbangzhu on 7/20/16.
 */

public class EndLessRecyclerView extends RecyclerView implements EndlessAdapter.IRetryListener {
    private EndlessAdapter mAdapter;
    private EndlessRecyclerOnScrollListener mOnScrollListener;
    private EndLessListener endLessListener;

    private int currentPageIndex = 1; // current page index


    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.

    private int layoutLoadingId;


    private String buttonRetryText;

    private String loadingText;

    public EndLessRecyclerView(Context context) {
        this(context, null);
    }

    public EndLessRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EndLessRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SmoothEndLessRecycler, 0, 0);
        layoutLoadingId = typedArray.getResourceId(R.styleable.SmoothEndLessRecycler_loadingLayout, R.layout.endless_foot_layout);
        buttonRetryText = typedArray.getString(R.styleable.SmoothEndLessRecycler_buttonRetryText);
        loadingText = typedArray.getString(R.styleable.SmoothEndLessRecycler_loadingText);
        if (TextUtils.isEmpty(buttonRetryText))
            buttonRetryText = context.getString(R.string.endless_string_retry_text);
        if (TextUtils.isEmpty(loadingText))
            loadingText = context.getString(R.string.endless_string_loading_text);
        typedArray.recycle();


        mOnScrollListener = new EndlessRecyclerOnScrollListener();
        addOnScrollListener(mOnScrollListener);

        setHasFixedSize(true);
    }

    @Override
    public void setLayoutManager(LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return mAdapter.getSpanSizeLookup(position, gridLayoutManager.getSpanCount());
                }
            });
        }
        super.setLayoutManager(layoutManager);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = new EndlessAdapter<>(adapter, layoutLoadingId);
        mAdapter.setButtonRetryText(buttonRetryText);
        mAdapter.setLoadingText(loadingText);
        super.setAdapter(mAdapter);
    }

    @Override
    public void onClickRetry() {
        onLoadMore();
    }

    private class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
        private boolean mLoading = true; // True if we are still waiting for the last set of data to load.
        int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;

        public void setLoading(boolean loading) {
            mLoading = loading;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            mVisibleItemCount = recyclerView.getChildCount();
            mTotalItemCount = getLayoutManager().getItemCount();
            if (getLayoutManager() instanceof LinearLayoutManager)
                mFirstVisibleItem = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
            else if (getLayoutManager() instanceof StaggeredGridLayoutManager)
                mFirstVisibleItem = ((StaggeredGridLayoutManager) getLayoutManager()).findFirstVisibleItemPositions(null)[0];


            if (!mLoading && (mTotalItemCount - mVisibleItemCount) <= (mFirstVisibleItem + visibleThreshold)) {
                Log.d(this.getClass().getSimpleName(), "needLoadMore");

                // user not register listener; ignore
                if (null == endLessListener) return;

                // End has been reached
                mLoading = true;

                increasePageIndex();

                onLoadMore();
            }
        }
    }

    public void setEndLessListener(EndLessListener endLessListener) {
        this.endLessListener = endLessListener;
        this.mOnScrollListener.setLoading(false);
    }

    /**
     * start page index
     *
     * @param currentPageIndex default 1
     */
    public void setStartPageIndex(int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }

    public void showRetryView() {
        if (null != endLessListener) {
            mAdapter.setShowRetry(true);
            mAdapter.setRetryListener(this);
        }
    }

    public void setButtonRetryText(String buttonRetryText) {
        this.buttonRetryText = buttonRetryText;
    }

    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

    public void onLoadMore() {
        if (null != endLessListener) {
            endLessListener.onLoadMoreData(currentPageIndex);
            mAdapter.setFooterStatus(EndlessAdapter.FooterStatus.VISIBLE);
        } else {
            // do nothing
        }
    }

    private void increasePageIndex() {
        currentPageIndex++;
    }

    public void setVisibleThreshold(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public void completeLoadMore() {
        mOnScrollListener.setLoading(false);
        mAdapter.setFooterStatus(EndlessAdapter.FooterStatus.GONE);
    }
}
