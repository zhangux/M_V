package com.cn.mv.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.cn.mv.R;
import com.cn.mv.constants.Constants;
import com.cn.mv.molder.MessageInfo;
import com.cn.mv.util.FileUtil;
import com.cn.mv.util.Utils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/7/5.
 */
public class FileMusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<File> files;
    private LayoutInflater lif;
    private Picasso picasso;
    public RelativeLayout.LayoutParams lp;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemDelClickListener onItemDelClickListener;
    private OnItemUpdateClickListener onItemUpdateClickListener;
    private Map<Integer, File> checkFile;//存储选中的文件
    private Handler handler = new Handler();
    private int modType;

    public Map<Integer, File> getCheckFile() {
        return checkFile;
    }

    public void setCheckFile(Map<Integer, File> checkFile) {
        this.checkFile = checkFile;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemUpdateClickListener(OnItemUpdateClickListener onItemUpdateClickListener) {
        this.onItemUpdateClickListener = onItemUpdateClickListener;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemDelClickListener(OnItemDelClickListener onItemDelClickListener) {
        this.onItemDelClickListener = onItemDelClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    private Context context;
    Map<Integer, FileItemHolder> dels = new HashMap<>();

    public FileMusicAdapter(Context context, List<File> files, int modType) {
        this.context = context;
        lif = LayoutInflater.from(context);
        this.files = files;
        this.modType = modType;
        //初始化图片加载框架
        picasso = Picasso.with(context);
        checkFile = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        lp = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
        View v = lif.inflate(R.layout.layout_music_layout, null);
//        if (modType == 0) {
//            v = lif.inflate(R.layout.layout_video_layout_s, null);
//        } else {
//            v = lif.inflate(R.layout.layout_video_layout_h, null);
//        }
        v.setLayoutParams(lp);
        viewHolder = new FileItemHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FileItemHolder itemHolder = (FileItemHolder) holder;
        if (files.get(position) == null) {
            files.remove(position);
        } else {
            setIcon(files.get(position), itemHolder);
            //判断当前项是否被选中
            if (checkFile.containsKey(position)) {
                itemHolder.checkBox.setChecked(true);
            } else {
                itemHolder.checkBox.setChecked(false);
            }
            if (dels.containsKey(position)) {
                itemHolder.tvDel.setVisibility(View.VISIBLE);
            } else {
                itemHolder.tvDel.setVisibility(View.GONE);
            }
        }
    }

    private synchronized void setIcon(File file, FileItemHolder itemHolder) {
        if (file != null) {
            String name = file.getName();
            if (name.endsWith(".mp3") || name.endsWith(".flac")) {
                itemHolder.img.setImageResource(R.mipmap.nd2);
                itemHolder.img.setTag(file.getPath());
                itemHolder.tvSize.setText(FileUtil.convertToString(file.length()));
                itemHolder.etTitle.setText(name.substring(0, name.lastIndexOf(".")));
                itemHolder.videoHz.setText(name.substring(name.lastIndexOf("."), name.length()));
                itemHolder.etTitle.setTag(file);
                itemHolder.tvTime.setText(FileUtil.getInfo(file));
                if (modType != 0) {
                    itemHolder.img.setImageResource(R.mipmap.all_begin);
                    itemHolder.videoView.setVideoPath(file.getPath());
                    itemHolder.videoView.setOnPreparedListener(itemHolder.preparedListener);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return files == null ? 0 : files.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position / 2 == 1) {
            return 0;
        }
        return 1;
    }

    class FileItemHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private EditText etTitle;
        private TextView tvSize;
        private TextView tvTime;
        private CheckBox checkBox;
        private VideoView videoView;
        //        private SeekBar sb;
//        private TextView tvDel;
        private TextView tvUpdate;
        private TextView videoHz;
        private View vBody;
        private TextView tvDel;

        private HorizontalScrollView horizontalScrollView;
        private View vAll;

        public FileItemHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.music_img);
            etTitle = (EditText) itemView.findViewById(R.id.music_title);
            tvSize = (TextView) itemView.findViewById(R.id.music_size);
            tvTime = (TextView) itemView.findViewById(R.id.music_time);
            checkBox = (CheckBox) itemView.findViewById(R.id.music_cb);
            tvUpdate = (TextView) itemView.findViewById(R.id.music_update_title);
            videoHz = (TextView) itemView.findViewById(R.id.music_hz);
            tvDel = (TextView) itemView.findViewById(R.id.tv_delete);
//            horizontalScrollView = (HorizontalScrollView) itemView.findViewById(R.id.music_hsv);
            vBody = itemView.findViewById(R.id.music_body);
//            vAll = itemView.findViewById(R.id.music_hsv);

//            vAll.setTag(this);
//            vAhorizontalScrollViewll.setMinimumWidth(Utils.getScreenWidth(context));
//            vAll.setOnTouchListener(touchLis);


            vBody.setTag(this);
//            vBody.setOnTouchListener(touchLis);
            vBody.setOnClickListener(clickLis);
            vBody.setOnLongClickListener(longClickLis);
            vBody.setMinimumWidth(Utils.getScreenWidth(context));

            checkBox.setTag(this);
            checkBox.setOnCheckedChangeListener(checkedChangeLis);
            etTitle.setOnFocusChangeListener(focusChangeLis);

            tvDel.setTag(this);
            tvDel.setOnClickListener(clickLis);

            tvUpdate.setTag(this);
            tvUpdate.setOnClickListener(clickLis);
//            if (modType != 0) {
//                img.setOnClickListener(clickPLis);
//                videoView = (VideoView) itemView.findViewById(R.id.video_video);
//                sb = (SeekBar) itemView.findViewById(R.id.video_sb);
//                sb.setEnabled(false);
//            }
        }

        private View.OnFocusChangeListener focusChangeLis = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                vBody.setMinimumWidth(Utils.getScreenWidth(context));
                switch (v.getId()) {
                    case R.id.music_title:
                        if (checkFile.size() <= 0) {
                            if (hasFocus) {
                                tvUpdate.setVisibility(View.VISIBLE);
                            } else {
                                tvUpdate.setVisibility(View.GONE);
                            }
                        }
                        break;
                }
            }
        };

        private MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                mp.start();
//                mp.pause();
//                sb.setMax(mp.getDuration());
//                设置进度条的拖拽改变监听
//                sb.setOnSeekBarChangeListener(seekBarChangeLis);
//                sb.setEnabled(true);
//                handler.post(r);
            }
        };


        private SeekBar.OnSeekBarChangeListener seekBarChangeLis = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(r);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.postDelayed(r, 1000);
            }
        };

        private Runnable r = new Runnable() {
            @Override
            public void run() {
                //获取当前播放时间
//                int currTime = videoView.getCurrentPosition();
//                if (!videoView.isPlaying()) {
//                    img.setImageResource(R.mipmap.all_begin);
//                }
//                sb.setProgress(currTime);
//                handler.postDelayed(this, 1000);
            }
        };
    }
    private View.OnTouchListener touchLis = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction()==MotionEvent.ACTION_MOVE){
                clearDels();
                RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) v.getTag();
                Integer position = vh.getAdapterPosition();
                ((FileItemHolder) vh).tvDel.setVisibility(View.VISIBLE);
                dels.put(position, (FileItemHolder) vh);
            }
            Log.e("aaaaaa","=="+event.getAction());
            return true;
        }
    };

    private void clearDels() {
        Set<Map.Entry<Integer, FileItemHolder>> set = dels.entrySet();
        for (Map.Entry<Integer, FileItemHolder> entry : set) {
            entry.getValue().tvDel.setVisibility(View.GONE);
        }
        dels.clear();
    }

    private CompoundButton.OnCheckedChangeListener checkedChangeLis = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean b) {
            //获取父级视 获取父级视图itemVlew的eTag
            RecyclerView.ViewHolder vh = (RecyclerView.ViewHolder) buttonView.getTag();
            Integer position = vh.getAdapterPosition();
            if (b) {//如果选中，添加
                checkFile.put(position, files.get(position));
            } else {//取消选中 移除
                checkFile.remove(position);
            }
            if ((((FileItemHolder) vh).checkBox.isChecked())) {
                ((FileItemHolder) vh).checkBox.setVisibility(View.VISIBLE);
            } else {
                ((FileItemHolder) vh).checkBox.setVisibility(View.GONE);
            }
            EventBus.getDefault().post(new MessageInfo(Constants.ITEM_CHECKED_CHANGED, checkFile));
        }
    };
    private View.OnClickListener clickLis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            int position = holder.getAdapterPosition();
            File tmp = files.get(position);
            ((FileItemHolder) holder).tvDel.setVisibility(View.VISIBLE);
            switch (v.getId()) {
                case R.id.tv_delete:
                    if (checkFile.size() <= 0) {
//                        onItemDelClickListener.onItemDelClick(v, position, tmp);
                    }
                    break;
                case R.id.music_body:
                    onItemClickListener.onItemClick(v, position, tmp);
                    break;
                case R.id.music_update_title:
                    if (checkFile.size() <= 0) {
                        String rename = ((FileItemHolder) holder).etTitle.getText().toString()
                                + ((FileItemHolder) holder).videoHz.getText().toString();
                        onItemUpdateClickListener.onItemUpdateClick(v, position, tmp, rename);
                    }
                    ((FileItemHolder) holder).tvUpdate.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private View.OnLongClickListener longClickLis = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            int position = holder.getAdapterPosition();
            File tmp = files.get(position);
            onItemLongClickListener.onItemLongClick(v, position, tmp);
            return true;
        }
    };

    public void selectAll() {
        for (int i = 0; i < files.size(); i++) {
            checkFile.put(i, files.get(i));
        }
        notifyDataSetChanged();
    }

    public void cancleAllSelectedItem() {
        checkFile.clear();
        notifyDataSetChanged();
    }

    private static final long KB = 1024;
    private static final long MB = KB * 1024;
    private static final long GB = MB * 1024;


    public interface OnItemClickListener {
        void onItemClick(View v, int position, File file);
    }

    public interface OnItemDelClickListener {
        void onItemDelClick(View v, int position, File file);
    }

    public interface OnItemUpdateClickListener {
        void onItemUpdateClick(View v, int position, File file, String rename);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View v, int position, File file);
    }
}
