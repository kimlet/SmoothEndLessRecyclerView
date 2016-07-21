package cndroid.com.smoothendlesslibrary;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

public class EndlessAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public enum LoadingFooterStatus {UNKNOWN, VISIBLE, GONE}

    private RecyclerView.Adapter<VH> mAdapter;
    private List<View> mHeaderViews;
    private LoadingFooterStatus mHasLoadingFooter;

    // Provide a suitable constructor (depends on the kind of dataset)
    public EndlessAdapter(RecyclerView.Adapter<VH> adapter) {
        mAdapter = adapter;
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                notifyItemRangeChanged(posFix(positionStart), itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                notifyItemRangeInserted(posFix(positionStart), itemCount);

            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                notifyItemRangeRemoved(posFix(positionStart), itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                notifyItemMoved(posFix(fromPosition), posFix(toPosition));
            }
        });

        mHeaderViews = new ArrayList<>();
        mHasLoadingFooter = LoadingFooterStatus.UNKNOWN;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < 0) {
            return (VH) new ViewHolder(mHeaderViews.get(-viewType - 1));
        }
        if (viewType == R.layout.endless_refresh_foot) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.endless_refresh_foot, parent, false);
            return (VH) new ViewHolder(view);
        }

        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (!isHeaderOrFooter(position)) {
            mAdapter.onBindViewHolder(holder, position - mHeaderViews.size());
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = mAdapter.getItemCount();

        itemCount += mHeaderViews.size();
        if (mHasLoadingFooter == LoadingFooterStatus.VISIBLE) {
            itemCount++;
        }

        return itemCount;
    }

    /**
     * @return <code>true</code> if data set changed, <code>false</code> otherwise
     */
    public boolean setHasLoadingFooter(LoadingFooterStatus hasLoadingFooter) {
        if (mHasLoadingFooter != hasLoadingFooter) {
            mHasLoadingFooter = hasLoadingFooter;
            switch (hasLoadingFooter) {
                case VISIBLE:
                    try {
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case GONE:
                    try {
                        notifyItemRemoved(getItemCount());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }

        return false;
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
        notifyDataSetChanged();
    }

    public boolean removeHeaderView(View view) {
        boolean result = mHeaderViews.remove(view);
        if (result) {
            notifyDataSetChanged();
        }

        return result;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mHeaderViews.size()) {
            return -(position + 1);
        }

        if (mHasLoadingFooter == LoadingFooterStatus.VISIBLE
                && position == getItemCount() - 1) {
            return R.layout.endless_refresh_foot;
        }

        return mAdapter.getItemViewType(position);
    }

    public int getSpanSizeLookup(int position, int spanSize) {
        if (isHeaderOrFooter(position)) {
            return spanSize;
        }

        return 1;
    }

    public boolean isHeaderOrFooter(int position) {
        int viewType = getItemViewType(position);
        return (viewType < 0 || viewType == R.layout.endless_refresh_foot);
    }

    private int posFix(int position) {
        return position + mHeaderViews.size();
    }
}
