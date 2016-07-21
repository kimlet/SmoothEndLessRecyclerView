package cndroid.com.smoothendlesslibrary;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;

public class EndlessAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public enum FooterStatus {UNKNOWN, VISIBLE, GONE}


    private String buttonRetryText, loadingText;

    private RecyclerView.Adapter<VH> mAdapter;
    private List<View> mHeaderViews;
    private FooterStatus mHasLoadingFooter;


    private int layout_loading_resource_id;


    private IRetryListener retryListener;


    private boolean showRetry;

    // Provide a suitable constructor (depends on the kind of dataset)
    public EndlessAdapter(RecyclerView.Adapter<VH> adapter, int layoutLoadingResourceId) {
        this(adapter);
        this.layout_loading_resource_id = layoutLoadingResourceId;
    }

    private EndlessAdapter(RecyclerView.Adapter<VH> adapter) {
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
        mHasLoadingFooter = FooterStatus.UNKNOWN;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < 0) {
            return (VH) new ViewHolder(mHeaderViews.get(-viewType - 1));
        }
        if (viewType == R.id.endless_view_type_loading) {
            View view = getLoadingView(parent);
            return (VH) new ViewHolder(view);
        }

        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @NonNull
    private View getLoadingView(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(layout_loading_resource_id, parent, false);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (!isHeaderOrFooter(position)) {
            mAdapter.onBindViewHolder(holder, position - mHeaderViews.size());
        }
        if (isFooter(position)) {
            Log.d(this.getClass().getSimpleName(), "onBindViewHolder position=" + position);
            final ViewHolder viewHolder = ((ViewHolder) holder);
            viewHolder.switcher.setDisplayedChild(showRetry ? 1 : 0);
            viewHolder.tvLoading.setText(loadingText);
            viewHolder.btnReload.setText(buttonRetryText);
            viewHolder.btnReload.setEnabled(showRetry);

            if (showRetry) {
                viewHolder.btnReload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder.btnReload.setEnabled(false);
                        retryListener.onClickRetry();
                    }
                });
            }

        }
    }

    private boolean isFooter(int position) {
        return getItemViewType(position) == R.id.endless_view_type_loading;
    }

    @Override
    public int getItemCount() {
        int itemCount = mAdapter.getItemCount();

        itemCount += mHeaderViews.size();
        if (mHasLoadingFooter == FooterStatus.VISIBLE) {
            itemCount++;
        }

        return itemCount;
    }


    /**
     * @return <code>true</code> if data set changed, <code>false</code> otherwise
     */
    public boolean setFooterStatus(FooterStatus footerStatus) {
        if (showRetry) setShowRetry(false);

        if (mHasLoadingFooter != footerStatus) {
            mHasLoadingFooter = footerStatus;
            switch (footerStatus) {
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

        if (position == getItemCount() - 1 && mHasLoadingFooter == FooterStatus.VISIBLE) {
            return R.id.endless_view_type_loading;
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
        return (viewType < 0 || viewType == R.id.endless_view_type_loading);
    }

    private int posFix(int position) {
        return position + mHeaderViews.size();
    }

    public void setRetryListener(IRetryListener retryListener) {
        this.retryListener = retryListener;
    }

    public interface IRetryListener {
        void onClickRetry();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private ViewSwitcher switcher;
        private TextView tvLoading;
        private Button btnReload;

        private ViewHolder(View itemView) {
            super(itemView);
            switcher = (ViewSwitcher) itemView.findViewById(R.id.endless_view_switcher_id);
            tvLoading = (TextView) itemView.findViewById(R.id.endless_loading_text_id);
            btnReload = (Button) itemView.findViewById(R.id.endless_retry_button_id);
        }
    }

    public void setShowRetry(boolean showRetry) {
        this.showRetry = showRetry;
        try {
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setButtonRetryText(String buttonRetryText) {
        this.buttonRetryText = buttonRetryText;
    }

    public void setLoadingText(String loadingText) {
        this.loadingText = loadingText;
    }

}
