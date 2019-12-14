package com.mtools.android.activity;

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
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mtools.android.R;
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
import java.util.*;
import java.util.concurrent.CountDownLatch;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crack_music);

        // 初始化组件
        musicId = getIntent().getStringExtra("extra_data");
        musicUrl = "http://music.163.com/song/media/outer/url?id="+musicId+".mp3";
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
            mediaPlayer.setDataSource(musicUrl);
            mediaPlayer.prepare();
            musicStartButton.setVisibility(View.VISIBLE);
            musicPauseButton.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_start_button: {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    musicStartButton.setVisibility(View.GONE);
                    musicPauseButton.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.music_pause_button: {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    musicPauseButton.setVisibility(View.GONE);
                    musicStartButton.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.music_stop_button: {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.reset();
                    initMediaPlayer();
                }
                break;
            }
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
            case R.id.floatingActionButton: {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                // 开始组装数据
                values.put("musicId", musicId);
                values.put("songName", musicName);
                values.put("artistName", musicArtistsName);
                values.put("songCoverUrl", musicCoverUrl);
                // 插入数据
                db.insert("FavouriteList", null, values);
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
     * 传入a_song对象 显示 歌名、歌手、专辑图
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
     * 传入专辑图链接
     * 调用Glide显示圆角图片
     * */
    private void loadAlbumImg(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(crack_music.this)
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

}