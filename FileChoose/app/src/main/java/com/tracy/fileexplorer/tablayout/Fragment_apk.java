package com.tracy.fileexplorer.tablayout;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.tracy.fileexplorer.FileManager;
import com.tracy.fileexplorer.R;
import com.tracy.fileexplorer.TFile;
import com.tracy.fileexplorer.apklist.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mechrevo on 2016/5/9.
 */
public class Fragment_apk extends Fragment implements AdapterView.OnItemClickListener{
    private ListView mListView;
    private List<TFile> mAppInfoList;
   // private FrameLayout mRootFrameLayout;
    public final int GET_APK_FINISH = 9527;
    //public ProgressBar mProgressBar;
    private PackageManager mPackageManager;
    private ListViewAdapter mListViewAdapter;
    private FileManager bfm;




    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == GET_APK_FINISH) {
                //dismissProgressBar();
                mListViewAdapter.notifyDataSetChanged();
            }
            super.handleMessage(msg);

        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        mPackageManager = getActivity().getPackageManager();
        View mainView = inflater.inflate(R.layout.activity_main_apk, container, false);

        mListView = (ListView) mainView.findViewById(R.id.listView);
        //mProgressBar = new ProgressBar(mContext);

        mAppInfoList = new ArrayList<TFile>();
        mListViewAdapter = new ListViewAdapter(getActivity());
        mListViewAdapter.setList(mAppInfoList);
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(this);
        // showProgressBar();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAppInfoList.clear();
                mAppInfoList.addAll(getAllAppInfo());
                handler.sendEmptyMessage(GET_APK_FINISH);
            }
        }).start();



        return mainView;
    }



    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3){
            TFile apkFile = mAppInfoList.get(position);
            bfm = FileManager.getInstance();
            CheckBox fileCheckBox = (CheckBox) view.findViewById(R.id.fileCheckBox);
            List<TFile> choosedFiles = bfm.getChoosedFiles();

            if (choosedFiles.contains(apkFile)) {
                choosedFiles.remove(apkFile);
                fileCheckBox.setChecked(false);
            } else {

                choosedFiles.add(apkFile);
                fileCheckBox.setChecked(true);
            }
        int cnt = bfm.getFilesCnt();
        sendBroadcast(cnt);
        }




    /**
     * 获取已经安装的应用
     */
    private List<TFile> getAllAppInfo() {
        mAppInfoList = new ArrayList<TFile>();
        mPackageManager = getActivity().getPackageManager();
        //获取已安装所有应用对应的PackageInfo
        List<PackageInfo> packageInfoList = mPackageManager.getInstalledPackages(0);
        for (int i = 0; i < packageInfoList.size(); i++) {
            PackageInfo packageInfo = packageInfoList.get(i);
            //获取应用名称
            TFile.appBuild builder = new TFile.appBuild(packageInfo, mPackageManager);
            TFile bxfile = builder.build();
            if (bxfile != null) {
                mAppInfoList.add(bxfile);
            }
        }
        return mAppInfoList;
    }


    public void sendBroadcast(int cnt){
        Intent intent = new Intent("send_cnt_change");
        intent.putExtra("cnt",cnt);
        getActivity().sendBroadcast(intent);
    }


}



    /**
     * 在屏幕中间显示风火轮
     */
    /*private void showProgressBar(){
        LayoutInflater inflater = LayoutInflater.from(getActivity().getParent());
        mRootFrameLayout=(FrameLayout) inflater.inflate(android.R.id.content);
        FrameLayout.LayoutParams layoutParams= new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER;
        mProgressBar=new ProgressBar(mContext);
        mProgressBar.setLayoutParams(layoutParams);
        mProgressBar.setVisibility(View.VISIBLE);
        mRootFrameLayout.addView(mProgressBar);
    }

    /**
     * 隐藏风火轮

    private void dismissProgressBar(){
        if(null!=mProgressBar&&null!=mRootFrameLayout){
            mRootFrameLayout.removeView(mProgressBar);
        }
    }*/
