package com.cn.mv;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.mv.adapter.MyAdapter;
import com.cn.mv.constants.Constants;
import com.cn.mv.fragment.FragmentHttpMV;
import com.cn.mv.fragment.FragmentMusic;
import com.cn.mv.fragment.FragmentVideo;
import com.cn.mv.molder.MessageInfo;
import com.cn.mv.util.FileUtil;
import com.cn.mv.util.SharedPreferencesUtils;
import com.cn.mv.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager vp;
    private Toolbar toolbar;
    private SlidingPaneLayout slidingPaneLayout;
    private ImageView imgTitle;

    private List<Fragment> fragments;
    private MyAdapter adapter;
    private View llMenu;
    private View body;
    private View bodyBack;

    private View view;
    private ImageView exitUpdate;
    private TextView tvCount;
    private TextView tvAll;
    private TextView tvAdd;
    private TextView tvDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_main);
        bindView();
        initView();
    }

    private void bindView() {
        tabLayout = (TabLayout) findViewById(R.id.main_tab);
        vp = (ViewPager) findViewById(R.id.main_vp);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        this.setSupportActionBar(toolbar);

//        toolbar.setOnMenuItemClickListener(menuItemClickLis);
        imgTitle = (ImageView) findViewById(R.id.toolbar_img);
        imgTitle.setOnClickListener(clickLis);
        llMenu = findViewById(R.id.main_left_menu);
        body = findViewById(R.id.main_body);
        bodyBack = findViewById(R.id.main_body_back);
        slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.main_spl);
        slidingPaneLayout.setPanelSlideListener(slideLis);
        slidingPaneLayout.setSliderFadeColor(Color.TRANSPARENT);

        view = findViewById(R.id.main_update_menu);
        exitUpdate = (ImageView) findViewById(R.id.exit_main_update_menu);
        tvCount = (TextView) findViewById(R.id.main_update_count);
        tvAll = (TextView) findViewById(R.id.main_update_all);
        tvAdd = (TextView) findViewById(R.id.main_update_add);
        tvDel = (TextView) findViewById(R.id.main_update_del);

        exitUpdate.setOnClickListener(clickLis);
        tvAll.setOnClickListener(clickLis);
        tvAdd.setOnClickListener(clickLis);
        tvDel.setOnClickListener(clickLis);
    }

    private Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        if (vp.getCurrentItem() == 0) {
            getMenuInflater().inflate(R.menu.video_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.music_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (map != null && map.size() > 0) {
            view.setVisibility(View.GONE);
            FragmentVideo fragmentVideo = findFragmentVideoByPosition(vp.getCurrentItem());
            fragmentVideo.cancleAllSelectedItem();
        }
        switch (item.getItemId()) {
            case R.id.order_name:
                order(0);
                break;
            case R.id.order_big_date:
                order(1);
                break;
            case R.id.order_date:
                order(2);
                break;
            case R.id.order_big_size:
                order(3);
                break;
            case R.id.order_size:
                order(4);
                break;
            case R.id.select_mod:
                selectType(0);
                break;
            case R.id.select_look_mod:
                selectType(1);
                break;
        }
        return true;
    }

    private void initView() {
        EventBus.getDefault().register(this);
        Bundle bundle = null;
        fragments = new ArrayList<>();
        FragmentVideo fragmentVideo = new FragmentVideo();
        fragments.add(fragmentVideo);
        bundle = new Bundle();
        bundle.putSerializable("title", "视频");
        fragmentVideo.setArguments(bundle);

        FragmentMusic fragmentMusic = new FragmentMusic();
        fragments.add(fragmentMusic);
        bundle = new Bundle();
        bundle.putSerializable("title", "音乐");
        fragmentMusic.setArguments(bundle);

        FragmentHttpMV fragmentHttpMV = new FragmentHttpMV();
        fragments.add(fragmentHttpMV);
        bundle = new Bundle();
        bundle.putSerializable("title", "我的");
        fragmentHttpMV.setArguments(bundle);


        adapter = new MyAdapter(fragments, getSupportFragmentManager());
        vp.setAdapter(adapter);
        tabLayout.setupWithViewPager(vp);
        vp.setOnPageChangeListener(pageChangeLis);
        //模糊------
        Bitmap bmp = ((BitmapDrawable) bodyBack.getBackground()).getBitmap();
        blur(bmp);
        //----------
    }

    private FragmentVideo fragmentVideo;
    private FragmentMusic fragmentMusic;

    private void getFragmentMVH() {
        if (vp.getCurrentItem() == 0) {
            fragmentVideo = findFragmentVideoByPosition(vp.getCurrentItem());
        } else if (vp.getCurrentItem() == 1) {
            fragmentMusic = findFragmentMusicByPosition(vp.getCurrentItem());
        }
    }

    private ViewPager.OnPageChangeListener pageChangeLis = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            menu.clear();
            onCreateOptionsMenu(menu);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            getFragmentMVH();
            view.setVisibility(View.GONE);
            if (vp.getCurrentItem() == 0 && map != null && map.size() > 0) {
                fragmentVideo.cancleAllSelectedItem();
            } else if (vp.getCurrentItem() == 1 && map != null && map.size() > 0) {
                fragmentMusic.cancleAllSelectedItem();
            }
        }
    };

    private void order(int type) {
        getFragmentMVH();
        if (vp.getCurrentItem() == 0) {
            SharedPreferencesUtils.saveVideoOrderType(this, type);
            fragmentVideo = findFragmentVideoByPosition(vp.getCurrentItem());
            fragmentVideo.fileOrder();
        } else if (vp.getCurrentItem() == 1) {
            SharedPreferencesUtils.saveMusicOrderType(this, type);
            fragmentMusic = findFragmentMusicByPosition(vp.getCurrentItem());
            fragmentMusic.fileOrder();
        }
    }

    private void selectType(int type) {
        getFragmentMVH();
        if (vp.getCurrentItem() == 0) {
            SharedPreferencesUtils.saveVideoModType(this, type);
            fragmentVideo = findFragmentVideoByPosition(vp.getCurrentItem());
            fragmentVideo.filesMod();
        } else if (vp.getCurrentItem() == 1) {
            SharedPreferencesUtils.saveMusicModType(this, type);
            fragmentMusic = findFragmentMusicByPosition(vp.getCurrentItem());
            fragmentMusic.filesMod();
        }
    }

    private View.OnClickListener clickLis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getFragmentMVH();
            switch (v.getId()) {
                case R.id.toolbar_img:
                    slidingPaneLayout.openPane();
                    break;
                case R.id.exit_main_update_menu:
                    view.setVisibility(View.GONE);
                    if (vp.getCurrentItem() == 0) {
                        fragmentVideo.cancleAllSelectedItem();
                    } else if (vp.getCurrentItem() == 1) {
                        fragmentMusic.cancleAllSelectedItem();
                    }
                    break;
                case R.id.main_update_all:
                    if (vp.getCurrentItem() == 0) {
                        fragmentVideo.selectAll();
                    } else if (vp.getCurrentItem() == 1) {
                        fragmentMusic.selectAll();
                    }
                    break;
                case R.id.main_update_del:
                    if (map != null && map.size() > 0) {
                        delete();
                    }
                    break;
                case R.id.main_update_add:
                    view.setVisibility(View.GONE);
                    if (vp.getCurrentItem() == 0) {
                        fragmentVideo.cancleAllSelectedItem();
                    } else if (vp.getCurrentItem() == 1) {
                        fragmentMusic.cancleAllSelectedItem();
                    }
                    break;
            }
        }
    };

    private void delete() {
        new AlertDialog.Builder(this)
                .setTitle("删除")
                .setMessage("确定要删除这 " + map.size() + " 项吗?")
                .setNegativeButton("确定", deleteFileClickLis)
                .setPositiveButton("取消", deleteFileClickLis)
                .show();
    }

    private DialogInterface.OnClickListener deleteFileClickLis = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int i) {
            switch (i) {
                case DialogInterface.BUTTON_NEGATIVE:
                    Set<Map.Entry<Integer, File>> set = map.entrySet();
                    getFragmentMVH();
                    if (vp.getCurrentItem() == 0) {
                        for (Map.Entry<Integer, File> entry : set) {
                            for (int j = 0; j < fragmentVideo.fileLis.size(); j++) {
                                if (fragmentVideo.fileLis.get(j) == (entry.getValue())) {
                                    FileUtil.delete(entry.getValue());
                                    fragmentVideo.remove(j);
                                }
                            }
                        }
                        fragmentVideo.refreshFile();
                        fragmentVideo.cancleAllSelectedItem();
                    } else if (vp.getCurrentItem() == 1) {
                        for (Map.Entry<Integer, File> entry : set) {
                            for (int j = 0; j < fragmentMusic.fileLis.size(); j++) {
                                if (fragmentMusic.fileLis.get(j) == (entry.getValue())) {
                                    FileUtil.delete(entry.getValue());
                                    fragmentMusic.remove(j);
                                }
                            }
                        }
                        fragmentMusic.refreshFile();
                        fragmentMusic.cancleAllSelectedItem();
                    }
                    view.setVisibility(View.GONE);
                    break;
                case DialogInterface.BUTTON_POSITIVE:

                    break;
            }
        }
    };
    private Map<Integer, File> map;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageInfo info) {
        switch (info.what) {
            case Constants.ITEM_CHECKED_CHANGED:
                map = (Map<Integer, File>) info.obj;
                if (map != null && map.size() > 0) {
                    view.setVisibility(View.VISIBLE);
                    tvCount.setText("选中 " + map.size() + " 个文件");
                } else {
                    view.setVisibility(View.GONE);
                }
                break;
        }
    }

    private FragmentVideo findFragmentVideoByPosition(int position) {
        String tag = "android:switcher:" + vp.getId() + ":" + position;
        return (FragmentVideo) getSupportFragmentManager().findFragmentByTag(tag);
    }

    private FragmentMusic findFragmentMusicByPosition(int position) {
        String tag = "android:switcher:" + vp.getId() + ":" + position;
        return (FragmentMusic) getSupportFragmentManager().findFragmentByTag(tag);
    }

    private SlidingPaneLayout.PanelSlideListener slideLis = new SlidingPaneLayout.PanelSlideListener() {
        @Override //滑动中调用
        public void onPanelSlide(View panel, float slideOffset) {
            int l = (int) ((1 - slideOffset) * llMenu.getWidth());
            llMenu.layout(-l, llMenu.getTop(), llMenu.getWidth() - l, llMenu.getBottom());
            llMenu.setScaleY(0.7f + 0.3f * slideOffset);
            llMenu.setScaleX(0.7f + 0.3f * slideOffset);
            llMenu.setAlpha(0.7f + 0.3f * slideOffset);
//            body.getPivotY();
            body.setPivotX(body.getWidth() / 6);
            body.setPivotY(body.getHeight() / 2);
            body.setScaleX(1 - 0.3f * slideOffset);
            body.setScaleY(1 - 0.3f * slideOffset);
        }

        @Override //打开后调用
        public void onPanelOpened(View panel) {

        }

        @Override //关闭后调用
        public void onPanelClosed(View panel) {

        }
    };

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);

    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.show("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //模糊
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blur(Bitmap bkg) {
        long startMs = System.currentTimeMillis();
        float radius = 25;

        bkg = small(bkg);
        Bitmap bitmap = bkg.copy(bkg.getConfig(), true);

        final RenderScript rs = RenderScript.create(this);
        final Allocation input = Allocation.createFromBitmap(rs, bkg, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);

        bitmap = big(bitmap);
        bodyBack.setBackground(new BitmapDrawable(getResources(), bitmap));
        rs.destroy();
        Log.d("zhangle", "blur take away:" + (System.currentTimeMillis() - startMs) + "ms");
    }

    private static Bitmap big(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(4f, 4f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    private static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.25f, 0.25f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }
}
