package cndroid.com.smoothendlessrecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cndroid.com.smoothendlesslibrary.EndLessListener;
import cndroid.com.smoothendlesslibrary.EndLessRecyclerView;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EndLessRecyclerView recyclerView = (EndLessRecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final MyAdapter adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new FadeInUpAnimator());


        recyclerView.setEndLessListener(new EndLessListener() {
            @Override
            public void onLoadMoreData(int pageIndex) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (adapter.count > 100) {
                                    recyclerView.showRetryView();
                                } else {
                                    recyclerView.completeLoadMore();
                                    adapter.increase();

                                }
                            }
                        });
                    }
                }, 2000);
            }
        });
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder2> {
        int count = 10;

        @Override
        public MyHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

            return new MyHolder2(v);
        }

        public void increase() {
            count += 50;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return count;
        }

        @Override
        public void onBindViewHolder(MyHolder2 holder, int position) {
            holder.textView.setText("Hello world p" + position);
        }


        class MyHolder2 extends RecyclerView.ViewHolder {

            TextView textView;

            public MyHolder2(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text);
            }
        }

    }

}
