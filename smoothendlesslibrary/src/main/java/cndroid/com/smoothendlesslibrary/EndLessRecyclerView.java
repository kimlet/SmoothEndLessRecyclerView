package cndroid.com.smoothendlesslibrary;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;


/**
 * Created by jinbangzhu on 7/20/16.
 */

public class EndLessRecyclerView extends RecyclerView {
    private EndlessAdapter mAdapter;
    private EndlessRecyclerOnScrollListener mOnScrollListener;


    private EndLessListener endLessListener;

    private int currentPageIndex = 0;

    public EndLessRecyclerView(Context context) {
        this(context, null);
    }

    public EndLessRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EndLessRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
        mAdapter = new EndlessAdapter<>(adapter);
        super.setAdapter(mAdapter);
    }

    private class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
        private boolean mLoading = true; // True if we are still waiting for the last set of data to load.
        private int mVisibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
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


            if (!mLoading && (mTotalItemCount - mVisibleItemCount) <= (mFirstVisibleItem + mVisibleThreshold)) {
                // End has been reached
                mLoading = true;
                Log.d(this.getClass().getSimpleName(), "needLoadMore");

                onLoadMore();
            }
        }
    }

    public void setEndLessListener(EndLessListener endLessListener) {
        this.endLessListener = endLessListener;
        this.mOnScrollListener.setLoading(false);
    }

    public void setStartPageIndex(int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }


    public void onLoadMore() {
        if (null != endLessListener) {
            endLessListener.onLoadMoreData(currentPageIndex);
            mAdapter.setHasLoadingFooter(EndlessAdapter.LoadingFooterStatus.VISIBLE);
        } else {
            Log.w(this.getClass().getSimpleName(), "Need register endLessListener");
        }
    }

    public void completeLoadMore() {
        mOnScrollListener.setLoading(false);
        mAdapter.setHasLoadingFooter(EndlessAdapter.LoadingFooterStatus.GONE);
    }
}
