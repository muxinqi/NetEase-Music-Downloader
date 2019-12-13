package com.mtools.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mtools.android.class_define.Music;
import com.mtools.android.adapter.PlaylistAdapter;
import com.mtools.android.R;
import com.mtools.android.class_define.playlist.imjad.A_Playlist;
import com.mtools.android.class_define.playlist.imjad.Ar;
import com.mtools.android.class_define.playlist.imjad.Tracks;
import com.mtools.android.other.GlideApp;
import com.mtools.android.util.HttpUtil;
import com.mtools.android.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.bumptech.glide.request.RequestOptions.circleCropTransform;

public class crack_playlist extends AppCompatActivity {

    private String playlistUrl;

    private ImageView playlistCoverImage;

    private ImageView playlistOwnerAvatarImage;

    private TextView playlistNameText;

    private TextView playlistOwnerText;

    private ListView playlistListView;

    private List<Music> musicList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crack_playlist);
        String playlistId = getIntent().getStringExtra("extra_data");
        Log.d("GOOD", "Playlist ID: " + playlistId);
        playlistUrl = "https://api.imjad.cn/cloudmusic/?type=playlist&id=" + playlistId;
        Log.d("GOOD", "Playlist URL: " + playlistUrl);
        // 组件初始化
        playlistCoverImage = findViewById(R.id.playlist_cover_image);
        playlistOwnerAvatarImage = findViewById(R.id.playlist_owner_avatar_image);
        playlistNameText = findViewById(R.id.playlist_name_text);
        playlistOwnerText = findViewById(R.id.playlist_owner_name_text);

        playlistListView = findViewById(R.id.playlist_listview);

        // ListView Array Data
//        playlistListView = (ListView)findViewById(R.id.playlist_listview);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                crack_playlist.this, android.R.layout.simple_list_item_1, data);
//        playlistListView.setAdapter(adapter);

        // ListView
//        initPlaylist();
//        PlaylistAdapter adapter = new PlaylistAdapter(crack_playlist.this, R.layout.playlist_item, musicList);
//        playlistListView.setAdapter(adapter);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Display Playlist Info
//        requestPlaylist();

        // Display Playlist Info By Jackson

        requestPlaylistByJackson();
        Log.d("GOOD", "Request Playlist Info complete!");

    }

    private void showPlaylistItem(A_Playlist a_playlist) {
        Log.d("GOOD", "Begin initPlaylist");
        initPlaylist(a_playlist);
        Log.d("GOOD", "Init Playlist complete!");
        playlistListView.setAdapter(new PlaylistAdapter(crack_playlist.this, R.layout.playlist_item, musicList) {
            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                Music music = getItem(position);//获取当前项的Fruit实例
                View view;
                if (convertView == null) {
                    // convertView 用于缓存之前加载好的布局
                    // 若其为空则需要加载
                    view = LayoutInflater.from(getContext()).inflate(getResourceId(), parent, false);
                } else {
                    // 若其不为空则直接调用它
                    view = convertView;
                }
                ImageView songImage = view.findViewById(R.id.playlist_item_song_image);
                TextView songName = view.findViewById(R.id.playlist_item_song_name_text);
                // Glide
                GlideApp.with(getContext())
                        .load(Objects.requireNonNull(music).getSongImgUrl())
                        .transform(new RoundedCornersTransformation(10, 0, RoundedCornersTransformation.CornerType.ALL))
                        .into(songImage);
                songName.setText(music.getSongName());
                return view;
            }
        });
        // 歌单列表点击操作
        playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = musicList.get(position);
                Toast.makeText(crack_playlist.this, "查看《"+music.getSongName()+"》", Toast.LENGTH_SHORT).show();
                // 将long转为String 以便intent传递数据
                String songIdStr = Long.toString(music.getSongId());
                Intent intent = new Intent(crack_playlist.this, crack_music.class);
                intent.putExtra("extra_data", songIdStr);
                startActivity(intent);
            }
        });
    }


    /**
     * 遍历歌单中的每首音乐的信息 添加到 List 中
     * */
    private void initPlaylist(A_Playlist a_playlist) {
        for (Tracks eachMusic : a_playlist.getPlaylist().getTracks()) {
            long songId = eachMusic.getId();
            String songName = eachMusic.getName();
            String songCoverUrl = eachMusic.getAl().getPicUrl()+"?param=64y64";
            // 遍历歌手（一个或多个）
            // 循环拼接字符串使用StringBuilder
            StringBuilder artistsNameStrB = new StringBuilder();
            int count = 0;
            for (Ar artist : eachMusic.getAr()) {
                if (count == 0) {
                    artistsNameStrB.append(artist.getName());
                } else {
                    String temp = " / " + artist.getName();
                    artistsNameStrB.append(temp);
                }
                count++;
            }
            String artistsName = new String(artistsNameStrB);
            Log.d("GOOD", "id: "+ songId + ", " + songName + ", " + songCoverUrl);
            Music item = new Music(songId, songName, artistsName, songCoverUrl);
            musicList.add(item);

        }
//        for(int i = 0; i < 100; i++) {
//            Music a = new Music("A","https://cdn.v2ex.com/avatar/677c/39d0/274232_large.png");
//            musicList.add(a);
//        }
    }

    // Toolbar的事件---返回
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    /*
      OkHttp3 获取 JSON
      Gson 反序列化 JSON 为 a_playlist 对象
      */
//    public void requestPlaylist() {
//        Log.d("GOOD", "RequestPlaylist... ");
//        // 使用 OkHttp3 访问playlistUrl获得response供内部使用
//        HttpUtil.sendOkHttpRequest(playlistUrl, new Callback() {
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                final String responseText = response.body().string();
//                a_playlist = Utility.handleA_PlaylistResponse(responseText);
//                Log.d("GOOD", "Get Playlist Object Successful!");
//                Log.d("GOOD", "Here is the response Text: " + responseText);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("GOOD", "Code: " + a_playlist.getCode());
//                        if (a_playlist.getCode() == 200) {
//                            showPlaylistInfo(a_playlist);
//                        } else {
//                            Toast.makeText(crack_playlist.this, "歌单状态码错误", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Log.d("GOOD", "OkHttp3 Request Failed");
//                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(crack_playlist.this, "获取歌单信息失败", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//    }

    /**
     * OkHttp3 获取 JSON
     * Jackson 反序列化 JSON 为 a_playlist 对象
     * */
    public void requestPlaylistByJackson() {
        Log.d("GOOD", "Request Playlist Begin ");
        // 使用 OkHttp3 访问playlistUrl获得response供内部使用
        HttpUtil.sendOkHttpRequest(playlistUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText = Objects.requireNonNull(response.body()).string();
//                final A_Playlist a_playlist = Utility.handleA_PlayistToObject(responseText);
                final A_Playlist a_playlist = Utility.handleJsonToObject(responseText, A_Playlist.class, false);
                Log.d("GOOD", "Get Playlist Object Successful!");
                Log.d("GOOD", "Here is the response Text: " + responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (a_playlist.getCode() == 200) {
                            showPlaylistInfo(a_playlist);
                            showPlaylistItem(a_playlist);
                        } else {
                            Toast.makeText(crack_playlist.this, "歌单状态码错误", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(crack_playlist.this, "获取歌单信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 使用Glide展示传入链接的图片
     * 以及歌单名称和创建者名称
     * */
    private void showPlaylistInfo(A_Playlist a_playlist) {
        final String coverImgUrl = a_playlist.getPlaylist().getCoverImgUrl() + "?param=200y200";
        final String creatorAvatarUrl = a_playlist.getPlaylist().getCreator().getAvatarUrl() + "?param=64y64";

        // 显示歌单封面图 并用Glide-Transformation转换圆角
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GlideApp.with(crack_playlist.this)
                        .load(coverImgUrl)
                        .transform(new RoundedCornersTransformation(25, 0, RoundedCornersTransformation.CornerType.ALL))
                        .error(R.drawable.album_loading_img)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(playlistCoverImage);
            }
        });

        // 显示创建者头像 并用Glide-Transformation转换圆形
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GlideApp.with(crack_playlist.this)
                        .load(creatorAvatarUrl)
                        .apply(circleCropTransform())
                        .error(R.drawable.album_loading_img)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(playlistOwnerAvatarImage);
            }
        });

        // 显示歌单名称
        playlistNameText.setText(a_playlist.getPlaylist().getName());

        // 显示歌单创建者名称
        playlistOwnerText.setText(a_playlist.getPlaylist().getCreator().getNickname());
    }

}
