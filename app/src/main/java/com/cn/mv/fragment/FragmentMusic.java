package com.cn.mv.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.mv.R;
import com.cn.mv.adapter.FileMusicAdapter;
import com.cn.mv.constants.Constants;
import com.cn.mv.molder.MessageInfo;
import com.cn.mv.util.FileUtil;
import com.cn.mv.util.SharedPreferencesUtils;
import com.cn.mv.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/7/17.
 */
public class FragmentMusic extends Fragment {
    private View v;
    private RecyclerView recyclerView;
    private TextView tvZdSearch;
    public List<File> fileLis;
    private Handler handler = new Handler();
    private FileMusicAdapter adapter;
    private String loginFile = "加载中...";
    private View longClickSelectView;//长按时选中的View
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    private LinearLayoutManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_music, null);
            bindView();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initView(0);
                }
            }, 500);
        }
        return v;
    }

    private void bindView() {
        recyclerView = (RecyclerView) v.findViewById(R.id.music_rv);
        tvZdSearch = (TextView) v.findViewById(R.id.music_zd_search);
        tvZdSearch.setVisibility(View.VISIBLE);
        fileLis = new ArrayList<>();
        tvZdSearch.setText(loginFile);
    }

    private synchronized void initView(int modType) {
        File file = new File(Environment.getExternalStorageDirectory().getPath());
        if (fileLis.size() <= 0) {
            addFile(file.listFiles());
        }
        tvZdSearch.setVisibility(View.VISIBLE);
        if (fileLis != null && fileLis.size() > 0) {
            adapter = new FileMusicAdapter(getContext(), fileLis, modType);
            adapter.setOnItemClickListener(itemClickLis);
            adapter.setOnItemLongClickListener(longClickLis);
            adapter.setOnItemDelClickListener(onItemDelClickLis);
            adapter.setOnItemUpdateClickListener(itemUpdateClickLis);
            if (modType == 0) {
                manager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(manager);

            } else {
                manager = new GridLayoutManager(getContext(), 1);
                recyclerView.setLayoutManager(manager);
            }
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        }
        tvZdSearch.setVisibility(View.GONE);

    }

    private FileMusicAdapter.OnItemUpdateClickListener itemUpdateClickLis = new FileMusicAdapter.OnItemUpdateClickListener() {
        @Override
        public void onItemUpdateClick(View v, int position, File file, String rename) {
            rename(file, rename, position);
        }
    };

    private void rename(File file, String rename, int position) {
        if (file != null && file.isFile()) {
            int result = FileUtil.rename(rename, file);
            switch (result) {
                case -1:
                    ToastUtil.show("命名失败");
                    break;
                case 0:
                    ToastUtil.show("名字重复");
                    break;
                case 1:
                    ToastUtil.show("命名完成");
                    String pathQ = file.getPath();
                    String path = pathQ.substring(0, pathQ.lastIndexOf("/") + 1) + rename;
                    fileLis.remove(position);
                    fileLis.add(position, new File(path));
                    refreshFile();
                    break;
            }
            return;
        }
    }

    private File delFile;
    private int delFileIndex;
    private FileMusicAdapter.OnItemDelClickListener onItemDelClickLis = new FileMusicAdapter.OnItemDelClickListener() {
        @Override
        public void onItemDelClick(View v, int position, File file) {
            delFile = file;
            delFileIndex = position;
            delete();
        }
    };

    private void delete() {
        new AlertDialog.Builder(getContext())
                .setTitle("删除")
                .setMessage("确定要删除 " + delFile.getName() + " 吗?")
                .setNegativeButton("确定", deleteFileClickLis)
                .setPositiveButton("取消", deleteFileClickLis)
                .show();
    }

    private DialogInterface.OnClickListener deleteFileClickLis = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int i) {
            switch (i) {
                case DialogInterface.BUTTON_NEGATIVE:
                    FileUtil.delete(delFile);
                    remove(delFileIndex);
                    refreshFile();
                    break;
                case DialogInterface.BUTTON_POSITIVE:

                    break;
            }
        }
    };
    private FileMusicAdapter.OnItemClickListener itemClickLis = new FileMusicAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position, File file) {
            if (adapter.getCheckFile().size() != 0) {
                if (file != null) {
                    CheckBox cb = (CheckBox) v.findViewById(R.id.music_cb);
                    //设置选中状态为其当前状态的相反状态
                    cb.setChecked(!cb.isChecked());
                }
                return;
            }
            if (adapter.getCheckFile().size() <= 0) {
                openFile(file, getContext());
            }
        }
    };
    private FileMusicAdapter.OnItemLongClickListener longClickLis = new FileMusicAdapter.OnItemLongClickListener() {
        @Override
        public void onItemLongClick(View v, int position, File file) {

            boolean flag = false;
            if (adapter.getCheckFile().size() == 0) {
                flag = true;//判断是否要弹出上下文菜单
            }
            CheckBox cb = (CheckBox) v.findViewById(R.id.music_cb);
            cb.setChecked(!cb.isChecked());
            if (flag) {
                longClickSelectView = v;
                registerForContextMenu(v);//注册上下文菜单
                getActivity().openContextMenu(v);//打开上下文
                unregisterForContextMenu(v);//取消注册的菜单
            }
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Map<Integer, File> map = adapter.getCheckFile();
        Set<Map.Entry<Integer, File>> set = map.entrySet();
        Iterator<Map.Entry<Integer, File>> it = set.iterator();
        if (it.hasNext()) {
            File file = it.next().getValue();
            if (file.isFile()) {
                getActivity().getMenuInflater().inflate(R.menu.menu_selected_file_part, menu);
            }
        }
    }

    public void cancleAllSelectedItem() {
        adapter.cancleAllSelectedItem();
    }

    public void selectAll() {
        adapter.selectAll();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_my_video:

                cancleAllSelectedItem();
                EventBus.getDefault().post(new MessageInfo(Constants.ITEM_CHECKED_CHANGED, null));
                break;
        }
        return true;
    }

    public void fileOrder() {
        tvZdSearch.setVisibility(View.VISIBLE);
        fileLis = FileUtil.getChildrenFiles(fileLis, SharedPreferencesUtils.getMusicOrderType(getContext()));
        adapter.setFiles(fileLis);
        tvZdSearch.setVisibility(View.GONE);
    }

    public void refreshFile() {
        tvZdSearch.setVisibility(View.VISIBLE);
        adapter.setFiles(fileLis);
        tvZdSearch.setVisibility(View.GONE);
    }

    public void remove(int position){
        fileLis.remove(position);
    }

    public void filesMod() {
        initView(SharedPreferencesUtils.getMusicModType(getContext()));
    }

    public void Closed() {
        //当上下文菜单关闭时 取消选中
        CheckBox cb = (CheckBox) longClickSelectView.findViewById(R.id.video_cb);
        cb.setChecked(false);
        cb.setVisibility(View.GONE);
    }

    private synchronized void addFile(File[] files) {
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                loginFile = "sd卡:" + files[i].toString();
                if (files[i] != null && files[i].isFile()) {
                    String name = files[i].getName();
                    if (name.endsWith(".mp3") || name.endsWith(".flac")) {
                        Log.e("aaaaaa", "======" + files[i].getPath());
                        fileLis.add(files[i]);
                    }
                } else if (files[i].isDirectory() && files[i].getParentFile().listFiles() != null) {
                    File[] files2 = files[i].listFiles();
                    addFile(files2);
                }
            }
            if (handler != null && tvZdSearch != null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvZdSearch.setVisibility(View.GONE);
                    }
                }, 100);
            }
        }

    }


    public static void openFile(File file, Context context) {
        String name = file.getName().toLowerCase();
        Intent intent = null;
        intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + file.getPath()), FileUtil.getMINType(name));
        List list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (list != null && list.size() > 0) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "无法播放该音乐！", Toast.LENGTH_SHORT).show();
        }
    }

}
