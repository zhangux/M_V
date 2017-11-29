package com.mjj.slidingbutton;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


public class MainActivity extends AppCompatActivity implements FileAdapter.IonSlidingViewClickListener {

    private RecyclerView mRecyclerView;

    private FileAdapter mFileAdapter;

    private final String TAG = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mjj.slidingbutton.R.layout.activity_main);

        initView();
        setAdapter();

    }

    private void initView(){
        mRecyclerView = (RecyclerView) findViewById(com.mjj.slidingbutton.R.id.recyclerview);
    }

    private void setAdapter(){

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFileAdapter = new FileAdapter(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }


    @Override
    public void onItemClick(View view, int position) {
        Log.i(TAG,"����"+position);
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        Log.i(TAG,"ɾ���"+position);
        mFileAdapter.removeData(position);
    }
}
