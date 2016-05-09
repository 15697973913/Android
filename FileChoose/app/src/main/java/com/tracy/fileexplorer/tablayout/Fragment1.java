package com.tracy.fileexplorer.tablayout;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.tracy.fileexplorer.FEApplication;
import com.tracy.fileexplorer.FileManager;
import com.tracy.fileexplorer.LocaleFileAdapter;
import com.tracy.fileexplorer.LocaleMediaFileBrowser;
import com.tracy.fileexplorer.R;
import com.tracy.fileexplorer.TFile;

import java.util.Collections;
import java.util.List;

/**
 * Created by mechrevo on 2016/5/9.
 */
public class Fragment1 extends Fragment implements AdapterView.OnItemClickListener{

    private ListView lv;
    private List<TFile> data;
    private LocaleFileAdapter adapter;
    private TextView emptyView;
    private FileManager bfm;
    private TextView localefile_bottom_tv;
    private Button localefile_bottom_btn;


    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            // TODO Auto-generated method stub
            if(1 == msg.what){
                lv.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                adapter = new LocaleFileAdapter(data,getActivity().getApplicationContext(),null,null);
                lv.setAdapter(adapter);
            }else if(0 == msg.what){
                lv.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText(getString(R.string.curCatagoryNoFiles));
            }
            super.handleMessage(msg);
        }

    };

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle saveInstanceState){
        View mainView = inflater.inflate(R.layout.localefile_browser,container,false);
        lv = (ListView) mainView.findViewById(R.id.listView);
        lv.setOnItemClickListener(this);
        emptyView = (TextView) mainView.findViewById(R.id.emptyView);
        localefile_bottom_btn = (Button) mainView.findViewById(R.id.localefile_bottom_btn);
        localefile_bottom_tv = (TextView) mainView.findViewById(R.id.localefile_bottom_tv);

        bfm = FileManager.getInstance();;
        Bundle bundle =getArguments();
        if(bundle.getInt("music")==1) {
            setData( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        }
        onFileClick();
        return mainView;
    }

    private void setData(final Uri uri){
        FEApplication app = (FEApplication) getActivity().getApplication();
        app.execRunnable(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                data= bfm.getMediaFiles(getActivity(),uri);
                if(null != data){
                    Collections.sort(data);
                    handler.sendEmptyMessage(1);
                }
                else
                    handler.sendEmptyMessage(0);
            }

        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if(0 == item.getItemId()){
            getActivity().setResult(1);
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //点击文件进行勾选操作
    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        CheckBox fileCheckBox = (CheckBox) view.findViewById(R.id.fileCheckBox);
        TFile bxfile = data.get(pos);

        List<TFile> choosedFiles = bfm.getChoosedFiles();
        if (choosedFiles.contains(bxfile)) {
            choosedFiles.remove(bxfile);
            fileCheckBox.setChecked(false);
        } else {

            choosedFiles.add(bxfile);
            fileCheckBox.setChecked(true);
        }

        onFileClick();
    }

    //点击文件，触发ui更新
    //onResume，触发ui更新
    private void onFileClick() {
        localefile_bottom_tv.setText(bfm.getFilesSizes());
        int cnt = bfm.getFilesCnt();
        localefile_bottom_btn.setText(String.format(getString(R.string.bxfile_choosedCnt), cnt));
        localefile_bottom_btn.setEnabled(cnt>0);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(null!=data){
            data.clear();
        }
        data = null;
        adapter = null;
        handler = null;
    }

}
