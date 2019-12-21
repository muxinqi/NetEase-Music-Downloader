package com.mtools.android.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mtools.android.R;
import com.mtools.android.adapter.CommentAdapter;
import com.mtools.android.class_define.MusicComment.Comment;
import com.mtools.android.class_define.MusicComment.HotComments;
import com.mtools.android.class_define.MusicComment.User;
import com.mtools.android.class_define.CommentItem;
import com.mtools.android.class_define.MusicDownload.Data;
import com.mtools.android.class_define.MusicDownload.MusicDownload;
import com.mtools.android.class_define.single_music.A_Song;
import com.mtools.android.class_define.single_music.Artists;
import com.mtools.android.class_define.single_music.Songs;
import com.mtools.android.db.MyDatabaseHelper;
import com.mtools.android.other.GlideApp;
import com.mtools.android.service.DownloadService;
import com.mtools.android.util.HttpUtil;
import com.mtools.android.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.bumptech.glide.request.RequestOptions.circleCropTransform;

public class crack_music extends AppCompatActivity implements View.OnClickListener {

    private String musicId = null;

    private String musicUrl = null;

    private String musicName = null;

    private String musicArtistsName = null;

    private String musicCoverUrl = null;

    private String musicFileName = null;

    private TextView songNameText;

    private TextView artistsNameText;

    private TextView songAliasText;

    private ImageView songImage;

    private Button musicStartButton;

    private Button musicPauseButton;

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private A_Song a_song;

    private boolean isStartDownload = false;

    private boolean isPauseDownload = false;

    private MyDatabaseHelper dbHelper;

    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获取实例
            downloadBinder = (DownloadService.DownloadBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private String commentUrl;

    private List<CommentItem> commentItemList = new ArrayList<>();

    ListView commentListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crack_music);

        // 初始化组件
        musicId = getIntent().getStringExtra("extra_data");
        musicUrl = "http://music.163.com/song/media/outer/url?id="+musicId+".mp3";
        commentUrl = "http://music.163.com/api/v1/resource/comments/R_SO_4_"+musicId+"?limit=10";
        Toolbar toolbar = findViewById(R.id.toolbar);
        songNameText = findViewById(R.id.song_name_text);
        artistsNameText = findViewById(R.id.artists_name_text);
        songImage = findViewById(R.id.song_image);
        songAliasText = findViewById(R.id.song_alias_text);
        musicStartButton = findViewById(R.id.music_start_button);
        musicPauseButton = findViewById(R.id.music_pause_button);
        Button musicStopButton = findViewById(R.id.music_stop_button);
        Button musicDownloadButton = findViewById(R.id.music_download_button);
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        dbHelper = new MyDatabaseHelper(this, "FavouriteList.db", null, 1);

        // 激活 Toolbar
        setSupportActionBar(toolbar);

        // 监听点击操作
        musicStartButton.setOnClickListener(this);
        musicPauseButton.setOnClickListener(this);
        musicStopButton.setOnClickListener(this);
        musicDownloadButton.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);

        // 启动下载服务
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);

        // 绑定下载服务
        bindService(intent, connection, BIND_AUTO_CREATE);
        if (ContextCompat.checkSelfPermission(crack_music.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(crack_music.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        }

        // 监听长按操作 - 取消下载
        musicDownloadButton.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View view) {
                if (downloadBinder == null) {
                    return true;
                }
                downloadBinder.cancelDownload();
                // 重置标记
                isStartDownload = false;
                isPauseDownload = false;
                Toast.makeText(crack_music.this, "Long Clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // 请求服务器 获取歌曲信息
        requestA_Song();

        // 初始化 MediaPlayer
        initMediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                musicStartButton.setVisibility(View.VISIBLE);
                musicPauseButton.setVisibility(View.INVISIBLE);
            }
        });

        // 显示歌曲评论
        requestCommentsList();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 初始化 MediaPLayer
     * 设置当前音乐地址为数据源
     * */
    private void initMediaPlayer() {
        try {
            Log.d("GOOD", "musicUrl: "+musicUrl);
            mediaPlayer.setDataSource(musicUrl);
            mediaPlayer.prepare();
            musicStartButton.setVisibility(View.VISIBLE);
            musicPauseButton.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(crack_music.this, "音频获取失败，请检查API可用性", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 点击音乐开始按钮
            case R.id.music_start_button: {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    musicStartButton.setVisibility(View.GONE);
                    musicPauseButton.setVisibility(View.VISIBLE);
                }
                break;
            }
            // 点击音乐暂停按钮
            case R.id.music_pause_button: {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    musicPauseButton.setVisibility(View.GONE);
                    musicStartButton.setVisibility(View.VISIBLE);
                }
                break;
            }
            // 点击音乐停止按钮
            case R.id.music_stop_button: {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                    initMediaPlayer();
                }
                break;
            }
            // 点击下载按钮
            case R.id.music_download_button: {
                Log.d("GOOD", "onClick: download");
                Log.d("GOOD", "onClick: binder is null: "+(downloadBinder == null));
                if (downloadBinder == null) {
                    Log.d("GOOD", "downloadBinder is null");
                    return;
                }
                Log.d("GOOD", "startDL:"+isStartDownload);
                Log.d("GOOD", "pauseDL:"+isPauseDownload);
                // 未开始&未暂停 表示'开始下载'
                // 已开始&已暂停 表示'继续下载'
                if ((!isStartDownload && !isPauseDownload)
                        || (isStartDownload && isPauseDownload)) {
                    // 获取下载链接
                    String downloadUrl = requestDownloadUrl();
                    if (downloadUrl != null && musicFileName != null) {
                        downloadBinder.startDownload(downloadUrl, musicFileName);
                        Log.d("GOOD", "startUrl: "+ musicUrl);
                        if (isStartDownload && isPauseDownload) {
                            // 若为'继续下载'操作 - 取消标记暂停
                            isPauseDownload = false;
                        } else {
                            // 否则为'开始下载'操作 - 标记开始
                            isStartDownload = true;
                        }
                    }
                }
                // 剩下两种异或的情况中
                // 若已开始下载 此时表示'暂停'操作
                else if (isStartDownload) {
                    Log.d("GOOD", "333333333");
                    downloadBinder.pauseDownload();
                    isPauseDownload = true;
                }
                break;
            }
            // 点击悬浮按钮
            case R.id.floatingActionButton: {
                // 查找当前歌曲是否已存在数据库中
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor = db.query("FavouriteList", null, null, null, null, null, null);
                int count = 0;
                boolean isShow = true;
                if (cursor.moveToNext()) {
                    do {
                        if (cursor.getString(cursor.getColumnIndex("musicId")).equals(musicId)) {
                            count++;
                            if (count == 1) {
                                isShow = (Integer.parseInt(cursor.getString(cursor.getColumnIndex("isShow"))) == 1);
                                break;
                            }
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                // 不存在 则添加
                if (count == 0) {
                    ContentValues values = new ContentValues();
                    // 开始组装数据
                    values.put("musicId", musicId);
                    values.put("songName", musicName);
                    values.put("artistName", musicArtistsName);
                    values.put("songCoverUrl", musicCoverUrl);
                    values.put("isShow", 1);
                    // 插入数据
                    db.insert("FavouriteList", null, values);
                    Toast.makeText(crack_music.this, "《"+musicName+"》加入收藏歌单成功", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isShow) {
                        // 存在但隐藏 修改为显示
                        ContentValues values = new ContentValues();
                        values.put("isShow", 1);
                        db.update("FavouriteList", values, "musicId = ?", new String[] { musicId });
                    }
                    Toast.makeText(crack_music.this, "《"+musicName+"》已存在于收藏歌单", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        // 解绑服务
        unbindService(connection);
    }


    /**
     * 根据歌曲ID获取并返回下载链接
     * */
    public String requestDownloadUrl() {
        if (musicId != null) {
            Log.d("GOOD", "requestDownloadUrl: musicId = " + musicId);
            final String music_download_info_url = "https://api.imjad.cn/cloudmusic/?type=song&id=" + musicId;
            final String[] downloadUrl = {null};
            /*
             * 因为OkHttp3为多线程执行 所以需要使用
             * CountDownLatch 控制线程的执行
             * 等待所有线程执行完毕后再返回结果
             * 以防由于网络波动导致未获取到数据就返回Null
             */
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            HttpUtil.sendOkHttpRequest(music_download_info_url, new Callback() {

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    final String response_text = Objects.requireNonNull(response.body()).string();
                    final MusicDownload musicDownload = Utility.handleJsonToObject(response_text, MusicDownload.class, false);
                    String music_download_url = null;
                    int count = 0;
                    for (Data musicDownloadData : musicDownload.getData()) {
                        if (count == 0) {
                            music_download_url = musicDownloadData.getUrl();
                            Log.d("GOOD", "onResponse: musicDownloadUrl 1 = " + music_download_url);
                        }
                        count++;
                    }
                    downloadUrl[0] = music_download_url;
                    countDownLatch.countDown();
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(crack_music.this, "下载链接获取失败\n请检查API可用性", Toast.LENGTH_SHORT).show();
                        }
                    });
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
                return downloadUrl[0];
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    /**
     * 根据歌曲id请求歌曲信息
     */
    public void requestA_Song() {
        final String music_info_url = "http://music.163.com/api/song/detail/?ids=%5B" + musicId + "%5D";
        Log.d("GOOD", "music info url " + music_info_url);
        HttpUtil.sendOkHttpRequest(music_info_url, new okhttp3.Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = Objects.requireNonNull(response.body()).string();
                Log.d("GOOD", responseText);
                a_song = Utility.handleJsonToObject(responseText, A_Song.class, false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("GOOD", "a_song code: " + a_song.getCode());
                        if (a_song.getCode() == 200) {
                            showA_SongInfo(a_song);
                        } else {
                            Toast.makeText(crack_music.this, "获取歌曲信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(crack_music.this, "获取歌曲信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * @param a_song 传入a_song对象
     * 显示歌名、歌手、专辑图
     * */
    private void showA_SongInfo(A_Song a_song) {
        // 由返回的JSON可知 Songs永远只有一个 所以此循环只运行一次
        for (Songs songs : a_song.getSongs()) {
            Log.d("GOOD", "songs_getName: "+songs.getName());

            // 显示歌曲名称
            musicName = songs.getName();
            songNameText.setText(musicName);
            String song_alias;
            for (String alias : songs.getAlias()) {
                if (alias != null) {
                    song_alias = alias;
                    songAliasText.setText(song_alias);
                    songAliasText.setVisibility(View.VISIBLE);
                }
            }
            // 查询歌手姓名
            StringBuilder singer_name_stb = new StringBuilder();
            int count = 0;
            for (Artists artists : songs.getArtists()) {
                if (count == 0) {
                    singer_name_stb.append(artists.getName());
                } else {
                    singer_name_stb.append(" / ").append(artists.getName());
                }
                count++;
            }
            // 显示歌手姓名
            musicArtistsName = new String(singer_name_stb);
            artistsNameText.setText(musicArtistsName);
            musicFileName = songs.getName()+" - "+musicArtistsName+".mp3";

            //记录专辑图链接
            musicCoverUrl = songs.getAlbum().getPicUrl() + "?param=200y200";
            Log.d("GOOD", "picUrl: "+musicCoverUrl);

            // 显示专辑图
            loadAlbumImg(musicCoverUrl);
        }
    }
    /**
     * @param url 专辑图链接
     * 调用Glide显示圆角图片
     * */
    private void loadAlbumImg(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GlideApp.with(crack_music.this)
                        .load(url)
                        .transform(new RoundedCornersTransformation(25, 0, RoundedCornersTransformation.CornerType.ALL))
                        .error(R.drawable.album_loading_img)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(songImage);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "拒绝权限将无法下载", Toast.LENGTH_SHORT).show();
//                finish();
            }
        }
    }

    /**
     * 根据评论API链接 获取json数据并反序列化为comment对象
     * 调用 两个函数来显示歌曲评论
     * */
    private void requestCommentsList() {
        HttpUtil.sendOkHttpRequest(commentUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(crack_music.this, "获取评论信息失败\n请检查API可用性", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = Objects.requireNonNull(response.body()).string();
                Log.d("GOOD", "onResponse: "+responseText.substring(responseText.length()-500, responseText.length()-1));
                final Comment comment = Utility.handleJsonToObject(responseText, Comment.class, false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (comment.getCode() == 200) {
                                initCommentList(comment);
                                showCommentList();
                            }
                        } catch (Exception e) {
                            Toast.makeText(crack_music.this, "歌曲评论状态码错误", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * 显示评论的ListView
     * */
    private void showCommentList() {
        commentListView = findViewById(R.id.music_comments_listview);
        commentListView.setAdapter(new CommentAdapter(crack_music.this, R.layout.comments_item, commentItemList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                CommentItem commentItem = getItem(position);    // 获取当前项的CommentItem对象
                View view;
                if (convertView == null) {
                    // convertView 用于缓存之前加载好的布局
                    // 若其为空则需要加载
                    view = LayoutInflater.from(getContext()).inflate(getResourceId(), parent, false);
                } else {
                    // 若其不为空则直接调用它
                    view = convertView;
                }
                // 初始化组件
                ImageView commentatorAvatar = view.findViewById(R.id.commentators_avatar_image);
                TextView commentatorName = view.findViewById(R.id.commentators_name_text);
                TextView commentatorContent = view.findViewById(R.id.commentators_content_text);
                TextView commentTime = view.findViewById(R.id.comment_time_text);
                TextView likedCount = view.findViewById(R.id.comment_like_count_text);
                // Glide 加载评论者头像（圆形）
                GlideApp.with(getContext())
                        .load(Objects.requireNonNull(commentItem).getCommentAvatarUrl())
                        .apply(circleCropTransform())
                        .into(commentatorAvatar);
                // 显示内容
                commentatorName.setText(commentItem.getCommentName());
                commentatorContent.setText(commentItem.getCommentContent());
                // 日期需要处理之后使用
                Date time = commentItem.getTime();
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA);
                commentTime.setText(ft.format(time));
                likedCount.setText(String.valueOf(commentItem.getLikedCount()));
                return view;
            }
        });
    }

    /**
     * @param comment 评论数据实体化的对象
     * 遍历对象 填充commentItemList列表
     * */
    private void initCommentList(Comment comment) {
        for (HotComments hotComments : comment.getHotComments()) {
            String commentContent = hotComments.getContent();
            long likedCount = hotComments.getLikedCount();
            Date time = new Date(hotComments.getTime());

            User user = hotComments.getUser();
            String commentName = user.getNickname();
            String commentAvatarUrl = user.getAvatarUrl() + "?param=64y64";

            CommentItem commentItem = new CommentItem(commentName, commentContent, commentAvatarUrl, likedCount, time);
            commentItemList.add(commentItem);
        }
    }

}