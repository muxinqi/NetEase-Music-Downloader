package com.mtools.android.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mtools.android.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 组件初始化
        Toolbar toolbar = findViewById(R.id.toolbar);
        Button button = findViewById(R.id.submit_url_button);
        Button favourite_list_button = findViewById(R.id.favour_list_button);
        TextView likeTextView = findViewById(R.id.recommend_to_friends_textview);
        // 配置Toolbar - 主活动不显示返回键
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        // 监听点击操作
        button.setOnClickListener(this);
        likeTextView.setOnClickListener(this);
        favourite_list_button.setOnClickListener(this);
        // 接收系统广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }


    @Override
    public void onClick(View v) {
        EditText editText = findViewById(R.id.url_input);
        String strEditText = editText.getText().toString();
        switch (v.getId()) {
            case R.id.submit_url_button: {
                String strMusicId = getMusicId(strEditText);
                boolean isList = isPlaylist(strEditText);
                // 如果链接错误 Toast提示
                if (strMusicId.compareTo("0") == 0) {
                    Toast.makeText(MainActivity.this, "链接格式错误！", Toast.LENGTH_SHORT).show();
                }
                // 链接正确 是单曲 -> 进入crack_music
                else if (!isList){
                    Intent intent = new Intent(MainActivity.this, crack_music.class);
                    intent.putExtra("extra_data", strMusicId);
                    Toast.makeText(MainActivity.this, "链接类型：单曲", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                // 链接正确 是歌单 -> 进入crack_playlist
                else {
                    Intent intent = new Intent(MainActivity.this, crack_playlist.class);
                    intent.putExtra("extra_data", strMusicId);
                    Toast.makeText(MainActivity.this, "链接类型：歌单", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                }
                break;
            }
            case R.id.recommend_to_friends_textview: {
                Intent intent = new Intent(MainActivity.this, RecommendToFriends.class);
                startActivity(intent);
                break;
            }
            case R.id.favour_list_button: {
                Intent intent = new Intent(MainActivity.this, FavouriteListActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }


    /**
     * 判断是否为歌单
     * 返回值类型 - boolean
     * */
    public boolean isPlaylist(String strEditText) {
        String pattern = ".*playlist.*";
        return Pattern.matches(pattern, strEditText);
    }

    /**
     * 获取链接中的id
     * 先判断URL是否为网易云链接并且其中是否含有参数id
     * 如果满足 则返回 字符串型id
     * 若不满足 则返回 "0"
     */
    public String getMusicId(String strEditText) {
        try {
            URL url = new URL(strEditText);
            String targetHost = "music.163.com";
            String targetPath = "/song";
            String targetPlaylistPath = "/playlist";
            // 识别URL中id参数
            String pattern = "([?|&]id=)(\\d+)";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(url.toString());
            // 判断输入的Host和Path是否正确
            if (url.getHost().compareToIgnoreCase(targetHost) == 0
                    && (url.getPath().compareToIgnoreCase(targetPath) == 0
                    || url.getPath().compareToIgnoreCase(targetPlaylistPath) == 0)
                    && m.find()) {
                return m.group(2);
            } else {
                Toast.makeText(MainActivity.this, url.getPath(), Toast.LENGTH_SHORT).show();
                throw new IOException("Parameter 'id' didn't exist in URL!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "0";
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }


    class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

            /*
             * android.net.NetworkInfo was deprecated in API level 29.
             * 需要以下权限
             *     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
             *     <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
             *     <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
             *     <uses-permission android:name="android.permission.INTERNET" />
             */
            Objects.requireNonNull(connectivityManager).requestNetwork(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NotNull Network network) {
                    super.onAvailable(network);
                    Toast.makeText(MainActivity.this, "网络可用", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLost(@NotNull Network network) {
                   super.onLost(network);
                    Toast.makeText(MainActivity.this, "网络断开", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
