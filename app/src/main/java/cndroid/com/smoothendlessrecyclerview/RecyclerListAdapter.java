package cndroid.com.smoothendlessrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cndroid.com.smoothendlesslibrary.CollectionUtils;


public abstract class RecyclerListAdapter<T> extends RecyclerView.Adapter<RecyclerListAdapter.ViewHolder> {

    private Context mContext;
    protected List<T> mDataSet;

    private OnItemClickListener<T> mOnItemClickListener;
    private OnItemLongClickListener<T> mOnItemLongClickListener;

    public interface OnItemClickListener<T> {
        void onItemClick(View v, T data, int position);
    }

    public interface OnItemLongClickListener<T> {
        boolean onItemLongClick(View v, T data, int position);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public abstract class ViewHolder extends RecyclerView.ViewHolder {
        View mRootView;
        T mData;

        public ViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView;
            mRootView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null && mData != null) {
                        mOnItemClickListener.onItemClick(
                                v, mData, getAdapterPosition());
                    }
                }
            });
            mRootView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnItemLongClickListener != null && mData != null) {
                        return mOnItemLongClickListener.onItemLongClick(
                                v, mData, getAdapterPosition());
                    }
                    return false;
                }
            });
        }

        public void bindData(T data) {
            mData = data;
        }
    }

    public class DummyViewHolder extends ViewHolder {

        protected DummyViewHolder() {
            super(new View(getContext()));
        }

        @Override
        public void bindData(T data) {
            super.bindData(data);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerListAdapter(Context context, List<T> myDataSet) {
        mDataSet = myDataSet;
        mContext = context;
    }

    public RecyclerListAdapter(Context context) {
        this(context, new ArrayList<T>());
    }

    public void add(T item) {
        int pos = getItemCount();
        if (mDataSet.add(item)) {
            notifyItemInserted(pos);
        }
    }

    public void add(int position, T item) {
        int pos = getItemCount();
        mDataSet.add(position, item);
        notifyItemInserted(pos);
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    public void addAll(T... items) {
        int pos = getItemCount();
        if (CollectionUtils.addAll(mDataSet, items)) {
            notifyItemRangeInserted(pos, items.length);
        }
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    public void addAll(Collection<T> items) {
        int pos = getItemCount();
        if (CollectionUtils.addAll(mDataSet, items)) {
            notifyItemRangeInserted(pos, items.size());
        }
    }

    /**
     * Adds the specified items at the end of the array.
     *
     * @param items The items to add at the end of the array.
     */
    public void addAllWithoutNotifyItem(Collection<T> items) {
        CollectionUtils.addAll(mDataSet, items);
    }
    /**
     * Inserts the specified object at the specified index in the array.
     *
     * @param object The object to insert into the array.
     * @param index  The index at which the object must be inserted.
     */
    public void insert(T object, int index) {
        mDataSet.add(index, object);
        notifyItemInserted(index);
    }

    /**
     * Removes the specified object from the array.
     *
     * @param object The object to remove.
     */
    public void remove(T object) {
        int pos = mDataSet.indexOf(object);
        if (pos != -1) {
            mDataSet.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    /**
     * Removes the specified object at pos.
     *
     * @param pos The pos of the object to remove.
     */
    public void remove(int pos) {
        if (pos >= 0 && pos < getItemCount()) {
            mDataSet.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    /**
     * Remove all elements from the list.
     */
    public void clear() {
        if(null == mDataSet)
            return;
        int count = mDataSet.size();
        mDataSet.clear();
//        notifyItemRangeRemoved(0, count);
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return mDataSet.get(position);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerListAdapter.ViewHolder holder, int position) {
        holder.bindData(mDataSet.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public Context getContext() {
        return mContext;
    }

    public ArrayList<T> getDataSet() {
        ArrayList<T> list = new ArrayList<>(mDataSet);
        return list;
    }
}
