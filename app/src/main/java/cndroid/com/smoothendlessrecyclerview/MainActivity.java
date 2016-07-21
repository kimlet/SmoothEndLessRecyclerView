package cndroid.com.smoothendlessrecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cndroid.com.smoothendlesslibrary.EndLessListener;
import cndroid.com.smoothendlesslibrary.EndLessRecyclerView;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

public class MainActivity extends AppCompatActivity {

    List<String> list;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = generateList();

        final EndLessRecyclerView recyclerView = (EndLessRecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


//        final MyAdapter adapter = new MyAdapter(this, list);
        final MyAdapter2 adapter = new MyAdapter2();
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
                                recyclerView.showRetryView();
//                                adapter.increase();
                            }
                        });
                    }
                }, 2000);
            }
        });
    }

    private List<String> generateList() {
        List<String> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add("Hello World " + index++);
        }
        return list;
    }


    class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.MyHolder2> {
        int count = 10;

        @Override
        public MyHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

            return new MyHolder2(v);
        }

        public void increase() {
            count += 10;
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


    class MyAdapter extends RecyclerListAdapter<String> {

        public MyAdapter(Context context, List<String> myDataSet) {
            super(context, myDataSet);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

            return new MyHolder(v);
        }


        class MyHolder extends ViewHolder {
            TextView textView;

            public MyHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.text);
            }

            @Override
            public void bindData(String data) {
                super.bindData(data);
                textView.setText((data.toString()));
            }
        }
    }

}
