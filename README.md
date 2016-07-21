# SmoothEndLessRecyclerView
endLess recyclerView

###Demo
![Sample Image](https://github.com/Jinbangzhu/SmoothEndLessRecyclerView/raw/master/demo.gif "Demo")

###Usage
####XML normal
    <cndroid.com.smoothendlesslibrary.EndLessRecyclerView
        android:id="@+id/recycler"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

####XML advance
    <cndroid.com.smoothendlesslibrary.EndLessRecyclerView
        android:id="@+id/recycler"
        app:sel_footerLayout="@layout/footer"
        app:sel_buttonRetryText="retry"
        app:sel_loadingText="loading"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
        
####Callback
    endLessRecyclerView.setEndLessListener(new EndLessListener() {
                @Override
                public void onLoadMoreData(int pageIndex) {
                    // do something
                }
            });
####Compelete loadMore
    endLessRecyclerView.completeLoadMore();
####Show retryView
    endLessRecyclerView.showRetryView();
    
    
